/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.atm;

import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.DynamicDataFragment;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.help.HelpMenuListFactory;
import com.discover.mobile.bank.services.atm.AddressToLocationDetail;
import com.discover.mobile.bank.services.atm.AddressToLocationResultDetail;
import com.discover.mobile.bank.services.atm.AtmResults;
import com.discover.mobile.bank.services.atm.AtmServiceHelper;
import com.discover.mobile.bank.util.FragmentOnBackPressed;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.help.HelpWidget;
import com.discover.mobile.common.nav.NavigationRootActivity;
import com.discover.mobile.common.ui.modals.ModalAlertWithTwoButtons;
import com.google.android.gms.maps.SupportMapFragment;
import com.slidingmenu.lib.SlidingMenu;

/**
 * Main fragment of the ATM locator. Will display a map and do searches based
 * off the users current location or off a location entered by the user.
 * 
 * @author jthornton
 *
 */
public abstract class AtmMapFragment extends BaseFragment 
implements LocationFragment, AtmMapSearchFragment, FragmentOnBackPressed, DynamicDataFragment{

	/**
	 * Location status of the fragment. Is set based off of user input and the ability
	 * to get the users location.  Defaults to NOT_ENABLED.
	 */
	private int locationStatus = NOT_ENABLED;

	/**Key to get the state of the buttons*/
	private static final String BUTTON_KEY = "buttonState";

	/**Private static key for the street view showing*/
	private static final String STREET_VIEW_SHOWING = "svs";

	/**Modal that asks the user if the app can use their current location*/
	private ModalAlertWithTwoButtons locationModal;

	/**Modal that lets the user know that their location services are disabled*/
	private ModalAlertWithTwoButtons settingsModal;

	/**Boolean set to true when the app has loaded the atms to that the app does not trigger the call more than one time*/
	private boolean hasLoadedAtms = false;

	/**Amount of ATMs that can be shown at one time*/
	private static final int INDEX_INCREMENT = 10;

	/**Maximum Amount of loads that the app can do*/
	private static final int MAX_LOADS = 30;

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

	/**Wrapper around the map*/
	private DiscoverMapWrapper mapWrapper;

	/**Wrapper for the location manager*/
	private DiscoverLocationMangerWrapper locationManagerWrapper;

	/**View of the layout*/
	private View view;

	/**Search bar of the fragment*/
	private AtmLocatorMapSearchBar searchBar;

	/**Support map fragment*/
	private SupportMapFragment fragment;

	/**Support map fragment*/
	private AtmListFragment  listFragment;

	/**Street view framgent*/
	private AtmWebView streetView;

	/**Boolean that is false if the app should allow the back button press*/
	private boolean shouldGoBack = false;

	/**Panel containing the buttons*/
	private LinearLayout navigationPanel;

	/**Boolean set to true if the device is in landscape and the list is showing*/
	private boolean isListLand = false;

	/**Boolean true when the fragment is loading*/
	private boolean isLoading = false;

	/**Help Widget*/
	private HelpWidget help;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState){
		//Check to see if the view has already been inflated
		if(null == view){
			view = inflater.inflate(getLayout(), null);
		}else{
			//Remove the view from its current parent so that it can be attached to the new parent
			final ViewGroup parent = (ViewGroup)(view.getParent());
			parent.removeView(view);
		}

		final NavigationRootActivity activity = (NavigationRootActivity)this.getActivity();
		final WebView web = (WebView) view.findViewById(R.id.web_view);
		final ProgressBar bar = (ProgressBar) view.findViewById(R.id.progress_bar);
		streetView = new AtmWebView(web, bar);
		mapButton = (Button) view.findViewById(R.id.map_nav);
		listButton = (Button) view .findViewById(R.id.list_nav);
		help = (HelpWidget) view.findViewById(R.id.help);
		help.showHelpItems(HelpMenuListFactory.instance().getAtmHelpItems());
		navigationPanel = (LinearLayout) view.findViewById(R.id.map_navigation_panel);
		fragment =  (SupportMapFragment) activity.getSupportFragmentManager().findFragmentById(getMapFragmentId());
		listFragment =  (AtmListFragment) activity.getSupportFragmentManager().findFragmentById(getListFragmentId());
		listFragment.setObserver(this);

		this.getActivity().getSupportFragmentManager().beginTransaction().hide(listFragment).commitAllowingStateLoss();
		streetView.hide();

		final AtmMarkerBalloonManager balloon = new AtmMarkerBalloonManager(this);
		final DiscoverInfoWindowAdapter adapter = new DiscoverInfoWindowAdapter(balloon);
		mapWrapper = new DiscoverMapWrapper(fragment.getMap(), adapter);
		locationManagerWrapper = new DiscoverLocationMangerWrapper(this);
		searchBar = (AtmLocatorMapSearchBar) view.findViewById(R.id.full_search_bar);
		searchBar.setFragment(this);

		setUpListeners();
		disableMenu();
		createSettingsModal();
		createLocationModal();
		if(null != savedInstanceState){
			resumeStateOfFragment(savedInstanceState);
		}
		return view;
	}

	/*
	 * Get the layout for the file
	 */
	public abstract int getLayout();

	/**
	 * Gets the map fragment id
	 * @return the map fragment id
	 */
	public abstract int getMapFragmentId();

	/**
	 * Gets the list fragment id
	 * @return the list fragment id
	 */
	public abstract int getListFragmentId();

	/**
	 * @return the current location address string
	 */
	@Override
	public String getCurrentLocationAddress() {
		final String str = mapWrapper.getGetAddressString();
		if(null == str || str.isEmpty()){
			locationModal.show();
		}
		return str;
	}

	/**
	 * Perform the search
	 * @param text - search text
	 */
	@Override
	public void performSearch(final String text) {
		final AtmServiceHelper helper = new AtmServiceHelper(text);
		BankServiceCallFactory.getLocationFromAddressCall(helper).submit();
	}

	/**
	 * Handle a successful address to location response
	 * @param bundle - bundle of data retrieved from the service call
	 */
	public void handleAddressToLocationResponse(final Bundle bundle){
		final AddressToLocationDetail addressResults = (AddressToLocationDetail) bundle.get(BankExtraKeys.DATA_LIST_ITEM);
		if(null == addressResults || null == addressResults.results || addressResults.results.isEmpty()){
			((NavigationRootActivity)this.getActivity()).closeDialog();
			AtmModalFactory.getInvalidAddressModal(this.getActivity()).show();
		}else{
			final AddressToLocationResultDetail address = addressResults.results.get(0);
			final Location location = new Location(LocationManager.GPS_PROVIDER);
			location.setLatitude(address.geometry.endLocation.lat);
			location.setLongitude(address.geometry.endLocation.lon);
			mapWrapper.clear();
			currentIndex = 0;
			mapWrapper.setUsersCurrentLocation(location, R.drawable.atm_starting_point_pin, this.getActivity());
			if(LocationManager.GPS_PROVIDER == location.getProvider()){
				mapWrapper.zoomToLocation(location, MAP_CURRENT_GPS_ZOOM);
			}else{
				mapWrapper.zoomToLocation(location, MAP_CURRENT_NETWORK_ZOOM);
			}
			getAtms(location);
			hasLoadedAtms = true;
		}
	}


	/**
	 * Set up the click listeners
	 */
	private void setUpListeners(){
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
	}

	/**
	 * Toggle Between the buttons
	 */
	private void toggleButton(){
		final boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
		if(isOnMap){
			showList();
			mapButton.setBackgroundResource((isLandscape) ? 
					R.drawable.atm_pinview_button_landscape : R.drawable.atm_pinview_button);
			listButton.setBackgroundResource((isLandscape) ? 
					R.drawable.atm_listview_button_ds_landscape : R.drawable.atm_listview_button_ds);
			isOnMap = false;
		}else{
			showMap();
			mapButton.setBackgroundResource((isLandscape) ? 
					R.drawable.atm_pinview_button_ds_landscape : R.drawable.atm_pinview_button_ds);
			listButton.setBackgroundResource((isLandscape) ? 
					R.drawable.atm_listview_button_landscape : R.drawable.atm_list_view_button);
			isOnMap = true;
		}
	}

	/**
	 * Resume the state of the fragment
	 * @param savedInstanceState - bundle holding the state of the fragment
	 */
	private void resumeStateOfFragment(final Bundle savedInstanceState) {
		locationStatus = savedInstanceState.getInt(LOCATION_STATUS, locationStatus);
		final Double lat = savedInstanceState.getDouble(LAT_KEY);
		final Double lon = savedInstanceState.getDouble(LONG_KEY);
		results = (AtmResults)savedInstanceState.getSerializable(BankExtraKeys.DATA_LIST_ITEM);
		if(null != results){
			hasLoadedAtms = true;
		}
		if(0.0 != lat && 0.0 != lon){
			final Location location = new Location(LocationManager.GPS_PROVIDER);
			location.setLatitude(lat);
			location.setLongitude(lon);
			setUserLocation(location);
		}
		currentIndex = savedInstanceState.getInt(BankExtraKeys.DATA_SELECTED_INDEX, 0);
		if(null != results){
			mapWrapper.addObjectsToMap(results.results.atms.subList(0, currentIndex));
			hasLoadedAtms = true;
		}
		isOnMap = !savedInstanceState.getBoolean(BUTTON_KEY, true);
		searchBar.restoreState(savedInstanceState);
		toggleButton();
		listFragment.handleReceivedData(savedInstanceState);
		streetView.hide();

		shouldGoBack = savedInstanceState.getBoolean(STREET_VIEW_SHOWING, true);
		if(shouldGoBack){
			showStreetView(savedInstanceState);
		}
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
		this.disableMenu();

		if(isOnMap){
			showMap();
		}else{
			showList();
		}

		final NavigationRootActivity activity = ((NavigationRootActivity)this.getActivity());
		activity.setCurrentFragment(this);

		if(NOT_ENABLED == locationStatus){
			locationStatus = (locationManagerWrapper.areProvidersenabled()) ? ENABLED : NOT_ENABLED;
		}

		if(NOT_ENABLED == locationStatus){
			settingsModal.show();
		}else if(ENABLED == locationStatus){
			locationModal.show();
		}else if(SEARCHING == locationStatus){
			getLocation();
		}else if(LOCKED_ON == locationStatus){
			setUserLocation(mapWrapper.getCurrentLocation());
		}

		if(LOCKED_ON != locationStatus){
			mapWrapper.focusCameraOnLocation(MAP_CENTER_LAT, MAP_CENTER_LONG);
		}
	}

	/**
	 * Handle the ATMs that are received from the services and display them on the map.
	 * @param bundle - bundle
	 */
	@Override
	public void handleReceivedData(final Bundle bundle){
		isLoading = true;
		results = (AtmResults)bundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM);
		int endIndex = (currentIndex + INDEX_INCREMENT);
		if(isListEmpty()){
			AtmModalFactory.getNoResultsModal(getActivity());
			endIndex = 0;
		}else if(endIndex > results.results.atms.size()){
			endIndex = results.results.atms.size();
		}

		mapWrapper.addObjectsToMap(results.results.atms.subList(currentIndex, endIndex));
		currentIndex = endIndex;
		bundle.putInt(BankExtraKeys.DATA_SELECTED_INDEX, currentIndex);
		listFragment.handleReceivedData(bundle);
	}

	/**
	 * Set if the fragment is loading more
	 * @param isLoadingMore - if the fragment is loading more
	 */
	@Override
	public void setIsLoadingMore(final boolean isLoadingMore){

	}

	/**
	 * Get if the fragment is loading more
	 * @return isLoadingMore - if the fragment is loading more
	 */
	@Override
	public boolean getIsLoadingMore(){
		return isLoading;
	}

	private boolean isListEmpty(){
		return (null == results || null == results.results || null == results.results.atms || results.results.atms.isEmpty());
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

	@Override
	public void getLocation(){
		isLoading = false;
		((NavigationRootActivity)this.getActivity()).startProgressDialog();
		locationManagerWrapper.getLocation();
	}

	/**
	 * Set the users location on the map.
	 */
	@Override
	public void setUserLocation(final Location location){
		if(null  != locationManagerWrapper){
			locationManagerWrapper.stopGettingLocaiton();
		}
		locationStatus = LOCKED_ON;
		mapWrapper.setUsersCurrentLocation(location, R.drawable.atm_starting_point_pin, this.getActivity());
		if(null == location){return;}
		if(LocationManager.GPS_PROVIDER == location.getProvider()){
			mapWrapper.zoomToLocation(location, MAP_CURRENT_GPS_ZOOM);
		}else{
			mapWrapper.zoomToLocation(location, MAP_CURRENT_NETWORK_ZOOM);
		}
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
		helper.setSurchargeFree(searchBar.isFilterOn());
		BankServiceCallFactory.createGetAtmServiceCall(helper).submit();
	}

	/**
	 * Save the state of the fragment
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState){
		//Something went wrong, do not save any information
		if(null == locationManagerWrapper){return;}
		locationManagerWrapper.stopGettingLocaiton();

		searchBar.saveState(outState);
		if(locationModal.isShowing()){
			locationModal.dismiss();
		} else if(settingsModal.isShowing()){
			settingsModal.dismiss();
		}
		outState.putInt(LOCATION_STATUS, locationStatus);
		if(null != mapWrapper.getCurrentLocation()){
			outState.putDouble(LAT_KEY, mapWrapper.getCurrentLocation().getLatitude());
			outState.putDouble(LONG_KEY, mapWrapper.getCurrentLocation().getLongitude());
		}
		if(results != null){
			outState.putSerializable(BankExtraKeys.DATA_LIST_ITEM, results);
		}
		outState.putInt(BankExtraKeys.DATA_SELECTED_INDEX, currentIndex);
		outState.putBoolean(BUTTON_KEY, isOnMap);
		outState.putBoolean(STREET_VIEW_SHOWING, shouldGoBack);
		if(shouldGoBack){
			streetView.bundleData(outState);
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onPause(){
		super.onPause();

		enableMenu();
	}

	/**
	 * Show that either the location could not be retrieved. 
	 */
	@Override
	public void showNoLocation() {
		locationManagerWrapper.stopGettingLocaiton();
		locationStatus = NOT_USING_LOCATION;
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
		locationManagerWrapper.stopGettingLocaiton();
		if(null == mapWrapper.getCurrentLocation()){
			showNoLocation();
		}
	}

	@Override
	public int getActionBarTitle() {
		return R.string.atm_locator_title;
	}

	@Override
	public void setLocation(final Location location) {
		mapWrapper.setCurrentLocation(location);
	}

	@Override
	public void showList(){
		streetView.hide();
		searchBar.setVisibility(View.VISIBLE);
		help.setVisibility(View.GONE);
		searchBar.showListView();
		isOnMap = false;
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
			navigationPanel.setVisibility(View.GONE);
			isListLand = true;
		}else{
			navigationPanel.setVisibility(View.VISIBLE);
			isListLand = false;
		}
		this.getActivity().getSupportFragmentManager().beginTransaction().hide(fragment).commitAllowingStateLoss();
		this.getActivity().getSupportFragmentManager().beginTransaction().show(listFragment).commitAllowingStateLoss();
	}

	@Override
	public void showMap(){
		streetView.hide();
		searchBar.setVisibility(View.VISIBLE);
		help.setVisibility(View.VISIBLE);
		searchBar.showMapView();
		navigationPanel.setVisibility(View.VISIBLE);
		isOnMap = true;
		isListLand = false;
		this.getActivity().getSupportFragmentManager().beginTransaction().hide(listFragment).commitAllowingStateLoss();
		this.getActivity().getSupportFragmentManager().beginTransaction().show(fragment).commitAllowingStateLoss();
	}

	@Override
	public void showStreetView(final Bundle bundle){
		shouldGoBack = true;
		streetView.show();
		streetView.loadStreetView(bundle);
	}

	public boolean canLoadMore(){
		boolean loadMore = true;
		if(isListEmpty()){
			loadMore =  false;
		}else if(currentIndex == (MAX_LOADS * INDEX_INCREMENT)){
			loadMore = false;
		}else if(results.results.atms.size() == currentIndex){
			loadMore =  false;
		}else{
			loadMore = true;
		}
		return loadMore;
	}

	/**
	 * Load more data
	 */
	public void loadMoreData(){
		final Bundle bundle = new Bundle();
		bundle.putSerializable(BankExtraKeys.DATA_LIST_ITEM, results);
		handleReceivedData(bundle);
	}

	/**
	 * Report an issue with an ATM
	 */
	public void reportAtm(final String id){
		shouldGoBack = true;
		streetView.show();
		streetView.reportAtm(id);
	}

	/**
	 * The onBackPressed method that an Activity normally calls.
	 */
	@Override
	public void onBackPressed(){
		if(shouldGoBack){
			streetView.clearWebview();
			streetView.hide();
			shouldGoBack = false;
		}else if(isListLand){
			toggleButton();
		}else{	
			shouldGoBack = true;
		}
	}

	/**
	 * Facade for FragmentOnBackPressed.isBackPressDisabled method. Used to determine
	 * if back press has been disbaled for the current fragment.
	 * 
	 * @return True if fragment does not allow back press, false otherwise.
	 */
	@Override
	public boolean isBackPressDisabled(){
		if(isListLand){
			return true;
		}else{
			return shouldGoBack;
		}
	}
}
