package com.discover.mobile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.discover.mobile.common.SharedPreferencesWrapper;
import com.discover.mobile.common.auth.LogOutCall;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.GenericAsyncCallback;
import com.discover.mobile.login.BaseErrorResponseHandler;
import com.discover.mobile.logout.LogOutSuccessListener;
import com.slidingmenu.lib.SlidingMenu;

/**
 * This is used as the base activity for when the user has logged in.  Extending this will show the action bar
 * with the sliding bar as well as the title text and logout button.
 * 
 * @author jthornton
 *
 */
public abstract class LoggedInRoboActivity extends BaseFragmentActivity{
	
	/**Pulled out variable for the fade of the sliding menu*/
	private static final float FADE = 0.35f;

	/**
	 * Create the activity, set up the action bar and sliding menu
	 * @param savedInstanceState - saved State of the activity
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		showActionBar();
		setupSlidingMenu();
	}
	
	 /**
     *	Show the action bar with the custom layout
     */
    public void showActionBar(){
    	setBehindContentView(R.layout.navigation_menu_frame);
		
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setCustomView(getLayoutInflater().inflate(R.layout.action_bar_menu_layout, null));
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		
		final TextView titleView = (TextView) findViewById(R.id.title_view);
		final ImageView navigationToggle = (ImageView) findViewById(R.id.navigation_button);
		final Button logout = (Button) findViewById(R.id.logout_button);
		
		navigationToggle.setVisibility(View.VISIBLE);
		logout.setVisibility(View.VISIBLE);
		navigationToggle.setVisibility(View.VISIBLE);
		titleView.setVisibility(View.VISIBLE);
		
		titleView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				updateStatusBarVisibility();
			}
		});
		
		navigationToggle.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				toggle();
			}
		});
		
		logout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				logout();
			}
		});	
    }    

	/**
     * Log the user out
     */
    public void logout(){
		final AsyncCallback<Object> callback = 
				GenericAsyncCallback.<Object>builder(this)
				.showProgressDialog(getResources().getString(R.string.push_progress_get_title), 
									getResources().getString(R.string.push_progress_registration_loading), 
									true)
				.withSuccessListener(new LogOutSuccessListener(this))
				.withErrorResponseHandler(new BaseErrorResponseHandler((ErrorHandlerUi) this))
				.build();
	
		new LogOutCall(this, callback).submit();
	} 
    
	/**
	 * Set up and style the sliding menu
	 */
	private void setupSlidingMenu() {
		// TODO customize these values
		final SlidingMenu slidingMenu = getSlidingMenu();
		slidingMenu.setShadowWidthRes(R.dimen.nav_menu_shadow_width);
		slidingMenu.setShadowDrawable(R.drawable.nav_menu_shadow);
		slidingMenu.setBehindOffsetRes(R.dimen.nav_menu_offset);
		slidingMenu.setFadeDegree(FADE);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
	}
	
	/**
	 * Checks the shared pref to get the visibility for the status bar and 
	 * sets the visibility
	 */
	public void setStatusBarVisbility(){
		final FragmentTransaction ft = this.getSupportFragmentManager()
				.beginTransaction();
		final boolean statusBarVisitility = getValueFromSharedPrefs(SharedPreferencesWrapper.STATUS_BAR_VISIBILITY, true);
		final Fragment statusBar = this.getSupportFragmentManager().findFragmentById(
				R.id.status_bar);
		
		/**
		 * If its set to false hide the fragment, else show it.
		 */
		if (!statusBarVisitility){
			ft.hide(statusBar);
		}else {
			ft.show(statusBar);
		}
		ft.commit();
	}
	
	/**
	 * Updates the shared preference for the status bar visibility and then calls 
	 * setStatusBarVisibility to update the visibility
	 * @param visible - boolean for setting the shared pref
	 */
	public void updateStatusBarVisibility(){
		final Fragment statusBar = this.getSupportFragmentManager().findFragmentById(
				R.id.status_bar);
		boolean visible = true;
		if (statusBar.isVisible()){
			visible = false;
		}else if (statusBar.isHidden()){
			visible=true;
		}
		saveToSharedPrefs(SharedPreferencesWrapper.STATUS_BAR_VISIBILITY, visible);
		setStatusBarVisbility();
	}
}
