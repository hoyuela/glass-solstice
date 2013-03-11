/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.atm;

import java.util.List;

import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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

	/**
	 * 
	 * @param map - map to be used
	 * @param adapter - adatper to attach to the map
	 */
	public DiscoverMapWrapper(final GoogleMap map, final DiscoverInfoWindowAdapter adapter){
		this.map = map;
		this.adapter = adapter;
		map.setInfoWindowAdapter(adapter);
		map.setOnInfoWindowClickListener(adapter.getInfoWindowClickListener());
		setupMap();
	}

	/**
	 * Set up the map
	 */
	private void setupMap(){
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		final UiSettings settings = map.getUiSettings();
		settings.setCompassEnabled(false);
		settings.setRotateGesturesEnabled(false);
		settings.setTiltGesturesEnabled(false);
		settings.setZoomControlsEnabled(false);
	}

	/**
	 * Set the users current location on the map
	 * @param location - location of the user
	 * @param drawable - drawable to pin on the map
	 */
	public void setUsersCurrentLocation(final Location location, final int drawable){
		this.location = location;
		if(null != currentMarker){
			currentMarker.remove();
		}
		final LatLng item = new LatLng(location.getLatitude(), location.getLongitude());
		currentMarker = map.addMarker(new MarkerOptions().position(item)
				.icon(BitmapDescriptorFactory.fromResource(drawable)));

	}

	/**
	 * Add a location object to the map
	 * @param objects - location object to the map
	 */
	public void addObjectsToMap(final List<? extends LocationObject> objects){
		for(final LocationObject object : objects){
			object.setDistanceFromUser(getDistanceFromUser(object));
			adapter.addMarkerAndAtm(map.addMarker(createMapMarker(object)), object);
		}
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
	 * Create the marker for the map
	 * @param object - to create the marker for
	 * @return the marker to be added to the map
	 */
	private MarkerOptions createMapMarker(final LocationObject object){
		final LatLng item = new LatLng(object.getLatitude(),object.getLongitude());	

		return new MarkerOptions().position(item)
				.icon(BitmapDescriptorFactory.fromResource(object.getPinDrawable()));
	}

	/**
	 * Zoom to a location
	 * @param location - location to zoom to
	 * @param zoomLevel - level to zoom to
	 */
	public void zoomToLocation(final Location location, final float zoomLevel){
		final LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
		map.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, zoomLevel));
	}

	/**
	 * Focus the camera on a location
	 * @param latitude
	 * @param longitude
	 */
	public void focusCameraOnLocation(final Double latitude, final Double longitude){
		map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
	}

	public void focusCameraOnLocation(final Double latitude, final Double longitude, final float zoomLevel){
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(
				new LatLng(location.getLatitude(), location.getLongitude()), zoomLevel));
	}

	/**
	 * @return the location
	 */
	public Location getCurrentLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setCurrentLocation(final Location location) {
		this.location = location;
	}

}
