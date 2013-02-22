package com.discover.mobile.common.nav;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.TextView;

import com.discover.mobile.common.LoggedInRoboActivity;
import com.discover.mobile.common.R;

/**
 * Root activity for the application after login. This will transition fragment on and off the screen
 * as well as show the sliding bar as well as the action bar.
 *
 */
public abstract class NavigationRootActivity extends LoggedInRoboActivity implements NavigationRoot {
	
	/**Fragment that needs to be resumed**/
	protected Fragment resumeFragment;
	
	/**String that is the key to getting the current fragment out of the saved bundle.*/
	private static final String CURRENT_FRAGMENT = "currentFragment";
	
	/**String that is the key to getting the current fragment title out of the saved bundle.*/
	private static final String TITLE = "title";
	
	/**String to get modal state*/
	private static final String MODAL_STATE = "modalState";

	private static final String TAG = "Navigation";
	
	/**Boolean to show the modal*/
	protected boolean shouldShowModal = true;
	
	/**
	 * Boolean set to true when the app was paused. If the fragment was paused this will stay true, but if the
	 * screen was rotated this will be recreated as false.
	 */
	protected boolean wasPaused = false;
	
	
	/**
	 * Create the activity
	 * @param savedInstatnceState - saved state of the activity
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupFirstVisibleFragment();
		setUpCurrentFragment(savedInstanceState);
		setStatusBarVisbility();
		NavigationIndex.clearAll();
		
	}
	
	
	
	/**
	 * Sets up the fragment that was visible before the app went into the background
	 * @param savedInstanceState - bundle containing the state
	 */
	private void setUpCurrentFragment(final Bundle savedInstanceState) {
		if(null == savedInstanceState){return;}
		shouldShowModal = savedInstanceState.getBoolean(MODAL_STATE, true);
		final Fragment fragment = this.getSupportFragmentManager().getFragment(savedInstanceState, CURRENT_FRAGMENT);
		if(null != fragment){
			resumeFragment = fragment;
			setActionBarTitle(savedInstanceState.getString(TITLE));
		}
	}

	/**
	 * Resume the activity to the state that it was when the activity went to the background
	 */
	@Override
	public void onResume(){
		super.onResume();
		if(null != resumeFragment && !wasPaused){
			getSupportFragmentManager().popBackStack();
			makeFragmentVisible(resumeFragment, false);
		}
		
	}

	

	/**
	 * Save the state of the activity when it goes to the background.
	 * @param outState - bundle containing the out state of the activity
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState){
		wasPaused = true;
		
		/**Crashes here when fragment is not in the backstack, by surrounding with try issue goes away*/
		try {
			this.getSupportFragmentManager().putFragment(outState, CURRENT_FRAGMENT, currentFragment);
		}catch(final Exception ex) {
			if( Log.isLoggable(TAG, Log.ERROR)) {
				Log.e(TAG, "An unexpected error occurred when attempting to store state of applicaiton");
			}
		}
		
		outState.putString(TITLE, getActionBarTitle());
		outState.putBoolean(MODAL_STATE, shouldShowModal);
		super.onSaveInstanceState(outState);
	}
	
	/**
	 * Set up the first visible fragment
	 */
	private void setupFirstVisibleFragment() {
		/**
		 * Loading the content_view layout as the first fragment. This layout contains a frame view
		 * that will handle swapping the fragments in and out as well as a static fragment for the 
		 * status bar.
		 */
		setContentView(R.layout.content_view);
	}
	
	/**
	 * Get the current title in the action bar 
	 * @return the current title in the action bar
	 */
	public String getActionBarTitle(){
		final TextView titleView= (TextView)findViewById(R.id.title_view);
		return titleView.getText().toString();
	}
	
	@Override
    public void onBackPressed() {
        final FragmentManager fragmentManager = this.getSupportFragmentManager();
        final int backStackCount = fragmentManager.getBackStackEntryCount();
        
        //Only handle back press if there is a fragment in the stack
        //Otherwise ignore the back press as we do not want to close the application via
        //Navigation root activity, it should only be closed from the logged in page
        if( backStackCount > 1 ) {
            super.onBackPressed();
        }
            
    }

}
