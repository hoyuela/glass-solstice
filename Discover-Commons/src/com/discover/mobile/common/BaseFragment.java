package com.discover.mobile.common;

import roboguice.RoboGuice;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragment;
import com.discover.mobile.common.nav.NavigationRootActivity;

/**
 * Base fragment that all of the fragments in the app should extend
 * 
 * @author jthornton
 * 
 */
public abstract class BaseFragment extends SherlockFragment {

	/**Static int used to signify that the logo should be shown and the title should not be shown*/
	public static final int NO_TITLE = -1;

	/**
	 * Create the fragment
	 * 
	 * @param savedInstanceState
	 *            - bundle containing saved state of the fragment
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		RoboGuice.getInjector(getActivity()).injectMembersWithoutViews(this);
	}

	/**
	 * Create the view
	 * 
	 * @param view
	 *            - view of the fragment
	 * @param savedInstanceState
	 *            - bundle containing saved state of the fragment
	 */
	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		RoboGuice.getInjector(getActivity()).injectViewMembers(this);
	}

	/**
	 * When the app resumes, make sure the fragment shows the same
	 */
	@Override
	public void onResume() {
		super.onResume();
		final NavigationRootActivity activity = (NavigationRootActivity) this
				.getActivity();
		activity.setCurrentFragment(this);
		setActionBarTitle(getActionBarTitle());
		activity.highlightMenuItems(getGroupMenuLocation(), getSectionMenuLocation());
	}

	/**
	 * Show the logout alert dialog
	 */
	public void showCustomAlertDialog(final AlertDialog modal) {
		final LoggedInRoboActivity activity = (LoggedInRoboActivity) this
				.getActivity();
		activity.showCustomAlert(modal);
	}

	/**
	 * Make the fragment visible
	 * 
	 * @param fragment
	 *            - fragment to be made visible
	 */
	public void makeFragmentVisible(final BaseFragment fragment) {
		final NavigationRootActivity activity = (NavigationRootActivity) this
				.getActivity();
		activity.makeFragmentVisible(fragment);
	}
	
	public void makeFragmentVisible(final BaseFragment fragment, final boolean addToHistory) {
		final NavigationRootActivity activity = (NavigationRootActivity) this
				.getActivity();
		activity.makeFragmentVisible(fragment, addToHistory);
	}

	/**
	 * Set the title in the action bar for display
	 * 
	 * @param title
	 *            - title to show in the display
	 */
	public void setActionBarTitle(final int title) {
		if(NO_TITLE == title){
			this.showActionBarLogo();
		}else{
			final BaseFragmentActivity activity = (BaseFragmentActivity) this.getActivity();
			this.hideActionBarLogo();
			activity.setActionBarTitle(activity.getResources().getString(title));
		}

	}

	/**
	 * Set the visibility of the status bar
	 */
	public void setStatusBarVisibility() {
		final LoggedInRoboActivity activity = (LoggedInRoboActivity) this
				.getActivity();
		activity.setStatusBarVisbility();
	}

	/**
	 * Hides the TextView on the ActionBar and instead shows the Discover logo.
	 */
	public void showActionBarLogo() {
		final LoggedInRoboActivity activity = (LoggedInRoboActivity) this
				.getActivity();
		activity.showActionBarLogo();
	}

	/**
	 * Hides the Logo on the ActionBar and instead shows the TextView.
	 */
	public void hideActionBarLogo() {
		final LoggedInRoboActivity activity = (LoggedInRoboActivity) this
				.getActivity();
		activity.hideActionBarLogo();
	}

	/**
	 * Get the resource id of the string that should be shown in the action bar
	 * 
	 * @return the resource id of the string that should be shown in the action
	 *         bar
	 */
	public abstract int getActionBarTitle();

	/**
	 * Get the group that should be highlighted when the fragment is shown
	 */
	public abstract int getGroupMenuLocation();

	/**
	 * Get the sub section location under the group
	 */
	public abstract int getSectionMenuLocation();

	/**
	 * Used to show the provide feedback fragment
	 * 
	 * NOTE: This has not been implemented yet, because the provide feedback
	 * fragment has not been created
	 */
	public void showProvideFeedback() {
		// TODO: Implement this
	}

	/**
	 * Used by a fragment when it's additionally using the
	 * {@code ExtendingScrollView}. Implement when you need to perform an action
	 * upon reaching the bottom of this scroll view.
	 */
	public void scrollViewBottomReached() {
		// DO NOTHING, fragments *must* override this.
	}
}
