package com.discover.mobile;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.discover.mobile.alert.LogoutModalAlert;
import com.discover.mobile.common.SharedPreferencesKey;
import com.discover.mobile.common.auth.LogOutCall;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.GenericAsyncCallback;
import com.discover.mobile.logout.LogOutErrorHandler;
import com.discover.mobile.logout.LogOutSuccessListner;

/**
 * 
 * @author jthornton
 *
 */
public class LoggedInRoboActvity extends RoboSlidingFragmentActivity{

	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		showActionBar();
	}
	
	 /**
     *	Show the action bar with the custom layout
     */
    public void showActionBar(){
    	setBehindContentView(R.layout.navigation_menu_frame);
		
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setCustomView(getLayoutInflater().inflate(R.layout.action_bar_menu_layout, null));
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		
		final TextView titleView = (TextView)findViewById(R.id.title_view);
		final ImageView navigationToggle = (ImageView)this.findViewById(R.id.navigation_button);
		final Button logout = (Button)this.findViewById(R.id.logout_button);
		
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
				logout();
			}
		});	
    }
    
    public void logout(){
		if(getValueFromSharedPrefs(SharedPreferencesKey.SHOW_LOGIN_MODAL, false)){
			clearSession();
		} else{
			final LogoutModalAlert alert = new LogoutModalAlert(this);
			alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
			alert.show();
			alert.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			alert.setOkClickListener(new OnClickListener(){
				@Override
				public void onClick(final View v) {
					saveToSharedPrefs(SharedPreferencesKey.SHOW_LOGIN_MODAL, alert.getShowAgain());
					clearSession();
				}
			});
		}
	}
    
    public void clearSession(){
		final AsyncCallback<Object> callback = 
				GenericAsyncCallback.<Object>builder(this)
				.showProgressDialog(getResources().getString(R.string.push_progress_get_title), 
									getResources().getString(R.string.push_progress_registration_loading), 
									true)
				.withSuccessListener(new LogOutSuccessListner(this))
				.withErrorResponseHandler(new LogOutErrorHandler(this))
				.build();
	
		new LogOutCall(this, callback).submit();
	}   
}
