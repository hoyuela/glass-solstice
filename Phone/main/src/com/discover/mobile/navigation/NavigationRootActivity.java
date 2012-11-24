package com.discover.mobile.navigation;

import roboguice.inject.ContentView;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.discover.mobile.R;
import com.discover.mobile.RoboSlidingFragmentActivity;
import com.slidingmenu.lib.SlidingMenu;

@ContentView(R.layout.navigation_main_frame)
public class NavigationRootActivity extends RoboSlidingFragmentActivity implements NavigationRoot {
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setupNavMenuList();
		setupSlidingMenu();
//		setupFirstVisibleFragment();
	}
	
//	private void setupFirstVisibleFragment() {
//		setContentView(R.layout.navigation_main_frame);
//	}
	
	@Override
	public void replaceMainFragment(final Fragment newFragment, final boolean closeMenu) {
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.navigation_content, newFragment)
				.commit();
		
		if(closeMenu)
			getSlidingMenu().showAbove();
	}
	
	private void setupNavMenuList() {
		setBehindContentView(R.layout.navigation_menu_frame);
		
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		actionBar.setDisplayUseLogoEnabled(false);
//		actionBar.setDisplayShowTitleEnabled(false);

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
		
		// enable this for zoom
//		slidingMenu.setBehindScrollScale(0.0f);
//		slidingMenu.setBehindCanvasTransformer(new CanvasTransformer() {
//			@Override
//			public void transformCanvas(final Canvas canvas, final float percentOpen) {
//				final float scale = (float) (percentOpen*0.25 + 0.75);
//				canvas.scale(scale, scale, canvas.getWidth()/2, canvas.getHeight()/2);
//			}
//		});
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
		}
		
		return super.onOptionsItemSelected(item);
	}
	
}
