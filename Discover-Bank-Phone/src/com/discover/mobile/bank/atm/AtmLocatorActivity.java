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
	 * Handles the back press of the activity
	 */
	@Override 
	public void onBackPressed(){
		final Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		this.finish();
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
}
