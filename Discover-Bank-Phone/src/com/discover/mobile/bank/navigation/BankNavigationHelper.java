package com.discover.mobile.bank.navigation;

import android.app.Activity;

import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.nav.NavigationRootActivity;

/**
 * Class contains several static utility methods to help with navigation between screen in the application.
 * 
 * @author henryoyuela
 *
 */
public final class BankNavigationHelper {

	/**
	 * This is a utility class and should not have a public or default constructor.
	 */
	private BankNavigationHelper() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Method used to determine if user is already viewing a screen that the user can navigate to
	 * via the menu section specified.
	 * 
	 * @param menuGroup Menu Group used to check if the current fragment displayed is part of that work-flow
	 * @param section Menu section used to check if the current fragment displayed is part of that work-flow.
	 * @return True if current fragment is reachable via the menu section specified, otherwise false.
	 */
	public static boolean isViewingMenuSection(final int menuGroup, final int menuSection) {
		boolean ret = false;
		
		final Activity activity = DiscoverActivityManager.getActiveActivity();

		/**Verify that the user is logged in and the NavigationRootActivity is the active activity*/
		if( activity instanceof NavigationRootActivity ) {
			final NavigationRootActivity navActivity = (NavigationRootActivity) activity;
		
			final BaseFragment fragment = navActivity.getCurrentContentFragment();
			
			/**Check if user is already in the work-flow specified by the menu section*/
			if( fragment != null &&
				fragment.getGroupMenuLocation() == menuGroup &&
				fragment.getSectionMenuLocation() == menuSection) {
				ret = true;
			}
		}
		
		return ret;
	}
	
	/**
	 * Utility method used to hide the sliding menu on the NavigationRootActivity if is the
	 * active Activity.
	 */
	public static void hideSlidingMenu() {
		final Activity activity = DiscoverActivityManager.getActiveActivity();

		/**Verify that the user is logged in and the NavigationRootActivity is the active activity*/
		if( activity instanceof NavigationRootActivity ) {
			final NavigationRootActivity navActivity = (NavigationRootActivity) activity;
			navActivity.hideSlidingMenuIfVisible();
		}
	}
}
