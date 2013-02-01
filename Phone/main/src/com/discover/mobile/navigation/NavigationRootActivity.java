package com.discover.mobile.navigation;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.discover.mobile.LoggedInRoboActivity;
import com.discover.mobile.R;
import com.discover.mobile.alert.ModalAlertWithOneButton;
import com.discover.mobile.alert.ModalConfirmationTop;
import com.discover.mobile.common.IntentExtraKey;

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
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
		if(null != resumeFragment && !wasPaused){
			getSupportFragmentManager().popBackStack();
			makeFragmentVisible(resumeFragment, false);
		}
		
		final Bundle extras = getIntent().getExtras();
		if(null != extras){
			handleIntentExtras(extras);
		}
	}
	
	/**
	 * Handle the extras passed in an intent
	 * @param extras - extras passed into the app
	 */
	private void handleIntentExtras(final Bundle extras) {
		if(!shouldShowModal){return;}
		final String screenType = extras.getString(IntentExtraKey.SCREEN_TYPE);
		if(null != screenType){
			final String userId = extras.getString(IntentExtraKey.UID);
			final String email = extras.getString(IntentExtraKey.EMAIL);
			final String lastFour = extras.getString(IntentExtraKey.ACCOUNT_LAST4);
			showConirmationModal(screenType, userId, email, lastFour);
		}
		
	}

	/**
	 * Show the confirmation modal
	 * @param screenType - screen type to be displayed in the modal
	 * @param userId - user ID to place in the modal
	 * @param email - email to place in the modal
	 * @param lastFour - last four account number digits to place in the modal
	 */
	private void showConirmationModal(final String screenType, final String userId,
			final String email, final String lastFour) {
		
		final ModalConfirmationTop top = new ModalConfirmationTop(this, null);
		final ModalAlertWithOneButton modal = new ModalAlertWithOneButton(this, top, null);
		top.setUserId(userId);
		top.setEmail(email);
		top.setLastFour(lastFour);
		top.setScreenType(screenType);
		top.getButton().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				modal.dismiss();	
				shouldShowModal = false;
			}
		});
		modal.show();
		
	}

	/**
	 * Save the state of the activity when it goes to the background.
	 * @param outState - bundle containing the out state of the activity
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState){
		wasPaused = true;
		this.getSupportFragmentManager().putFragment(outState, CURRENT_FRAGMENT, currentFragment);
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
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        int backStackCount = fragmentManager.getBackStackEntryCount();
        
        if( backStackCount == 1 ) {
            this.logout();
        } else {
            super.onBackPressed();
        }
            
    }

}
