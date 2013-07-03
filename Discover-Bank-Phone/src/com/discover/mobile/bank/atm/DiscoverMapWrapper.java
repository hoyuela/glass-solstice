/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.atm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;

import com.discover.mobile.bank.R;
import com.discover.mobile.common.DiscoverActivityManager;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.twotoasters.clusterkraf.Clusterkraf;
import com.twotoasters.clusterkraf.InputPoint;
import com.twotoasters.clusterkraf.Options;

/**
 * Wrapper to go around maps so that the funcationality can be ecapsulated from the UI.
 * @author jthornton
 *
 */
public class DiscoverMapWrapper {

	/**Map that the wrapper is to use*/
	private final GoogleMap map;

	/**Users current location marker*/
	private Marker currentMarker;

	/**Users current location*/
	private Location location;

	/**Info window adapter*/
	private final DiscoverInfoWindowAdapter adapter;

	/**Conversion to convert to miles*/
	private static final double MILES_PER_KILOMETER = 0.000621371;

	/**Address of the users current location*/
	private static String addressString;

	/**Clustering library reference**/
	private Clusterkraf clusterkraf;
	
	private final int zoomLevel;

	/**
	 * 
	 * @param map - map to be used
	 * @param adapter - adatper to attach to the map
	 */
	public DiscoverMapWrapper(final GoogleMap map, final DiscoverInfoWindowAdapter adapter){
		this.map = map;
		this.adapter = adapter;
		if(null != map){
			map.setInfoWindowAdapter(adapter);
			map.setOnInfoWindowClickListener(adapter.getInfoWindowClickListener());
		}
		this.zoomLevel = DiscoverActivityManager.getActiveActivity().getResources().getDimensionPixelSize(R.dimen.atm_padding_from_edge_map);
		setupMap();
	}

	/**
	 * Set up the map
	 */
	private void setupMap(){
		if(null != map){
			map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			final UiSettings settings = map.getUiSettings();
			settings.setCompassEnabled(false);
			settings.setRotateGesturesEnabled(false);
			settings.setTiltGesturesEnabled(false);
			settings.setZoomControlsEnabled(false);
		}
	}

	/**
	 * Clear the map of all markers
	 */
	public void clear(){
		if(null != map){
			map.clear();
		}
		if (null != clusterkraf){
			clusterkraf.clear();
			clusterkraf = null;
		}
	}

	/**
	 * Set the users current location on the map
	 * @param location - location of the user
	 * @param drawable - drawable to pin on the map
	 */
	public void setUsersCurrentLocation(final Location location, final int drawable, final Context context){
		if(null != map){
			if(null == location){return;}
			this.location = location;
			if(null != currentMarker){
				currentMarker.remove();
			}
			final LatLng item = new LatLng(location.getLatitude(), location.getLongitude());
			currentMarker = map.addMarker(new MarkerOptions().position(item)
					.icon(BitmapDescriptorFactory.fromResource(drawable)));
			createAddressString(context);
		}
	}

	/**
	 * Create address string
	 * @param context - activity context
	 */
	public void createAddressString(final Context context){
		if (location == null) {
			addressString = "";
			return;
		}

		final Geocoder coder = new Geocoder(context);
		final String spaceCommaSpace = " , ";
		final String commaSpace = ", ";

		try {
			final List<Address> addresses = coder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
			if(null == addresses || addresses.isEmpty()){
				addressString =  location.getLatitude() + spaceCommaSpace + location.getLongitude();
				return;
			}
			final Address address = addresses.get(0);
			adapter.setCurrentLocation(address);
			addressString =  address.getFeatureName() + " " + address.getAddressLine(0) +commaSpace 
					+ address.getLocality() +commaSpace + address.getAdminArea() + commaSpace + address.getCountryName();
		} catch (final IOException e) {
			addressString =  location.getLatitude() + spaceCommaSpace + location.getLongitude();
		}
	}

