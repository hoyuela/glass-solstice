package com.discover.mobile.navigation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.discover.mobile.LoggedInRoboActivity;
import com.discover.mobile.R;
import com.discover.mobile.common.CurrentSessionDetails;
import com.discover.mobile.push.register.PushNowAvailableFragment;

/**
 * Root activity for the application after login. This will transition fragment on and off the screen
 * as well as show the sliding bar as well as the action bar.
 *
 */
public class NavigationRootActivity extends LoggedInRoboActivity implements NavigationRoot {
	
	/**Fragment that needs to be resumed**/
	private Fragment resumeFragment;
	
	/**String that is the key to getting the current fragment out of the saved bundle.*/
	private static final String CURRENT_FRAGMENT = "currentFragment";
	
	/**String that is the key to getting the current fragment title out of the saved bundle.*/
	private static final String TITLE = "title";
	
	/**
	 * Create the activity
	 * @param savedInstatnceState - saved state of the activity
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setupFirstVisibleFragment();
		setUpCurrentFragment(savedInstanceState);
	}
	
	/**
	 * Sets up the fragment that was visible before the app went into the background
	 * @param savedInstanceState - bundle containing the state
	 */
	private void setUpCurrentFragment(final Bundle savedInstanceState) {
		if(null == savedInstanceState){return;}
		final Fragment fragment = this.getSupportFragmentManager().getFragment(savedInstanceState, CURRENT_FRAGMENT);
		setActionBarTitle(savedInstanceState.getString(TITLE));
		if(null != fragment){
			resumeFragment = fragment;
		}
	}

	/**
	 * Resume the activity to the state that it was when the activity went to the background
	 */
	@Override
	public void onResume(){
		super.onResume();
		
		if(resumeFragment != null)
			makeFragmentVisible(resumeFragment);
		
		if(!CurrentSessionDetails.getCurrentSessionDetails().isNotCurrentUserRegisteredForPush())
			makeFragmentVisible(new PushNowAvailableFragment());		
	}
	
	/**
	 * Save the state of the activity when it goes to the background.
	 * @param outState - bundle containing the out state of the activity
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState){
		this.getSupportFragmentManager().putFragment(outState, CURRENT_FRAGMENT, currentFragment);
		outState.putString(TITLE, getActionBarTitle());
		super.onSaveInstanceState(outState);
	}
	
	/**
	 * Set up the first visible fragment
	 */
	private void setupFirstVisibleFragment() {
		final FrameLayout contentView = new FrameLayout(this);
		contentView.setId(R.id.navigation_content);
		setContentView(contentView, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	
	/**
	 * Get the current title in the action bar 
	 * @return the current title in the action bar
	 */
	public String getActionBarTitle(){
		final TextView titleView= (TextView)findViewById(R.id.title_view);
		return titleView.getText().toString();
	}
}
