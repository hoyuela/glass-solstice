package com.discover.mobile.bank.navigation;

import java.util.Calendar;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.discover.mobile.bank.DynamicDataFragment;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.error.BankErrorHandler;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.paybills.SchedulePaymentFragment.OnPaymentCanceledListener;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.util.FragmentOnBackPressed;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.auth.KeepAlive;
import com.discover.mobile.common.error.ErrorHandler;
import com.discover.mobile.common.nav.NavigationRootActivity;
import com.discover.mobile.common.net.SessionTokenManager;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.OnClosedListener;

/**
 * Root activity for the application after login. This will transition fragment
 * on and off the screen as well as show the sliding bar as well as the action
 * bar.
 * 
 */
public class BankNavigationRootActivity extends NavigationRootActivity
implements OnPaymentCanceledListener {

	/** Allows access to and manual control of the soft keyboard. */
	private InputMethodManager imm;
	/** If a fragment error exists this will be set to true. */
	private boolean fragmentErrorShown = false;

	private final String BANK_USER_KEY = "bankUser";
	private final String BANK_SESSION_KEY = "session";

	/**
	 * Resume the activity to the state that it was when the activity went to
	 * the background
	 */
	@Override
	public void onResume() {
		super.onResume();

		updateMenuOnClose();
		
		compareLastTouchTimeAndUpdateSession();
	}

	/**
	 * Refreshes the sliding menu when its closed. This ensures that the highlighting on the menu is accurate
	 */
	private void updateMenuOnClose() {
		final SlidingMenu slidingMenu = getSlidingMenu();
		slidingMenu.setOnClosedListener(new OnClosedListener() {

			@Override
			public void onClosed() {
				final BaseFragment currentFragment = getCurrentContentFragment();
				highlightMenuItems(currentFragment.getGroupMenuLocation(), currentFragment.getSectionMenuLocation());

			}
		});
	}

	/**
	 * Start the activity
	 */
	@Override
	public void onStart(){
		super.onStart();

		/** Status bar should always be hidden for bank. It's possbile it will also go away card.
		 * This is a temp solution. If it goes away for card this code will be removed.  */
		final Fragment statusBar = this.getSupportFragmentManager().findFragmentById(R.id.status_bar);
		final FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
		ft.hide(statusBar);
		ft.commit();
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		super.onCreate(savedInstanceState);

		// Application was previously destroyed; reloading the BankUser
		// Singleton.
		if (BankUser.instance().getCustomerInfo() == null) {
			BankUser.instance().setBankUser(
					(BankUser) savedInstanceState
							.getSerializable(BANK_USER_KEY));
			BankUrlManager
					.setNewLinks(BankUser.instance().getCustomerInfo().links);
			SessionTokenManager.setToken(savedInstanceState
					.getString(BANK_SESSION_KEY));
		}
	}

	@Override
	public void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		BankUser.instance().getCustomerInfo().links = BankUrlManager.getLinks();
		outState.putSerializable(BANK_USER_KEY, BankUser.instance());
		outState.putString(BANK_SESSION_KEY, SessionTokenManager.getToken());
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
			compareLastTouchTimeAndUpdateSession();
		}

		// Don't consume event.
		return false;
	}

	/**
	 * Determines the current time and gets the time stored in globals. Then
	 * updates globals with the current time.
	 */
	private void compareLastTouchTimeAndUpdateSession() {
		final Calendar mCalendarInstance = Calendar.getInstance();

		final long previousTime = Globals.getOldTouchTimeInMillis();
		final long currentTime = mCalendarInstance.getTimeInMillis();

		if(!setIsUserTimedOut(previousTime, currentTime)) {
			KeepAlive.checkForRequiredSessionRefresh();
		}
		Globals.setOldTouchTimeInMillis(currentTime);
	}

	/**
	 * Determines whether or not the user is timed out.
	 * 
	 * @param previousTime
	 * @param currentTime
	 * @return true if the user is timed-out, false otherwise.
	 */
	private boolean setIsUserTimedOut(final long previousTime,
			final long currentTime) {
		// Previous value exists
		if (previousTime != 0) {
			final long difference = currentTime - previousTime;
			final float secs = (float) difference / 1000;

			// User has become inactive and will be set to timed-out.
			if ( secs > BankUrlManager.MAX_IDLE_TIME) {
				BankConductor.logoutUser(this);
				return true;
			}
		}
		return false;
	}

	@Override
	public int getBehindContentView() {
		// TODO Auto-generated method stub
		return R.layout.navigation_bank_menu_frame;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.discover.mobile.common.BaseFragmentActivity#getErrorHandler()
	 */
	@Override
	public ErrorHandler getErrorHandler() {
		return BankErrorHandler.getInstance();
	}

	/**
	 * Determines if the current fragment is attempting to load more
	 * 
	 * @return if the current fragment is attempting to load more
	 */
	public boolean isFragmentLoadingMore(){
		boolean isLoadingMore = false;
		if(currentFragment instanceof DynamicDataFragment){
			isLoadingMore = ((DynamicDataFragment)currentFragment).getIsLoadingMore();
		}
		return isLoadingMore;
	}

	/**
	 * Determines if the current fragment implements the FragmentOnBackPressed
	 * interface.
	 * 
	 * @return if the current fragment implements the FragmentOnBackPressed
	 *         interface.
	 */
	public boolean isBackPressFragment() {
		return currentFragment instanceof FragmentOnBackPressed;
	}

	/**
	 * Facade for FragmentOnBackPressed.isBackPressDisabled method. Used to determine
	 * if back press has been disbaled for the current fragment.
	 * 
	 * @return True if fragment does not allow back press, false otherwise.
	 */
	public boolean isBackPressDisabled() {
		return ( isBackPressFragment() && ((FragmentOnBackPressed)currentFragment).isBackPressDisabled());
	}

	/**
	 * Allows a Fragment that implements the FragmentOnBackPressed interface to
	 * override the onBackPressed at the Activity level essentially.
	 */
	@Override
	public void onBackPressed() {
		if( !isBackPressDisabled()) {
			super.onBackPressed();
		}
		else if(isBackPressFragment()){
			((FragmentOnBackPressed)currentFragment).onBackPressed();
		}
	}

	/**
	 * Send data to the dynamic data fragment
	 * 
	 * @param bundle
	 *            - bundle of data to pass to the fragment
	 */
	public void addDataToDynamicDataFragment(final Bundle bundle){
		((DynamicDataFragment)currentFragment).handleReceivedData(bundle);
	}

	/**
	 * Method used to show or hide Navigation Menu Button
	 * 
	 * @param value
	 *            True to show Navigation Menu Button, false otherwise
	 */
	public void showNavigationMenuButton(final boolean value) {
		final ImageView navigationToggle = (ImageView) findViewById(R.id.navigation_button);

		if (null != navigationToggle) {
			if (value) {
				navigationToggle.setVisibility(View.VISIBLE);
			} else {
				navigationToggle.setVisibility(View.INVISIBLE);
			}
		}
	}

	/**
	 * Method used to enable or disable sliding navigation menu. If disabled
	 * then user will not be able to use a swipe gesture to see the navigation
	 * menu.
	 * 
	 * @param value
	 *            True to enable sliding navigation menu, false otherwise.
	 */
	public void enableSlidingMenu(final boolean value) {
		final SlidingMenu slidingMenu = this.getSlidingMenu();

		if (value) {
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


	/**
	 * Method used to search for a fragment of a specific class type within the
	 * back stack.
	 * 
	 * @param fragmentClassType
	 *            Class type of a fragment being looked up in the back stack
	 * 
	 * @return Return the location of the fragment of the class type specified
	 *         in the back stack.
	 */
	public int getFragmentIndex(final Class<?> fragmentClassType) {
		int ret = -1;

		final FragmentManager fragManager = this.getSupportFragmentManager();
		final int fragCount = fragManager.getBackStackEntryCount();
		if (fragCount > 0) {
			for (int i = 0; i < fragCount; i++) {
				if (fragManager.getBackStackEntryAt(i).getName()
						.equals(fragmentClassType.getSimpleName())) {
					ret = i;
					break;
				}
			}
		}
		return ret;
	}

	/**
	 * Method used to pop everything from the FragmentActivity's back stack
	 * until reaching a fragment with the class type specified. The method will
	 * first look-up in the back-stack if one is found it will precede with
	 * poping the backstack until that fragment is reached.
	 * 
	 * @param fragmentClassType
	 *            Class type of the fragment to look for in the back stack.
	 */
	public boolean popTillFragment(final Class<?> fragmentClassType) {
		final FragmentManager fragManager = this.getSupportFragmentManager();
		boolean ret = false;
		
		/**
		 * Search for the fragment with the class type specified in the
		 * backstack
		 */
		final int fragIndex = getFragmentIndex(fragmentClassType);

		if( fragIndex != -1) {
			/**How many times the backstack will be popped in order to reach the fragment desired*/
			final int callsToPop =  (fragManager.getBackStackEntryCount() - 1) - fragIndex;
			for( int i = 0; i < callsToPop; i++ ) {
				fragManager.popBackStackImmediate();
			}
			
			ret = true;
		} 
		
		return ret;
	}

	/**
	 * Returns the InputMethodManager for the activity which is used to manually
	 * control the keyboard.
	 */
	public InputMethodManager getInputMethodManager() {
		return imm;
	}

	/**
	 * Returns error whether or not an error exists. Once this has been called
	 * the error is consumed.
	 * 
	 * @return true if an error exists, false otherwise.
	 */
	public boolean consumeFragmentError() {
		final boolean returnValue = fragmentErrorShown;
		fragmentErrorShown = false;
		return returnValue;
	}

	/**
	 * Called when a payment was canceled and a previous fragment needs to be
	 * informed.
	 */
	@Override
	public void onPaymentCanceled() {
		fragmentErrorShown = true;
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
