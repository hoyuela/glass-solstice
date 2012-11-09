package com.discover.mobile.navigation;

import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.view.MenuItem;
import com.discover.mobile.R;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.CanvasTransformer;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class NavigationMenuRootActivity extends SlidingFragmentActivity {
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// TEMP
		setTitle(R.string.app_name);
		
		setupNavMenuList();
		setupSlidingMenu();
		setupFirstVisibleFragment();
	}
	
	private void setupFirstVisibleFragment() {
		// TODO make production ready
		
		setContentView(R.layout.navigation_main_frame);
		
		// TEMP
		final HomeFragment homeFragment = new HomeFragment();
		replaceMainFragment(homeFragment, false);
	}
	
	private void replaceMainFragment(final Fragment newFragment, final boolean focus) {
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.navigation_main_frame, newFragment)
				.commit();
		
		if(focus)
			getSlidingMenu().showAbove();
	}
	
	private void setupNavMenuList() {
		setBehindContentView(R.layout.navigation_menu_frame);
		
		// TODO consider using a shared/persisted instance (to save state)
		final NavigationMenuFragment navMenuFragment = new NavigationMenuFragment();
		
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.navigation_menu_frame, navMenuFragment)
				.commit();
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	// TODO customize these values
	private void setupSlidingMenu() {
		final SlidingMenu slidingMenu = getSlidingMenu();
		slidingMenu.setShadowWidthRes(R.dimen.nav_menu_shadow_width);
		slidingMenu.setShadowDrawable(R.drawable.nav_menu_shadow);
		slidingMenu.setBehindOffsetRes(R.dimen.nav_menu_offset);
		slidingMenu.setFadeDegree(0.35f);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		slidingMenu.setBehindScrollScale(0.0f);
		slidingMenu.setBehindCanvasTransformer(new CanvasTransformer() {
			@Override
			public void transformCanvas(final Canvas canvas, final float percentOpen) {
				final float scale = (float) (percentOpen*0.25 + 0.75);
				canvas.scale(scale, scale, canvas.getWidth()/2, canvas.getHeight()/2);
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				toggle();
				return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
}
