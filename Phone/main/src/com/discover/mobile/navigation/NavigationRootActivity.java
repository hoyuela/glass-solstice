package com.discover.mobile.navigation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.discover.mobile.R;
import com.discover.mobile.RoboSherlockFragment;
import com.discover.mobile.RoboSlidingFragmentActivity;
import com.discover.mobile.common.CurrentSessionDetails;
import com.discover.mobile.push.PushNowAvailableFragment;
import com.slidingmenu.lib.SlidingMenu;

public class NavigationRootActivity extends RoboSlidingFragmentActivity implements NavigationRoot {
	
	/**Pulled out variable for the fade of the sliding menu*/
	private static final float FADE = 0.35f;
	
	/**Fragment that is currently being shown to the user*/
	private Fragment currentFragment;
	
	/**Fragment that needs to be resumed**/
	private Fragment resumeFragment;
	
	/**String that is the key to getting the current fragment out of the saved bundle.*/
	private static final String CURRENT_FRAGMENT = "currentFragment";
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setupNavMenuList();
		setupSlidingMenu();
		setupFirstVisibleFragment();
		setUpCurrentFragment(savedInstanceState);
		
	}
	
	private void setUpCurrentFragment(final Bundle savedInstanceState) {
		if(null == savedInstanceState){return;}
		final Fragment fragment = this.getSupportFragmentManager().getFragment(savedInstanceState, CURRENT_FRAGMENT);
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
	 * @param outState - bundle containgin the out state of the acitivty
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState){
		this.getSupportFragmentManager().putFragment(outState, CURRENT_FRAGMENT, currentFragment);
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
	 * Make the fragment visible
	 * @param fragment - fragment to be made visible
	 */
	@Override
	public void makeFragmentVisible(final Fragment fragment) {
		setVisibleFragment(fragment);
		hideSlidingMenuIfVisible();
	}
	
	/**
	 * Sets the fragment seen by the user
	 * @param fragment - fragment to be shown
	 */
	private void setVisibleFragment(final Fragment fragment) {
		this.currentFragment = fragment;
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.navigation_content, fragment)
				//Adds the class name and fragment to the back stack
				.addToBackStack(fragment.getClass().getSimpleName())
				.commit();
		hideSlidingMenuIfVisible();
	}
	
	/**
	 * Hides the sliding menu is it is currently visible
	 */
	private void hideSlidingMenuIfVisible() {
		final SlidingMenu slidingMenu = getSlidingMenu();
		if(slidingMenu.isBehindShowing())
			slidingMenu.showAbove();
	}
	
	private void setupNavMenuList() {
		setBehindContentView(R.layout.navigation_menu_frame);
		
		final ActionBar actionBar = getSupportActionBar();
		
		// TEMP
		actionBar.setDisplayHomeAsUpEnabled(true);

		// TEMP
		actionBar.setDisplayUseLogoEnabled(false);
//		actionBar.setDisplayShowTitleEnabled(false);
		
		// TEMP
//		actionBar.setTitle(R.string.member_title_text);
		actionBar.setTitle("John Doe");
		actionBar.setSubtitle("Card Ending 4545");
	}
	
	// TODO customize these values
	private void setupSlidingMenu() {
		final SlidingMenu slidingMenu = getSlidingMenu();
		slidingMenu.setShadowWidthRes(R.dimen.nav_menu_shadow_width);
		slidingMenu.setShadowDrawable(R.drawable.nav_menu_shadow);
		slidingMenu.setBehindOffsetRes(R.dimen.nav_menu_offset);
		slidingMenu.setFadeDegree(FADE);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
	}
	
	// TEMP
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		final MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.nav_root_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				toggle();
				return true;
				
			// TODO
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Set the current fragment that is being shown
	 * @param fragment - fragment that is currently shown
	 */
	public void setCurrentFragment(final RoboSherlockFragment fragment){
		this.currentFragment = fragment;
	}
	
}