	/**
	 * Add a location object to the map
	 * @param objects - location object to the map
	 */
	public void addObjectsToMap(final List<? extends LocationObject> objects){
		if(null != map){
			//clusterkraf options
			final Options options = new Options();
			options.setTransitionInterpolator(new OvershootInterpolator());
			options.setPixelDistanceToJoinCluster(convertToDensityIndependentPixels());
			options.setZoomToBoundsPadding(zoomLevel);
			final ArrayList<InputPoint> list = new ArrayList<InputPoint>();
			for(final LocationObject object : objects){
				options.setMarkerOptionsChooser(
						new AtmClusterMarker(DiscoverActivityManager.getActiveActivity().getApplicationContext(), 
								object.getPinDrawable()));
				object.setDistanceFromUser(getDistanceFromUser(object));
				list.add(new InputPoint(new LatLng(object.getLatitude(), object.getLongitude()), object));
				adapter.addAtmToList(object);
			}
			clusterkraf = new Clusterkraf(map, options, list);
			map.setOnInfoWindowClickListener(adapter.getInfoWindowClickListener());
		}
	}

	/**
	 * converts the pixel calculation to density independent 
	 * pixels.  used for clustering 
	 */
	private int convertToDensityIndependentPixels(){
		final DisplayMetrics disMetrics = new DisplayMetrics();
		final WindowManager windowManager = 
				(WindowManager) DiscoverActivityManager.getActiveActivity().getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(disMetrics);
		return Math.round(disMetrics.density * 100);
	}
	/**
	 * Get the distance of an object from the user
	 * @param detail - detail to get distance to
	 * @return - the distance of an object from the user
	 */
	private double getDistanceFromUser(final LocationObject detail){
		final Location atmLocation = new Location(LocationManager.GPS_PROVIDER);
		atmLocation.setLatitude(detail.getLatitude());
		atmLocation.setLongitude(detail.getLongitude());
		final double distance = location.distanceTo(atmLocation);

		return distance * MILES_PER_KILOMETER;

	}

	/**
	 * Zoom to a location
	 * @param location - location to zoom to
	 * @param zoomLevel - level to zoom to
	 */
	public void zoomToLocation(final Location location, final float zoomLevel){
		if(null != map){
			final LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
			map.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, zoomLevel));
		}
	}

	/**
	 * Animate a camera change from the current camera view to a new one.
	 * @param newZoom a CameraUpdate object.
	 */
	public final void animateZoomChange(final CameraUpdate newZoom ) {
		if(map != null) {
			map.animateCamera(newZoom);
		}
	}

	/**
	 * 
	 * @return the GoogleMap in use.
	 */
	public final GoogleMap getMap() {
		return map;
	}

	public final float getCurrentMapZoom() {
		float zoomLevel = 0;
		if(null != map){
			zoomLevel = map.getCameraPosition().zoom;
		}
		return zoomLevel;
	}

	/**
	 * Focus the camera on a location
	 * @param latitude
	 * @param longitude
	 */
	public void focusCameraOnLocation(final Double latitude, final Double longitude){
		if(null != map){
			map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
		}
	}

	/**
	 * Focus the camera on a location
	 * @param latitude
	 * @param longitude
	 * @param zoomLevel
	 */
	public void focusCameraOnLocation(final Double latitude, final Double longitude, final float zoomLevel){
		if(null != map){
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(
					new LatLng(latitude, longitude), zoomLevel));
		}
	}

	/**
	 * @return the location
	 */
	public Location getCurrentLocation() {
		return location;
	}

	/**
	 * Return the current camera location.
	 * @return a LatLng object that represents the current camera location.
	 */
	public LatLng getCameraLocation() {
		LatLng cameraPosition = null;

		if(map != null) {
			cameraPosition = map.getCameraPosition().target;
		}

		return cameraPosition;
	}

	/**
	 * @param location the location to set
	 */
	public void setCurrentLocation(final Location location) {
		this.location = location;
	}

	/**
	 * 
	 * @return the string representing the address of the current location
	 */
	public String getGetAddressString(){
		return addressString;

	}

}
