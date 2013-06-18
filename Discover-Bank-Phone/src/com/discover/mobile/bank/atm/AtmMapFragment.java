/*
 * � Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.atm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AlphaAnimation;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.DynamicDataFragment;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.help.HelpMenuListFactory;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.services.atm.AddressToLocationDetail;
import com.discover.mobile.bank.services.atm.AddressToLocationResultDetail;
import com.discover.mobile.bank.services.atm.AtmResults;
import com.discover.mobile.bank.services.atm.AtmServiceHelper;
import com.discover.mobile.bank.util.FragmentOnBackPressed;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.DiscoverApplication;
import com.discover.mobile.common.DiscoverModalManager;
import com.discover.mobile.common.nav.NavigationRootActivity;
import com.discover.mobile.common.ui.modals.SimpleTwoButtonModal;
import com.discover.mobile.common.utils.CommonUtils;
import com.google.android.gms.common.GooglePlayServicesUtil;
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
implements LocationFragment, AtmMapSearchFragment, FragmentOnBackPressed,
DynamicDataFragment, OnTouchListener, OnGlobalLayoutListener {

	/**
	 * Location status of the fragment. Is set based off of user input and the ability
	 * to get the users location.  Defaults to NOT_ENABLED.
	 */
	private int locationStatus = NOT_ENABLED;

	/**Key to get the state of the buttons*/
	private static final String BUTTON_KEY = "buttonState";

	/**Private static key for the street view showing*/
	private static final String STREET_VIEW_SHOWING = "svs";

	/**Key to get the state of the help modal from the bundle*/
	private static final String ATM_HELP_MODAL = "atmModal";

	/**Key to get the state of the Leaving App modal from the bundle*/
	private static final String LEAVING_APP_MODAL = "leaveApp";

	/**Modal that asks the user if the app can use their current location*/
	private SimpleTwoButtonModal locationModal;

	/**Modal that lets the user know that their location services are disabled*/
	private SimpleTwoButtonModal settingsModal;

	/**Modal that lets the user know that getting of their location failed*/
	private SimpleTwoButtonModal locationFailureModal;

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

	/**Saved state of the fragment*/
	private Bundle savedState;

	/**Help Widget*/
	private ImageButton help;

	/**Location of the user*/
	private Location location;
	private Location cameraLocation = null;

	private static boolean needsToAnimateZoom = false;

	private AtmResults tempResults = null;
	private int resultEndIndex = 0;

	private float cameraZoom = MAP_CURRENT_GPS_ZOOM;

	/**Boolean true if the help menu alert menu is showing*/
	private boolean helpModalShowing = false;


	private boolean processingOnBackpress = false;

	/**
	 * Flag used to determing if the modal used for leaving the application is
	 * being shown
	 */
	private boolean isLeavingModalShowing = false;

	/**
	 * Holds reference to layout that displays Google Logo and Terms of Use Link
	 */
	private RelativeLayout googleTerms;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		/**
		 * Check if this is called because of rotation or because it is the first time it is instantiated.
		 * Nested Fragments should only be instantiated on the initial creation of this fragment.
		 */
		if( savedInstanceState == null ) {
			fragment = SupportMapFragment.newInstance();
			listFragment = new AtmListFragment();
			listFragment.setObserver(this);
			GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(getActivity());
		}
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState){
		final View view = inflater.inflate(getLayout(), null);

		/**
		 * The map and list fragments should only be added if they aren't already on the back stack. These
		 * are the only nested fragments that should be on the back stack of the child fragment manager for this fragment.
		 */
		if( getChildFragmentManager().getBackStackEntryCount() == 0 ) {
			getChildFragmentManager()
			.beginTransaction()
			.add(R.id.discover_map, fragment)
			.addToBackStack(fragment.getClass().getSimpleName())
			.commit();
			getChildFragmentManager()
			.beginTransaction()
			.add(R.id.discover_list, listFragment)
			.addToBackStack(listFragment.getClass().getSimpleName())
			.commit();
		}
		/**
		 * If fragments are already on the back stack then just do a look up in the back stack.
		 */
		else {
			fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.discover_map);
			listFragment = (AtmListFragment)getChildFragmentManager().findFragmentById(R.id.discover_list);
			listFragment.setObserver(this);
		}

		final WebView web = (WebView) view.findViewById(R.id.web_view);
		final ProgressBar bar = (ProgressBar) view.findViewById(R.id.progress_bar);
		streetView = new AtmWebView(web, bar);
		mapButton = (Button) view.findViewById(R.id.map_nav);
		listButton = (Button) view .findViewById(R.id.list_nav);
		help = (ImageButton) view.findViewById(R.id.help);
		help.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				showHelpModal();				
			}
		});
		navigationPanel = (LinearLayout) view.findViewById(R.id.map_navigation_panel);
		streetView.hide();
		locationManagerWrapper = new DiscoverLocationMangerWrapper(this);
		searchBar = (AtmLocatorMapSearchBar) view.findViewById(R.id.full_search_bar);
		searchBar.setFragment(this);
		googleTerms = (RelativeLayout) view.findViewById(R.id.terms_layout);
		googleTerms.setOnTouchListener(this);

		DiscoverModalManager.clearActiveModal();

		setUpListeners();
		disableMenu();
		savedState = getArguments();

		CommonUtils.fixBackgroundRepeat(navigationPanel);

		return view;
	}

	/**
	 * Shows the help modal
	 */
	protected void showHelpModal(){
		showCustomAlertDialog(AtmModalFactory.getAtmLocatorHelpModal(this));
		setHelpModalShowing(true);
	}


	/**
	 * Resume the fragment
	 */
	@Override
	public void onResume(){
		super.onResume();

		final NavigationRootActivity activity = (NavigationRootActivity)getActivity();
		activity.setCurrentFragment(this);
		setUpMap();
		disableMenu();

		setupMapOrListView();

		/**
		 * Verify that the bundle used to populate the map fragment has data.
		 */
		resumeStateOfFragment(savedState);


		determineNavigationStatus();

		if(shouldGoBack){
			streetView.showWebView();
		}

		if( fragment.getView() != null && fragment.getMap() != null ) {
			setMapTransparent((ViewGroup)fragment.getView());

		}

		if(isHelpModalShowing()){
			HelpMenuListFactory.instance().showAtmHelpModal(this);
			setHelpModalShowing(true);
		} else if (isLeavingModalShowing) {
			showTerms();
		}

		restoreCameraView();
		adjustMapZoomIfNeeded();
		
		fadeInMap();
	}
	
	/**
	 * Performs an animation on the map fragment to fade it in and avoid
	 * showing a black area for the map.
	 */
	private void fadeInMap() {
		final View mapView = getView().findViewById(R.id.discover_map);
		
		if(mapView != null) {
			final int halfSecond = 500;
			final AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
			fadeIn.setDuration(halfSecond);
			mapView.startAnimation(fadeIn);
			mapView.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Setup the view of the map fragment to be either the map or the list depending on the current
	 * isOnMap boolean.
	 */
	private void setupMapOrListView() {
		if(isOnMap){
			showMap();
		}else{
			showList();
		}
	}

	/**
	 * Restore the position of the camera on the map to what is stored in the local variables for the camera position
	 * and zoom level.
	 */
	private void restoreCameraView() {
		if(cameraLocation != null && cameraZoom != 0) {
			mapWrapper.getMap().stopAnimation();
			mapWrapper.focusCameraOnLocation(cameraLocation.getLatitude(), cameraLocation.getLongitude(), cameraZoom);
		}
	}

	/**
	 * Determine the location status and navigate based on that.
	 */
	private void determineNavigationStatus() {
		mapWrapper.focusCameraOnLocation(MAP_CENTER_LAT, MAP_CENTER_LONG);
		if(NOT_ENABLED == locationStatus){
			locationStatus = locationManagerWrapper.areProvidersenabled() ? ENABLED : NOT_ENABLED;
		}

		switch(locationStatus){
		case NOT_ENABLED:
			settingsModal = AtmModalFactory.getSettingsModal(getActivity(), this);
			((NavigationRootActivity) getActivity()).showCustomAlert(settingsModal);
			break;
		case ENABLED:
			showLocationAcceptanceModal();
			break;
		case LOCATION_FAILED:
			locationFailureModal = AtmModalFactory.getCurrentLocationFailModal(getActivity(), this);
			((NavigationRootActivity) getActivity()).showCustomAlert(locationFailureModal);
			break;
		case SEARCHING:
			getLocation();
			break;
		case LOCKED_ON:
			setUserLocation(mapWrapper.getCurrentLocation());
			break;
		default:
			break;
		}
	}

	private void showLocationAcceptanceModal() {
		if(DiscoverApplication.getLocationPreference().shouldShowModal()) {
			locationModal = AtmModalFactory.getLocationAcceptanceModal(getActivity(), 
					AtmMapFragment.this);
			((NavigationRootActivity) getActivity()).showCustomAlert(locationModal);
		}else {
			searchCurrentLocation();
		}
	}

	public final void searchCurrentLocation() {
		setLocationStatus(LocationFragment.SEARCHING);
		getLocation();
	}

	/**
	 * Set up the map wrapper if it needs to be setup
	 */
	private void setUpMap(){
		final AtmMarkerBalloonManager balloon = new AtmMarkerBalloonManager(this);
		final DiscoverInfoWindowAdapter adapter = new DiscoverInfoWindowAdapter(balloon);
		if(null == mapWrapper){
			mapWrapper = new DiscoverMapWrapper(fragment.getMap(), adapter);
		}
		if (null == mapWrapper){
			mapWrapper = new DiscoverMapWrapper(fragment.getMap(), adapter);
			setMapTransparent((ViewGroup)fragment.getView());
		}
		if(null != location){
			mapWrapper.setCurrentLocation(location);
			setUserLocation(mapWrapper.getCurrentLocation());
		}
	}

	/**
	 * Method to set the map surface view background to transparent.  This comes from an issue with
	 * the integration of the sliding menu and the google maps.
	 * @param group - view group containing the surface view
	 */
	private void setMapTransparent(final ViewGroup group) {
		final int childCount = group.getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View child = group.getChildAt(i);
			if (child instanceof ViewGroup) {
				setMapTransparent((ViewGroup) child);
			} else if (child instanceof SurfaceView) {
				child.setBackgroundColor(0x00000000);
			}
		}
	}

	/*
	 * Get the layout for the file
	 */
	public abstract int getLayout();

	/**
	 * @return the current location address string
	 */
	@Override
	public void startCurrentLocationSearch() {
		if(!locationManagerWrapper.areProvidersenabled()){
			settingsModal = AtmModalFactory.getSettingsModal(getActivity(), this);
			((NavigationRootActivity)getActivity()).showCustomAlert(settingsModal);
		}else{
			locationStatus = ENABLED;
			if(LOCKED_ON == locationStatus){
				getLocation();
			}else if(null == locationModal || !locationModal.isShowing()){
				showLocationAcceptanceModal();	
			} 	
		}
	}

	/**
	 * Perform the search
	 * @param text - search text
	 */
	@Override
	public void performSearch(final String text) {		
		isLoading = false;
		((NavigationRootActivity)getActivity()).startProgressDialog(true);
		final AtmServiceHelper helper = new AtmServiceHelper(text);
		BankServiceCallFactory.getLocationFromAddressCall(helper).submit();
	}

	@Override
	public void onStart(){
		super.onStart();

		final NavigationRootActivity activity = (NavigationRootActivity) getActivity();
		activity.highlightMenuItems(getGroupMenuLocation(), getSectionMenuLocation());
	}

	/**
	 * Handle a successful address to location response
	 * @param bundle - bundle of data retrieved from the service call
	 */
	public void handleAddressToLocationResponse(final Bundle bundle){
		final AddressToLocationDetail addressResults = (AddressToLocationDetail) bundle.get(BankExtraKeys.DATA_LIST_ITEM);
		if(null == addressResults || null == addressResults.results || addressResults.results.isEmpty()){
			((NavigationRootActivity)getActivity()).closeDialog();
			AtmModalFactory.getInvalidAddressModal(getActivity()).show();
		}else{
			final AddressToLocationResultDetail address = addressResults.results.get(0);
			final Location newLocation = new Location(LocationManager.GPS_PROVIDER);
			newLocation.setLatitude(address.geometry.endLocation.lat);
			newLocation.setLongitude(address.geometry.endLocation.lon);
			mapWrapper.clear();
			currentIndex = 0;
			mapWrapper.setUsersCurrentLocation(newLocation, R.drawable.atm_starting_point_pin, getActivity());

			zoomToLocation(newLocation);

			getAtms(newLocation);
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
			mapButton.setBackgroundResource(isLandscape ? 
					R.drawable.atm_pinview_button_landscape : R.drawable.atm_pinview_button);
			listButton.setBackgroundResource(isLandscape ? 
					R.drawable.atm_listview_button_ds_landscape : R.drawable.atm_listview_button_ds);
			isOnMap = false;
		}else{
			showMap();
			mapButton.setBackgroundResource(isLandscape ? 
					R.drawable.atm_pinview_button_ds_landscape : R.drawable.atm_pinview_button_ds);
			listButton.setBackgroundResource(isLandscape ? 
					R.drawable.atm_listview_button_landscape : R.drawable.atm_list_view_button);
			isOnMap = true;	
			adjustMapZoomIfNeeded();
		}
	}

	/**
	 * Will re-focus the camera on the current location and all visible pins if for example, a user loads more atms
	 * from the list view and toggles back to the map view.
	 */
	private void adjustMapZoomIfNeeded() {
		if(needsToAnimateZoom) {
			if(tempResults != null && resultEndIndex != 0 && isOnMap) {
				mapWrapper.getMap().stopAnimation();
				adjustZoomToShowPins(tempResults, resultEndIndex);
				tempResults = null;
				resultEndIndex = 0;
				needsToAnimateZoom = false;
			}
		}
	}

	private static final String TEMP_RESULTS = "tr";
	private static final String RESULT_END_INDEX = "ren";

	/**
	 * Resume the state of the fragment
	 * @param savedInstanceState - bundle holding the state of the fragment
	 */
	private void resumeStateOfFragment(final Bundle savedInstanceState) {
		if(savedInstanceState != null && !savedInstanceState.isEmpty()) {
			locationStatus = savedInstanceState.getInt(LOCATION_STATUS, locationStatus);
			final Double lat = savedInstanceState.getDouble(LAT_KEY);
			final Double lon = savedInstanceState.getDouble(LONG_KEY);
			tempResults = (AtmResults)savedInstanceState.getSerializable(TEMP_RESULTS);
			resultEndIndex = savedInstanceState.getInt(RESULT_END_INDEX);

			isOnMap = !savedInstanceState.getBoolean(BUTTON_KEY, true);
			toggleButton();
			results = (AtmResults)savedInstanceState.getSerializable(BankExtraKeys.DATA_LIST_ITEM);
			currentIndex = savedInstanceState.getInt(BankExtraKeys.DATA_SELECTED_INDEX, 0);
			listFragment.handleReceivedData(savedInstanceState);
			setHelpModalShowing(savedInstanceState.getBoolean(ATM_HELP_MODAL, false));
			isLeavingModalShowing = savedInstanceState.getBoolean(LEAVING_APP_MODAL, false);
			if(0.0 == lat && 0.0 == lon){
				mapWrapper.focusCameraOnLocation(MAP_CENTER_LAT, MAP_CENTER_LONG);
			}else{
				if(null != results){
					hasLoadedAtms = true;
				}

				final float zoom = savedInstanceState.getFloat(MAP_ZOOM);

				if(zoom != 0.0f) {
					cameraZoom = zoom;
				}

				restoreCameraLocation(savedInstanceState);

				restoreCurrentLocation(lat, lon);

				addResultsToMap(results, currentIndex);

				searchBar.restoreState(savedInstanceState);
				streetView.hide();

				shouldGoBack = savedInstanceState.getBoolean(STREET_VIEW_SHOWING, true);
				if(shouldGoBack){
					showStreetView(savedInstanceState);
				}
			}
		}
	}

	/**
	 * Set the current location marker to the following lat and lon values.
	 * @param lat
	 * @param lon
	 */
	private void restoreCurrentLocation(final double lat, final double lon) {
		final Location newLocation = new Location(LocationManager.GPS_PROVIDER);
		newLocation.setLatitude(lat);
		newLocation.setLongitude(lon);
		mapWrapper.setCurrentLocation(newLocation);
	}

	/**
	 * Retrieve a saved camera position from a Bundle and set the camera's position to that value.
	 * @param savedInstanceState a Bundle which contains a lat and lon value for camera position.
	 */
	private void restoreCameraLocation(final Bundle savedInstanceState)  {
		final Location newLocation = new Location(LocationManager.GPS_PROVIDER);

		final double cameraLat = savedInstanceState.getDouble(CAMERA_LAT);
		final double cameraLon = savedInstanceState.getDouble(CAMERA_LON);
		cameraLocation = new Location(newLocation);

		if(cameraLat != 0 && cameraLon != 0) {
			cameraLocation.setLatitude(cameraLat);
			cameraLocation.setLongitude(cameraLon);
		}
	}

	/**
	 * Add a set of atm results objects to the map.
	 * @param results an AtmResults object that contains atm locations.
	 * @param endIndex the index that is the lat position in the list of atm results that should be added to the map.
	 */
	private void addResultsToMap(final AtmResults results, final int endIndex) {
		if(null != results && !hasLoadedAtms){
			mapWrapper.addObjectsToMap(results.results.atms.subList(0, endIndex));
			hasLoadedAtms = true;
		}
	}


	/**
	 * Disable the sliding menu
	 */
	private void disableMenu(){
		((NavigationRootActivity)getActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
	}

	/**
	 * Enable the sliding menu
	 */
	private void enableMenu(){
		((NavigationRootActivity)getActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
	}

	/**
	 * Handle the ATMs that are received from the services and display them on the map.
	 * @param bundle - bundle
	 */
	@Override
	public void handleReceivedData(final Bundle bundle){
		isLoading = true;
		results = (AtmResults)bundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM);
		int endIndex = currentIndex + INDEX_INCREMENT;
		if(isListEmpty()){
			showCustomAlertDialog(AtmModalFactory.getNoResultsModal(getActivity()));
			endIndex = 0;
		}else if(endIndex > results.results.atms.size()){
			endIndex = results.results.atms.size();
		}

		mapWrapper.addObjectsToMap(results.results.atms.subList(currentIndex, endIndex));
		currentIndex = endIndex;
		bundle.putInt(BankExtraKeys.DATA_SELECTED_INDEX, currentIndex);
		listFragment.handleReceivedData(bundle);
		if(isOnMap) {
			adjustZoomToShowPins(results, endIndex);
		}else {
			needsToAnimateZoom = true;
			tempResults = results;
			resultEndIndex = endIndex;
		}
	}

	/**
	 * Adjusts the camera to zoom to encompass all pins within 25 miles of the users location.
	 * 
	 * @param atms a list that contians ATM locations
	 * @param endIndex the last position in the list of atms to zoom around.
	 */
	private void adjustZoomToShowPins(final AtmResults atms, final int endIndex) {
		new Thread(new AutoZoomRunnable(mapWrapper, atms, endIndex)).start();
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
		return null == results || null == results.results ||
				null == results.results.atms || results.results.atms.isEmpty();
	}

	@Override
	public void getLocation(){
		isLoading = false;
		hasLoadedAtms = false;
		mapWrapper.clear();
		currentIndex = 0;
		((NavigationRootActivity)getActivity()).startProgressDialog(true);
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
		mapWrapper.setUsersCurrentLocation(location, R.drawable.atm_starting_point_pin, getActivity());
		if(null == location){return;}

		zoomToLocation(location);

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
	private void onStoreState(final Bundle outState){
		//Something went wrong, do not save any information
		if(null == locationManagerWrapper){return;}
		locationManagerWrapper.stopGettingLocaiton();

		searchBar.saveState(outState);
		hideModalIfNeeded();

		if(null != mapWrapper.getMap()){
			outState.putFloat(MAP_ZOOM, mapWrapper.getCurrentMapZoom());
			outState.putDouble(CAMERA_LAT, mapWrapper.getCameraLocation().latitude);
			outState.putDouble(CAMERA_LON, mapWrapper.getCameraLocation().longitude);
		}
		outState.putSerializable(TEMP_RESULTS, tempResults);
		outState.putInt(RESULT_END_INDEX, resultEndIndex);

		outState.putInt(LOCATION_STATUS, locationStatus);
		if(null != mapWrapper.getCurrentLocation()){
			outState.putDouble(LAT_KEY, mapWrapper.getCurrentLocation().getLatitude());
			outState.putDouble(LONG_KEY, mapWrapper.getCurrentLocation().getLongitude());
		}
		if(results != null){
			outState.putSerializable(BankExtraKeys.DATA_LIST_ITEM, results);
		}
		outState.putBoolean(ATM_HELP_MODAL, isHelpModalShowing());
		outState.putBoolean(LEAVING_APP_MODAL, isLeavingModalShowing);
		outState.putInt(BankExtraKeys.DATA_SELECTED_INDEX, currentIndex);
		outState.putBoolean(BUTTON_KEY, isOnMap);
		outState.putBoolean(STREET_VIEW_SHOWING, shouldGoBack);
		if(shouldGoBack){
			streetView.bundleData(outState);
		}
	}

	/**
	 * Hide one of the modals if they are showing
	 */
	private void hideModalIfNeeded() {
		if(null != locationModal && locationModal.isShowing()){
			locationModal.dismiss();
		} else if(null != settingsModal && settingsModal.isShowing()){
			settingsModal.dismiss();
		} else if(null != locationFailureModal && locationFailureModal.isShowing()){
			locationFailureModal.dismiss();
		} else if(isHelpModalShowing()){
			DiscoverModalManager.getActiveModal().dismiss();
			DiscoverModalManager.clearActiveModal();
			setHelpModalShowing(true);
		}
	}

	@Override
	public void onPause(){
		super.onPause();

		location = mapWrapper.getCurrentLocation();
		enableMenu();

		/**
		 * Save the state of this fragment in the argument bundle provided
		 * at instantiation. This allows for the data to persist even
		 * if another fragment has been placed on top of this one in the
		 * hosting activities back stack.
		 */
		onStoreState(getArguments());
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
		final NavigationRootActivity activity = (NavigationRootActivity)getActivity();
		activity.closeDialog();
		locationManagerWrapper.stopGettingLocaiton();
		locationFailureModal = AtmModalFactory.getCurrentLocationFailModal(activity, this);
		activity.showCustomAlert(locationFailureModal);
		locationStatus = LOCATION_FAILED;
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
		googleTerms.setVisibility(View.GONE);
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

		getChildFragmentManager().beginTransaction().hide(fragment).commitAllowingStateLoss();
		getChildFragmentManager().beginTransaction().show(listFragment).commitAllowingStateLoss();

		if(!searchBar.isSearchExpanded()){
			searchBar.showBar();
		}
	}

	@Override
	public void showMap(){
		streetView.hide();
		googleTerms.setVisibility(View.VISIBLE);
		searchBar.setVisibility(View.VISIBLE);
		help.setVisibility(View.VISIBLE);
		searchBar.showMapView();
		navigationPanel.setVisibility(View.VISIBLE);
		isOnMap = true;
		isListLand = false;
		if(null == fragment.getMap()){
			final View frame = getView().findViewById(R.id.discover_map);
			final RelativeLayout.LayoutParams params = 
					new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.BELOW, R.id.full_search_bar);
			frame.setLayoutParams(params);
		}
		getChildFragmentManager().beginTransaction().hide(listFragment).commitAllowingStateLoss();
		getChildFragmentManager().beginTransaction().show(fragment).commitAllowingStateLoss();
		if(!searchBar.isSearchExpanded()){
			searchBar.hideBar();
		}
	}

	/**
	 * Show the street view for an ATM
	 * @param bundle - bundle of data to be shown
	 */
	@Override
	public void showStreetView(final Bundle bundle){
		shouldGoBack = true;
		streetView.show();
		streetView.loadStreetView(bundle);
	}

	/**
	 * Determine if the fragment can load more data or if it has loaded all the data available
	 * @return true if the fragment can load more data
	 */
	public boolean canLoadMore(){
		boolean loadMore = true;
		if(isListEmpty()){
			loadMore =  false;
		}else if(currentIndex == MAX_LOADS * INDEX_INCREMENT){
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
		if(helpModalShowing){
			DiscoverModalManager.getActiveModal().dismiss();
			setHelpModalShowing(false);
		}if(shouldGoBack){
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
		if(isListLand || isHelpModalShowing()){
			return true;
		}else{

			preProcessOnBackPress();

			return shouldGoBack;
		}
	}

	/**
	 * Method used to handle the black out of the Map View during an animated transition of the hosting fragment. This
	 * method is only meant to be handled when the active activity is a BankNavigationRootActivity. This method will
	 * hide the map view if it is being displayed and once its visibility has been changed then it will continue with
	 * processing the back press event.
	 */
	private void preProcessOnBackPress() {
		/**
		 * This method should only be handled if the active activity is a NavigationRootActivity
		 */
		if (DiscoverActivityManager.getActiveActivity() instanceof BankNavigationRootActivity) {
			/**
			 * If the fragment is displaying the map, then hide the map before navigating away. This is to overcome the
			 * limitation of Google Map View v2 where the surface view turns black during the fragment transition.
			 */
			if (isOnMap && isMapVisible()) {
				/** The shouldGoBack will be set as per request when the map view is hidden */
				processingOnBackpress = !shouldGoBack;
				shouldGoBack = true;

				/** Listen for when the visibility of the map view changes */
				final ViewTreeObserver vto = getView().getViewTreeObserver();
				vto.addOnGlobalLayoutListener(this);

				/** Hide Map View */
				showMapView(false);
			}
		}
	}

	/**
	 * Event callback for when the view tree for a view changes. In this class this callback is only called when the
	 * visibility of the map view has changed during the processing of an onBackPress event.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onGlobalLayout() {
		/** Check if this event should be handled */
		if (processingOnBackpress) {
			getView().getViewTreeObserver().removeGlobalOnLayoutListener(this);

			/** Set flag to the value it was prior to this event being fired */
			shouldGoBack = !processingOnBackpress;

			/** Delay executing the back press to allow time for the map view to be hidden */
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					DiscoverActivityManager.getActiveActivity().onBackPressed();
				}
			}, 0);
		}
	}

	/**
	 * @return the current location address string
	 */
	public String getCurrentLocationAddress() {
		return mapWrapper.getGetAddressString();
	}

	/**
	 * @return the helpModalShowing
	 */
	public boolean isHelpModalShowing() {
		return helpModalShowing;
	}

	/**
	 * @param helpModalShowing the helpModalShowing to set
	 */
	public void setHelpModalShowing(final boolean helpModalShowing) {
		this.helpModalShowing = helpModalShowing;
	}

	private void zoomToLocation(final Location location) {
		if (LocationManager.GPS_PROVIDER == location.getProvider()) {
			mapWrapper.zoomToLocation(location, MAP_CURRENT_GPS_ZOOM);
		} else {
			mapWrapper.zoomToLocation(location, MAP_CURRENT_NETWORK_ZOOM);
		}
	}

	@Override
	public boolean onTouch(final View sender, final MotionEvent event) {
		/** Check if User clicked on Terms link in footer */
		if (sender != null && sender == googleTerms && event.getAction() == MotionEvent.ACTION_DOWN) {
			final int four = 4;
			final int five = 5;

			final float locationOfLink = googleTerms.getLeft() + googleTerms.getMeasuredWidth() * four / five;

			if (event.getRawX() > locationOfLink) {
				showTerms();
			}
		}
		return false;
	}

	private void showTerms() {
		/**
		 * Show modal to prompt user that they are navigating away from
		 * application
		 */
		final AlertDialog dialog = BankConductor.navigateToBrowser(R.string.atm_browser_title, R.string.atm_browser_body,
				BankUrlManager.getCardGoogleTermsUrl());

		/** Listen to when modal is dismissed to set flag to false */
		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(final DialogInterface arg0) {
				isLeavingModalShowing = false;
			}
		});

		/** Set flag to true so modal is displayed on rotation */
		isLeavingModalShowing = true;

	}

	/**
	 * Private method used to show or hide the map view.
	 * 
	 * @param value
	 *            True to show the map view, false otherwise.
	 */
	private void showMapView(final boolean value) {
		final FrameLayout mapLayout = (FrameLayout) getView().findViewById(R.id.discover_map);

		if (value) {
			mapLayout.setVisibility(View.VISIBLE);
		} else {
			getChildFragmentManager().beginTransaction().hide(fragment).commitAllowingStateLoss();

			mapLayout.setVisibility(View.GONE);
		}
	}

	/**
	 * Private method used to check whether the map view is being displayed.
	 * 
	 * @return True if map view is being displayed, false otherwise.
	 */
	private boolean isMapVisible() {
		boolean isVisible = false;

		if (getView() != null) {
			final FrameLayout mapLayout = (FrameLayout) getView().findViewById(R.id.discover_map);

			isVisible = mapLayout.getVisibility() == View.VISIBLE;
		}
		return isVisible;
	}
}
