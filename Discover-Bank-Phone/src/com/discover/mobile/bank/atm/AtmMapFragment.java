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
import android.widget.Toast;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.R;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.ui.modals.ModalAlertWithTwoButtons;

public class AtmMapFragment extends BaseFragment implements LocationFragment{

	private int locationStatus = NOT_ENABLED;

	private Location location;

	private LocationManager manager;

	private DiscoverLocationListener networkListener, gpsListener;

	private DiscoverGpsStatusListener gpsStatusListener;

	private ModalAlertWithTwoButtons locationModal;

	private ModalAlertWithTwoButtons settingsModal;

	/**
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {	
		final View view = inflater.inflate(R.layout.bank_atm_map, null);

		createSettingsModal();
		createLocationModal();
		manager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);	
		networkListener = new DiscoverLocationListener(this);
		gpsListener = new DiscoverLocationListener(this);
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
	 * Resume the fragment
	 */
	@Override
	public void onResume(){
		super.onResume();

		if(NOT_ENABLED == locationStatus){
			locationStatus = (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) ? ENABLED : NOT_ENABLED;
		}

		if(NOT_ENABLED == locationStatus){
			settingsModal.show();
		}else if(ENABLED == locationStatus){
			locationModal.show();
		}else if(SEARCHING == locationStatus){
			getLocation();
		}else if(LOCKED_ON == locationStatus){
			//TODO: Place the user on the map
		}else{
			//TODO: Show default user map with search
		}
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
	 * Get the users current locaiton
	 */
	@Override
	public void getLocation(){
		manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, networkListener);
		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsListener);
		manager.addGpsStatusListener(gpsStatusListener);
	}

	/**
	 * Set the users location on the map.
	 */
	@Override
	public void setUserLocation(final Location location){
		removeListeners();
		locationStatus = LOCKED_ON;
		this.location = location;
		final String locationString = "Retrieved Location and the Latitude is " + location.getLatitude() + 
				" and Longitude is " + location.getLongitude();
		Toast.makeText(getActivity(), locationString, 5000).show();
	}

	/**
	 * Remove all the listeners that are currently lookin for location.
	 */
	private void removeListeners() {
		manager.removeUpdates(networkListener);
		manager.removeUpdates(gpsListener);
		manager.removeGpsStatusListener(gpsStatusListener);
	}

	/**
	 * Save the state of the fragment
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState){
		removeListeners();
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
		super.onSaveInstanceState(outState);	
	}

	/**
	 * Show that either the location could not be retrieved. 
	 */
	@Override
	public void showNoLocation() {
		locationStatus = NOT_USING_LOCATION;
		//TODO: show a modal saying we could not retrieve your location
		//TODO: display a default map
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
		removeListeners();
		showNoLocation();
	}

	@Override
	public int getActionBarTitle() {
		return R.string.atm_locator_title;
	}

	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.SEARCH_BY_LOCATION;
	}

	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.ATM_LOCATOR_GROUP;
	}
}
