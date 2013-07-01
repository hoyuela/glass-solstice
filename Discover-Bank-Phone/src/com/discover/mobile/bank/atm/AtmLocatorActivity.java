/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.atm;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.ui.modals.AtmSearchingForAtmsModal;
import com.discover.mobile.bank.util.FragmentOnBackPressed;
import com.discover.mobile.common.DiscoverModalManager;
import com.discover.mobile.common.error.ErrorHandler;
import com.discover.mobile.common.nav.NavigationRootActivity;

/**
 * Activity that will allow the user to access the atm locator
 * outside of the application
 * @author jthornton
 *
 */
public class AtmLocatorActivity extends NavigationRootActivity{

	/**Atm Fragment that is being shown on the screen*/
	private AtmMapFragment mapFragment;

	/**
	 * Create the activity
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.bank_atm_locator_activity);
		showActionBar();
	}

	/**
	 * Returns the id for the sliding drawer menu frame
	 * @return
	 */
	@Override
	public int getBehindContentView(){
		return R.layout.atm_locator_nav;
	}

	/**
	 * Show the action bar with the custom layout
	 */
	@Override
	public void showActionBar() {
		setBehindContentView(getBehindContentView());

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setCustomView(getLayoutInflater().inflate(
				R.layout.action_bar_menu_layout, null));
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

		final TextView titleView = (TextView) findViewById(R.id.title_view);
		final ImageView navigationToggle = (ImageView) findViewById(R.id.navigation_button);
		final Button logout = (Button) findViewById(R.id.logout_button);
		titleView.setVisibility(View.VISIBLE);
		navigationToggle.setVisibility(View.INVISIBLE);
		logout.setVisibility(View.INVISIBLE);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		/**
		 * If the backstack of the fragment manager for this activity is empty, then
		 * the map fragment has not been added and it should be added.
		 */
		if( 0 == this.getSupportFragmentManager().getBackStackEntryCount() )  {
			mapFragment = new SearchNearbyFragment();
			mapFragment.setArguments(new Bundle());
			getSupportFragmentManager().beginTransaction()
									   .add(R.id.map, mapFragment)
									   .addToBackStack(mapFragment.getClass().getSimpleName())
									   .commit();
		}
		/**
		 * Map Fragment should not have to be re-created on rotation or when activity is minimized
		 */
		else {
			mapFragment = (AtmMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		}
		
		setMapFragment(mapFragment);
		
		//If a modal was showing show the modal
		if(DiscoverModalManager.isAlertShowing() && null != DiscoverModalManager.getActiveModal()){
			if (DiscoverModalManager.getActiveModal() instanceof ProgressDialog) {
				startProgressDialog(DiscoverModalManager.isProgressDialogCancelable());
			} else {
				DiscoverModalManager.getActiveModal().show();
			}
			DiscoverModalManager.setAlertShowing(true);
		}
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		AtmTapAndHoldCoachOverlay coachOverlay = mapFragment.getCoachOverlay();
		
		if(coachOverlay != null && coachOverlay.isShowing()) {
			mapFragment.getCoachOverlay().dismissCoach();
		}
		
		return super.dispatchTouchEvent(ev);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		/**
		 * Remove the fragment from the activity to avoid any leaks only if onPause is being called
		 * because this Activity is being destroyed
		 */
		if (isFinishing() && mapFragment != null && getSupportFragmentManager().getBackStackEntryCount() > 0) {
			mapFragment = null;
		}
		
		if (DiscoverModalManager.getActiveModal() instanceof AtmSearchingForAtmsModal) {
			DiscoverModalManager.getActiveModal().dismiss();
			DiscoverModalManager.setAlertShowing(true);
		}
	}

	@Override
	public ErrorHandler getErrorHandler() {
		return null;
	}

	/**
	 * Determines if the current fragment implements the FragmentOnBackPressed
	 * interface.
	 * 
	 * @return if the current fragment implements the FragmentOnBackPressed
	 *         interface.
	 */
	public boolean isBackPressFragment() {
		return mapFragment instanceof FragmentOnBackPressed;
	}


	/**
	 * Facade for FragmentOnBackPressed.isBackPressDisabled method. Used to determine
	 * if back press has been disbaled for the current fragment.
	 * 
	 * @return True if fragment does not allow back press, false otherwise.
	 */
	public boolean isBackPressDisabled() {
		return ( isBackPressFragment() && ((FragmentOnBackPressed)mapFragment).isBackPressDisabled());
	}

	/**
	 * Handles the back press of the activity
	 */
	@Override 
	public void onBackPressed(){
		if(!isBackPressDisabled()) {
			BankConductor.navigateToLoginPage(this, "", "");
		}

		if (!isFinishing() && isBackPressFragment()) {
			((FragmentOnBackPressed)mapFragment).onBackPressed();
		}
	}

	/**
	 * @return the fragment
	 */
	public AtmMapFragment getMapFragment() {
		return mapFragment;
	}

	/**
	 * @param fragment the fragment to set
	 */
	private void setMapFragment(final AtmMapFragment fragment) {
		mapFragment = fragment;
	}

	/**
	 * Determines if the current fragment is attempting to load more
	 * 
	 * @return if the current fragment is attempting to load more
	 */
	public boolean isFragmentLoadingMore(){
		return mapFragment.getIsLoadingMore();
	}

	/**
	 * Starts a Progress dialog using this activity as the context. The ProgressDialog created
	 * will be set at the active dialog.
	 */
	@Override
	public void startProgressDialog(boolean isProgressDialogCancelable) {
		//Prevent a second modal from appearing if this method is recalled.
		if (!DiscoverModalManager.hasActiveModal()) {
			DiscoverModalManager.setActiveModal(new AtmSearchingForAtmsModal(getContext(), false, null));
			DiscoverModalManager.setProgressDialogCancelable(false);
			DiscoverModalManager.setAlertShowing(true);
			DiscoverModalManager.getActiveModal().show();	
		}
	}
}
