package com.discover.mobile.bank.navigation;

import java.util.Calendar;

import android.view.MotionEvent;

import com.discover.mobile.R;
import com.discover.mobile.bank.BankNavigator;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.urlmanager.BankUrlManager;
import com.discover.mobile.navigation.NavigationRootActivity;

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
}
