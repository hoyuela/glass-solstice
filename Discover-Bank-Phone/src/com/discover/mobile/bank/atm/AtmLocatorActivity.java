/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.atm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.login.LoginActivity;
import com.discover.mobile.bank.util.FragmentOnBackPressed;
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

		setMapFragment((AtmMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map));
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
			final Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			this.finish();
		}

		if(isBackPressFragment()){
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
	public void startProgressDialog() {		
		if(!isFragmentLoadingMore()){
			super.startProgressDialog();
		}
	}
}
