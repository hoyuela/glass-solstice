/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.atm;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.services.atm.AtmDetail;
import com.discover.mobile.bank.services.atm.AtmResults;
import com.discover.mobile.bank.services.atm.AtmServiceHelper;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.nav.NavigationRootActivity;
import com.discover.mobile.common.ui.modals.ModalAlertWithTwoButtons;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.slidingmenu.lib.SlidingMenu;

/**
 * Main fragment of the ATM locator. Will display a map and do searches based
 * off the users current location or off a location entered by the user.
 * 
 * @author jthornton
 *
 */
public class AtmMapFragment extends BaseFragment implements LocationFragment{

	/**
	 * Location status of the fragment. Is set based off of user input and the ability
	 * to get the users location.  Defaults to NOT_ENABLED.
	 */
	private int locationStatus = NOT_ENABLED;

	/**Key to get the state of the buttons*/
	private static final String BUTTON_KEY = "buttonState";

	/**Users current location*/
	private Location location;

	/** Location manager of the application*/
	private LocationManager manager;

	/**Modal that asks the user if the app can use their current location*/
	private ModalAlertWithTwoButtons locationModal;

	/**Modal that lets the user know that their location services are disabled*/
	private ModalAlertWithTwoButtons settingsModal;

	/**Google map instance*/
	private GoogleMap map;

	/**GPS status listener to attach to the location manager when trying to the users current location*/
	private DiscoverGpsStatusListener gpsStatusListener;

	/**Location listener to attach to the location manager when trying to get the users current location*/
	private DiscoverLocationListener gpsListener, networkListener;

	/**Boolean set to true when the app has loaded the atms to that the app does not trigger the call more than one time*/
	private boolean hasLoadedAtms = false;

	/**Amount of ATMs that can be shown at one time*/
	private static final int INDEX_INCREMENT = 10;

	/**current index of the next atm that needs to be displayed*/
	private int currentIndex = 0;

	/**Atms close to the users location*/
	private AtmResults results;

	/**Button that is used to show the map view*/
	private Button mapButton;

	/**Button that is used to show the list view*/
	private Button listButton;

	/**Boolean that is true if the map is showing*/
	private boolean isOnMap = true;

	/**Current location marker*/
	private Marker currentLocMarker;

	/**
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState){
		final View view = inflater.inflate(R.layout.bank_atm_map, null);

		map = ((SupportMapFragment) this.getActivity().getSupportFragmentManager().findFragmentById(R.id.discover_map)).getMap();
		mapButton = (Button) view.findViewById(R.id.map_nav);
		listButton = (Button) view .findViewById(R.id.list_nav);

		mapButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				if(!isOnMap) {
					toggleButton();
				}
			}
		});
		listButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				if(isOnMap) {
					toggleButton();
				}
			}
		});

		disableMenu();
		createSettingsModal();
		createLocationModal();
		manager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
		gpsStatusListener = new DiscoverGpsStatusListener(this);
		gpsListener = new DiscoverLocationListener(this);
		networkListener = new DiscoverLocationListener(this);

		if(null != savedInstanceState){
			resumeStateOfFragment(savedInstanceState);
		}
		return view;
	}

	/**
	 * Toggle Between the buttons
	 */
	private void toggleButton(){
		if(isOnMap){
			mapButton.setBackgroundResource(R.drawable.atm_pinview_button);
			listButton.setBackgroundResource(R.drawable.atm_listview_button_ds);
			isOnMap = false;
		}else{
			mapButton.setBackgroundResource(R.drawable.atm_pinview_button_ds);
			listButton.setBackgroundResource(R.drawable.atm_list_view_button);
			isOnMap = true;
		}
	}

	/**
	 * Resume the state of the fragment
	 * @param savedInstanceState - bundle holding the state of the fragment
	 */
	private void resumeStateOfFragment(final Bundle savedInstanceState) {
		locationStatus = savedInstanceState.getInt(LOCATION_STATUS, locationStatus);
		if(LOCKED_ON == locationStatus){
			location = new Location(LocationManager.GPS_PROVIDER);
			location.setLatitude(savedInstanceState.getDouble(LAT_KEY));
			location.setLongitude(savedInstanceState.getDouble(LONG_KEY));
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(
					new LatLng(location.getLatitude(), location.getLongitude()), MAP_CURRENT_GPS_ZOOM));
		}
		results = (AtmResults)savedInstanceState.getSerializable(BankExtraKeys.DATA_LIST_ITEM);
		currentIndex = savedInstanceState.getInt(BankExtraKeys.DATA_SELECTED_INDEX, 0);
		if(null != results){
			addAtmsToMap(results.results.atms.subList(0, currentIndex));
			hasLoadedAtms = true;
		}
		isOnMap = !savedInstanceState.getBoolean(BUTTON_KEY, true);
		toggleButton();
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
	 * Disable the sliding menu
	 */
	private void disableMenu(){
		((NavigationRootActivity)this.getActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
	}

	/**
	 * Enable the sliding menu
	 */
	private void enableMenu(){
		((NavigationRootActivity)this.getActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
	}

	/**
	 * Resume the fragment
	 */
	@Override
	public void onResume(){
		super.onResume();
		setupMap();
		((NavigationRootActivity)this.getActivity()).setCurrentFragment(this);

		if(NOT_ENABLED == locationStatus){
			locationStatus = (areProvidersenabled()) ? ENABLED : NOT_ENABLED;
		}

		if(NOT_ENABLED == locationStatus){
			settingsModal.show();
		}else if(ENABLED == locationStatus){
			locationModal.show();
		}else if(SEARCHING == locationStatus){
			getLocation();
		}

		if(LOCKED_ON != locationStatus){
			map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(MAP_CENTER_LAT, MAP_CENTER_LONG)));
		}
	}

