package com.discover.mobile.forgotuidpassword;

import roboguice.activity.RoboListActivity;
import roboguice.inject.ContentView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.discover.mobile.R;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.ScreenType;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.login.CustomArrayAdapter;
import com.discover.mobile.login.register.AccountInformationActivity;

@ContentView(R.layout.forgot_login)
public class ForgotCredentialsActivity extends RoboListActivity {
	
	private static final String[] vals = {"Forgot User ID","Forgot Password","Forgot Both"};
	private static final String TAG = ForgotCredentialsActivity.class.getSimpleName();
	
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		TrackingHelper.trackPageView(AnalyticsPage.FORGOT_PASSWORD_MENU);
	}
	
	/*
	 * Using onResume so that if a user presses the back button
	 * the list fields will get reset instead of staying blue.
	 */
	@Override
	public void onResume(){
		super.onResume();
		setListAdapter(new CustomArrayAdapter(this, vals));
	}

	 @Override
	 protected void onListItemClick( final ListView l, final View v, final int position, final long id ) {  
		handleSelection(position);
	 }
	
	/*
	 * Navigate to the appropriate screen.
	 */

	public void handleSelection(final int position){
		Intent forgotSomethingScreen = null;
		
		switch (position){
		case 0:
			forgotSomethingScreen = new Intent(this, ForgotUserIdActivity.class);
			break;
		case 1:
			forgotSomethingScreen = new Intent(this, AccountInformationActivity.class);
			forgotSomethingScreen.putExtra(IntentExtraKey.SCREEN_TYPE, ScreenType.FORGOT_PASSWORD);
			break;
		case 2:
			forgotSomethingScreen = new Intent(this, AccountInformationActivity.class);
			forgotSomethingScreen.putExtra(IntentExtraKey.SCREEN_TYPE, ScreenType.FORGOT_BOTH);
			break;
		default:
			Log.e(TAG, "Invalid Screen Selection!");
		}
		
		if(forgotSomethingScreen != null)
			startActivity(forgotSomethingScreen);
	}
	
}
