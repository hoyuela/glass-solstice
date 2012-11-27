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
import com.discover.mobile.RoboSlidingFragmentActivity;
import com.slidingmenu.lib.SlidingMenu;

public class NavigationRootActivity extends RoboSlidingFragmentActivity implements NavigationRoot {
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setupNavMenuList();
		setupSlidingMenu();
		setupFirstVisibleFragment();
	}
	
	private void setupFirstVisibleFragment() {
		final FrameLayout contentView = new FrameLayout(this);
		contentView.setId(R.id.navigation_content);
		setContentView(contentView, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	
	@Override
	public void makeFragmentVisible(final Fragment fragment) {
		setVisibleFragment(fragment);
		
		hideSlidingMenuIfVisible();
	}
	
	private void setVisibleFragment(final Fragment fragment) {
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.navigation_content, fragment)
				.commit();
	}
	
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
		slidingMenu.setFadeDegree(0.35f);
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
	
}
