/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.atm;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.R;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.nav.NavigationRootActivity;
import com.discover.mobile.common.ui.modals.ModalAlertWithTwoButtons;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.slidingmenu.lib.SlidingMenu;

public class AtmMapFragment extends BaseFragment implements LocationFragment, OnMyLocationChangeListener{

	private int locationStatus = NOT_ENABLED;

	private Location location;

	private LocationManager manager;

	private ModalAlertWithTwoButtons locationModal;

	private ModalAlertWithTwoButtons settingsModal;

	private GoogleMap map;

	private final float MAP_CURRENT_LOCATION_ZOOM = 17f;

	private final Double MAP_CENTER_LAT = 37.88;

	private final Double MAP_CENTER_LONG = -98.21;

	private DiscoverGpsStatusListener gpsStatusListener;

	/**
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState){
		final View view = inflater.inflate(R.layout.bank_atm_map, null);

		map = ((SupportMapFragment) this.getActivity().getSupportFragmentManager().findFragmentById(R.id.discover_map)).getMap();

		disableMenu();
		createSettingsModal();
		createLocationModal();
		manager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
		gpsStatusListener = new DiscoverGpsStatusListener(this);

		if(null != savedInstanceState){
			locationStatus = savedInstanceState.getInt(LOCATION_STATUS, locationStatus);
			if(LOCKED_ON == locationStatus){
				location = new Location(LocationManager.GPS_PROVIDER);
				location.setLatitude(savedInstanceState.getDouble(LAT_KEY));
				location.setLongitude(savedInstanceState.getDouble(LONG_KEY));
			}
		}
		return view;
	}

	/**
	 * Set up the map
	 */
	private void setupMap(){
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		final UiSettings settings = map.getUiSettings();
		settings.setMyLocationButtonEnabled(false);
		settings.setCompassEnabled(false);
		settings.setRotateGesturesEnabled(false);
		settings.setTiltGesturesEnabled(false);
		settings.setZoomControlsEnabled(false);
	}

	/**
	 * Disable the sliding menu
	 */
	private void disableMenu(){
		((NavigationRootActivity)this.getActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
	}

	/**
	 * Enable the sliding menu
	 */
	private void enableMenu(){
		((NavigationRootActivity)this.getActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
	}

	/**
	 * Resume the fragment
	 */
	@Override
	public void onResume(){
		super.onResume();
		setupMap();

		if(NOT_ENABLED == locationStatus){
			locationStatus = (areProvidersenabled()) ? ENABLED : NOT_ENABLED;
		}

		if(NOT_ENABLED == locationStatus){
			settingsModal.show();
		}else if(ENABLED == locationStatus){
			locationModal.show();
		}else if(SEARCHING == locationStatus){
			getLocation();
		}else if(LOCKED_ON == locationStatus){
			getLocation();
		}

		if(LOCKED_ON != locationStatus){
			map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(MAP_CENTER_LAT, MAP_CENTER_LONG)));
		}
	}

	private boolean areProvidersenabled(){
		return manager.isProviderEnabled(LocationManager.GPS_PROVIDER) 
				&& manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}

	/**
	 * Create the would you like to enable the current location modal
	 */
	private void createSettingsModal(){
		settingsModal = AtmModalFactory.getSettingsModal(getActivity(), this);
	}

	/**
	 * Create the would you like to use the current location modal
	 */
	private void createLocationModal(){
		locationModal = AtmModalFactory.getLocationAcceptanceModal(getActivity(), this);
	}

	/**
	 * Get the users current location
	 */
	@Override
	public void getLocation(){
		map.setMyLocationEnabled(true);
		map.setOnMyLocationChangeListener(this);
		manager.addGpsStatusListener(gpsStatusListener);
	}

	/**
	 * Set the users location on the map.
	 */
	@Override
	public void setUserLocation(final Location location){
		locationStatus = LOCKED_ON;
		this.location = (null == map.getMyLocation()) ? location : map.getMyLocation();
		zoomToLocation(this.location, MAP_CURRENT_LOCATION_ZOOM);
		manager.removeGpsStatusListener(gpsStatusListener);
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
	 * Save the state of the fragment
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState){
		if(locationModal.isShowing()){
			locationModal.dismiss();
		} else if(settingsModal.isShowing()){
			settingsModal.dismiss();
		}
		outState.putInt(LOCATION_STATUS, locationStatus);
		if(LOCKED_ON == locationStatus){
			outState.putDouble(LAT_KEY, location.getLatitude());
			outState.putDouble(LONG_KEY, location.getLongitude());
		}
		enableMenu();
		map.setMyLocationEnabled(false);
		manager.removeGpsStatusListener(gpsStatusListener);
		super.onSaveInstanceState(outState);
	}

	/**
	 * Show that either the location could not be retrieved. 
	 */
	@Override
	public void showNoLocation() {
		locationStatus = NOT_USING_LOCATION;
		map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(MAP_CENTER_LAT, MAP_CENTER_LONG)));
	}

	/**
	 * Launch the settings activity so that the user can turn the location services on.
	 */
	@Override
	public void launchSettings(){
		startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
	}

	/**
	 * @return the locationStatus
	 */
	public int getLocationStatus() {
		return locationStatus;
	}

	/**
	 * @param locationStatus the locationStatus to set
	 */
	@Override
	public void setLocationStatus(final int locationStatus) {
		this.locationStatus = locationStatus;
	}

	/**
	 * Handle the timeout of the listeners.  Meaning that the listeners
	 * were unable to get the location in a reasonable amount of time.
	 */
	@Override
	public void handleTimeOut() {
		map.setMyLocationEnabled(false);
		manager.removeGpsStatusListener(gpsStatusListener);
		//TODO: Show modal
		showNoLocation();
	}

	@Override
	public int getActionBarTitle() {
		return R.string.atm_locator_title;
	}

	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.ATM_LOCATOR_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.SEARCH_BY_LOCATION;
	}

	@Override
	public void setLocation(final Location location) {
		this.location = location;
	}

	@Override
	public void onMyLocationChange(final Location location) {
		if(null != location && location.getProvider().equals(LocationManager.GPS_PROVIDER)){
			setUserLocation(location);
		}
	}
}