	/**
	 * Handle the ATMs that are received from the services and display them on the map.
	 * @param bundle - bundle
	 */
	public void handleRecievedAtms(final Bundle bundle){
		results = (AtmResults)bundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM);

		addAtmsToMap(results.results.atms.subList(currentIndex, INDEX_INCREMENT));
		currentIndex += INDEX_INCREMENT;
	}

	private void addAtmsToMap(final List<AtmDetail> atms){
		for(final AtmDetail atm : atms){
			map.addMarker(createMapMarker(atm));
		}
	}

	/**
	 * Create the marker for the map
	 * @param atm - to create the marker for
	 * @return the marker to be added to the map
	 */
	private MarkerOptions createMapMarker(final AtmDetail atm){
		final LatLng item = new LatLng(Double.parseDouble(atm.latitude), Double.parseDouble(atm.longitude));
		final int drawable = (atm.isAtmSearchargeFree()) ? R.drawable.atm_orange_pin : R.drawable.atm_gray_pin;	

		return new MarkerOptions().position(item)
				.icon(BitmapDescriptorFactory.fromResource(drawable));

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
		manager.addGpsStatusListener(gpsStatusListener);
		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsListener);
		manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, networkListener);
	}

	private void removeListeners(){
		manager.removeGpsStatusListener(gpsStatusListener);
		manager.removeUpdates(gpsListener);
		manager.removeUpdates(networkListener);
	}

	/**
	 * Set the users location on the map.
	 */
	@Override
	public void setUserLocation(final Location location){
		removeListeners();
		locationStatus = LOCKED_ON;
		this.location = location;
		if(null != currentLocMarker){
			currentLocMarker.remove();
		}
		final LatLng item = new LatLng(location.getLatitude(), location.getLongitude());
		currentLocMarker = map.addMarker(new MarkerOptions().position(item)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.atm_starting_point_pin)));
		if(LocationManager.GPS_PROVIDER == location.getProvider()){
			zoomToLocation(this.location, MAP_CURRENT_GPS_ZOOM);
		}else{
			zoomToLocation(this.location, MAP_CURRENT_NETWORK_ZOOM);
		}
		manager.removeGpsStatusListener(gpsStatusListener);
		if(!hasLoadedAtms){
			getAtms(location);
			hasLoadedAtms = true;
		}
	}

	/**
	 * Get atms near location
	 * @param location - location to get atms near
	 */
	private void getAtms(final Location location){
		final AtmServiceHelper helper = new AtmServiceHelper(location);
		helper.setSurchargeFree(false);
		BankServiceCallFactory.createGetAtmServiceCall(helper).submit();
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
		removeListeners();
		manager.removeGpsStatusListener(gpsStatusListener);
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
		if(results != null){
			outState.putSerializable(BankExtraKeys.DATA_LIST_ITEM, results);
		}
		outState.putInt(BankExtraKeys.DATA_SELECTED_INDEX, currentIndex);
		outState.putBoolean(BUTTON_KEY, isOnMap);


		super.onSaveInstanceState(outState);
	}

	@Override
	public void onPause(){
		super.onPause();
		final Fragment fragment = (getActivity().getSupportFragmentManager().findFragmentById(R.id.discover_map));  
		final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
		ft.remove(fragment);
		ft.commit();
		enableMenu();
	}

	/**
	 * Show that either the location could not be retrieved. 
	 */
	@Override
	public void showNoLocation() {
		removeListeners();
		locationStatus = NOT_USING_LOCATION;
		map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(MAP_CENTER_LAT, MAP_CENTER_LONG)));
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(MAP_CENTER_LAT, MAP_CENTER_LONG), map.getMaxZoomLevel()));
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
		manager.removeGpsStatusListener(gpsStatusListener);
		if(null == location){
			//TODO: Show modal
			showNoLocation();
		}
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
}
