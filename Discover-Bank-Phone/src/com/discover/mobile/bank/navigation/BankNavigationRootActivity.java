package com.discover.mobile.bank.navigation;

import java.util.Calendar;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.discover.mobile.bank.BankNavigator;
import com.discover.mobile.bank.DynamicDataFragment;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.account.FragmentOnBackPressed;
import com.discover.mobile.bank.error.BankErrorHandler;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.error.ErrorHandler;
import com.discover.mobile.common.nav.NavigationRootActivity;
import com.slidingmenu.lib.SlidingMenu;

/**
 * Root activity for the application after login. This will transition fragment on and off the screen
 * as well as show the sliding bar as well as the action bar.
 *
 */
public class BankNavigationRootActivity extends NavigationRootActivity {


	/**
	 * Resume the activity to the state that it was when the activity went to the background
	 */
	@Override
	public void onResume(){
		super.onResume();
		getLastTouchTime();
	}

	/**
	 * Used to handle user interaction across the application.
	 * 
	 * @param ev
	 *            The MotionEvent that was recognized.
	 * @return True if consumed, false otherwise.
	 */
	@Override
	public boolean dispatchTouchEvent(final MotionEvent ev) {
		super.dispatchTouchEvent(ev);

		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			getLastTouchTime();
		}

		// Don't consume event.
		return false;
	}

	/**
	 * Determines the current time and gets the time stored in globals.
	 * Then updates globals with the current time.
	 */
	private void getLastTouchTime() {
		final Calendar mCalendarInstance = Calendar.getInstance();

		final long previousTime = Globals.getOldTouchTimeInMillis();
		final long currentTime = mCalendarInstance.getTimeInMillis();

		setIsUserTimedOut(previousTime, currentTime);
		Globals.setOldTouchTimeInMillis(currentTime);
	}

	/**
	 * Determines whether or not the user is timed out.
	 * @param previousTime
	 * @param currentTime
	 */
	private void setIsUserTimedOut(final long previousTime, final long currentTime) {
		// Previous value exists
		if (previousTime != 0) {
			final long difference = currentTime - previousTime;
			final float secs = (float)difference / 1000;

			// User has become inactive and will be set to timed-out.
			if ( secs > BankUrlManager.MAX_IDLE_TIME) {
				BankNavigator.navigateToLoginPage(this, IntentExtraKey.SESSION_EXPIRED);
			}
		}
	}

	@Override
	public int getBehindContentView() {
		// TODO Auto-generated method stub
		return R.layout.navigation_bank_menu_frame;
	}

	/* (non-Javadoc)
	 * @see com.discover.mobile.common.BaseFragmentActivity#getErrorHandler()
	 */
	@Override
	public ErrorHandler getErrorHandler() {
		return BankErrorHandler.getInstance();
	}
	
	/**
	 * Determines if the current fragment is an instance of the dynamic date fragment
	 * @return if the current fragment is an instance of the dynamic date fragment
	 */
	public boolean isDynamicDataFragment(){
		return this.currentFragment instanceof DynamicDataFragment;
	}
	
	/**
	 * Determines if the current fragment implements the FragmentOnBackPressed interface.
	 * @return if the current fragment implements the FragmentOnBackPressed interface.
	 */
	public boolean isBackPressFragment() {
		return this.currentFragment instanceof FragmentOnBackPressed;
	}
	
	/**
	 * Allows a Fragment that implements the FragmentOnBackPressed interface to override the 
	 * onBackPressed at the Activity level essentially.
	 */
	@Override
	public void onBackPressed() {
		if(isBackPressFragment()){
			((FragmentOnBackPressed)this.currentFragment).onBackPressed();
		}else
			super.onBackPressed();
	}

	/**
	 * Send data to the dynamic data fragment
	 * @param bundle - bundle of data to pass to the fragment
	 */
	public void addDataToDynamicDataFragment(final Bundle bundle){
		((DynamicDataFragment)this.currentFragment).handleReceivedData(bundle);
	}
	
	/**
	 * Method used to show or hide Navigation Menu Button
	 * 
	 * @param value True to show Navigation Menu Button, false otherwise
	 */
	public void showNavigationMenuButton(final boolean value) {
		final ImageView navigationToggle = (ImageView) findViewById(R.id.navigation_button);

		if( null != navigationToggle) {
			if( value ) {
				navigationToggle.setVisibility(View.VISIBLE);
			} else {
				navigationToggle.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	/**
	 * Method used to enable or disable sliding navigation menu. If disabled
	 * then user will not be able to use a swipe gesture to see the navigation menu.
	 * 
	 * @param value True to enable sliding navigation menu, false otherwise.
	 */
	public void enableSlidingMenu(final boolean value) {
		final SlidingMenu slidingMenu = this.getSlidingMenu();

		if( value ) {
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		} else {
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}
	}

	/**
	 * @return True if Sliding Menu is enabled, false otherwise.
	 */
	public boolean isSlidingMenuEnabled() {
		final SlidingMenu slidingMenu = this.getSlidingMenu();

		return (slidingMenu.getTouchModeAbove() == SlidingMenu.TOUCHMODE_FULLSCREEN);
	}
}
