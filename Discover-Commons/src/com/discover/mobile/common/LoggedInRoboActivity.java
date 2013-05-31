package com.discover.mobile.common;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.discover.mobile.common.facade.FacadeFactory;
import com.slidingmenu.lib.SlidingMenu;

/**
 * This is used as the base activity for when the user has logged in. Extending
 * this will show the action bar with the sliding bar as well as the title text
 * and logout button.
 * 
 * @author jthornton
 * 
 */
public abstract class LoggedInRoboActivity extends BaseFragmentActivity {

	/** Pulled out variable for the fade of the sliding menu */
	private static final float FADE = 0.35f;
	/** Flag used to know when in the middle of a log out */
	private static boolean pendingLogout = false;
	private  ImageView navigationToggle;
	private ImageView backButtonX;

	/**
	 * Flag used for if its bank or not
	 */
	Boolean isCard;

	/**
	 * Create the activity, set up the action bar and sliding menu
	 * 
	 * @param savedInstanceState
	 *            - saved State of the activity
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Globals.getCurrentAccount().equals(AccountType.CARD_ACCOUNT)) {
			isCard = true;
		} else {
			isCard = false;
		}
		showActionBar();
		setupSlidingMenu();
	}

	/**
	 * Returns the id for the sliding drawer menu frame
	 * @return
	 */
	public abstract int getBehindContentView();

	/**
	 * Show the action bar with the custom layout
	 */
	public void showActionBar() {
		setBehindContentView(getBehindContentView());

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setCustomView(getLayoutInflater().inflate(
				R.layout.action_bar_menu_layout, null));
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

		final TextView titleView = (TextView) findViewById(R.id.title_view);
		navigationToggle = (ImageView) findViewById(R.id.navigation_button);
		final Button logout = (Button) findViewById(R.id.logout_button);
		backButtonX = (ImageView) findViewById(R.id.navigation_back_x_button);

		navigationToggle.setVisibility(View.VISIBLE);
		logout.setVisibility(View.VISIBLE);
		navigationToggle.setVisibility(View.VISIBLE);
		titleView.setVisibility(View.VISIBLE);

		navigationToggle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				toggle();
			}
		});


		backButtonX.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				onBackPressed();
			}
		});

		logout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				Globals.setLoggedIn(false);
				logout();
			}
		});
	}

	/**
	 * Log the user out of card
	 */
	public void logout() {

		/** Used on pause to know when to set Globals isLoggedIn to false **/
		pendingLogout = true;

		FacadeFactory.getLogoutFacade().logout(this,this,Globals.getCurrentAccount());

	}

	/**
	 * Set up and style the sliding menu
	 */
	protected void setupSlidingMenu() {
		final SlidingMenu slidingMenu = getSlidingMenu();
		slidingMenu.setShadowWidthRes(R.dimen.nav_menu_shadow_width);
		slidingMenu.setShadowDrawable(R.drawable.nav_menu_shadow);
		slidingMenu.setBehindOffsetRes(R.dimen.nav_menu_offset);
		slidingMenu.setFadeDegree(FADE);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		slidingMenu.setBehindScrollScale(0.0f);
	}

	/**
	 * Checks the shared pref to get the visibility for the status bar and sets
	 * the visibility
	 */
	public void setStatusBarVisbility() {
		final FragmentTransaction ft = getSupportFragmentManager()
				.beginTransaction();
		final boolean statusBarVisitility = Globals.isStatusBarVisibility();
		final Fragment statusBar = getSupportFragmentManager()
				.findFragmentById(R.id.status_bar);

		/**
		 * If its set to false hide the fragment, else show it.
		 */
		if (!statusBarVisitility) {
			ft.hide(statusBar);
		} else {
			ft.show(statusBar);
		}
		ft.commit();
	}

	/**
	 * Updates the shared preference for the status bar visibility and then
	 * calls setStatusBarVisibility to update the visibility
	 * 
	 * @param visible
	 *            - boolean for setting the shared pref
	 */
	public void updateStatusBarVisibility() {
		final Fragment statusBar = getSupportFragmentManager()
				.findFragmentById(R.id.status_bar);
		boolean visible = true;
		if (statusBar.isVisible()) {
			visible = false;
		} else if (statusBar.isHidden()) {
			visible = true;
		}
		Globals.setStatusBarVisibility(visible);

		setStatusBarVisbility();
	}

	@Override
	public void onPause() {
		super.onPause();

		// Clear all global variables after logout
		if (pendingLogout) {
			pendingLogout = false;

			Globals.setToDefaults();
		}
	}

	/**
	 * Displays the Discover logo in the actionBar as opposed to the TextView.
	 */
	public void showActionBarLogo() {
		showActionBarLogo(true);
	}

	/**
	 * Displays the TextView in the ActionBar and hides the Discover Logo.d
	 */
	public void hideActionBarLogo() {
		showActionBarLogo(false);
	}

	/**
	 * Shows the "X" in the action bar
	 */
	public void showBackX(){
		backButtonX.setVisibility(View.VISIBLE);
		navigationToggle.setVisibility(View.GONE);
	}

	/**
	 * Show menu button
	 */
	public void showMenuButton(){
		backButtonX.setVisibility(View.GONE);
		navigationToggle.setVisibility(View.VISIBLE);
	}

	/**
	 * Hides and shows the textView and ImageView.
	 * 
	 * @param show
	 *            Displays logo if true, displays TextView otherwise.
	 */
	protected void showActionBarLogo(final boolean show) {
		final TextView titleView = (TextView) findViewById(R.id.title_view);
		final ImageView titleImageView = (ImageView) findViewById(R.id.action_bar_discover_logo);

		if (show) {
			titleView.setVisibility(View.GONE);
			titleImageView.setVisibility(View.VISIBLE);
		} else {
			titleView.setVisibility(View.VISIBLE);
			titleImageView.setVisibility(View.GONE);
		}
	}

	public void disableMenuButton()
	{
		navigationToggle.setVisibility(View.INVISIBLE);
	}

	public void enableMenuButton()
	{
		navigationToggle.setVisibility(View.VISIBLE);
	}
}
