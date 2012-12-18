package com.discover.mobile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.discover.mobile.alert.ModalAlertWithTwoButtons;
import com.discover.mobile.alert.ModalDefaultTopView;
import com.discover.mobile.alert.ModalLogoutBottom;
import com.discover.mobile.common.SharedPreferencesWrapper;
import com.discover.mobile.common.auth.LogOutCall;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.GenericAsyncCallback;
import com.discover.mobile.logout.LogOutErrorHandler;
import com.discover.mobile.logout.LogOutSuccessListener;
import com.slidingmenu.lib.SlidingMenu;

/**
 * This is used as the base activity for when the user has logged in.  Extending this will show the action bar
 * with the sliding bar as well as the title text and logout button.
 * 
 * @author jthornton
 *
 */
public abstract class LoggedInRoboActivity extends RoboSlidingFragmentActivity{
	
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
		
		navigationToggle.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				toggle();
			}
		});
		
		logout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				maybeShowModalAlert();
			}
		});	
    }
    
    /**
     * Show the modal if the user wants it shown
     */
    public void maybeShowModalAlert(){
		if(getValueFromSharedPrefs(SharedPreferencesWrapper.SHOW_LOGIN_MODAL, false)){
			logout();
		} else{
			showAlert(setUpLogoutAlert());
		}
	}
    
    /**
     * Set up the modal alert that will be displayed for logout confirmation
     * @return the modal alert that will be displayed for logout confirmation
     */
    private AlertDialog setUpLogoutAlert() {
    	final ModalDefaultTopView topView = new ModalDefaultTopView(this, null);
		final ModalLogoutBottom bottomView = new ModalLogoutBottom(this, null);
		final ModalAlertWithTwoButtons alert = new ModalAlertWithTwoButtons(this, topView, bottomView);
		topView.setTitle(R.string.logout_confirm_title);
		topView.setContent(R.string.logout_confirm_text);
		bottomView.setOkButtonText(R.string.logout_ok_button_text);
		bottomView.setCancelButtonText(R.string.logout_cancel_button_text);
		bottomView.getOkButton().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				saveToSharedPrefs(SharedPreferencesWrapper.SHOW_LOGIN_MODAL, bottomView.isShowAgainSelected());
				logout();
			}
		});
		
		bottomView.getCancelButton().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				alert.dismiss();
			}
		});
		return alert;
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
				.withErrorResponseHandler(new LogOutErrorHandler(this))
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
	 * Set the title in the action bar for display
	 * @param title - title to show in the display
	 */
	public void setActionBarTitle(final String title){
		final TextView titleView= (TextView)findViewById(R.id.title_view);
		titleView.setText(title);
	}
}
