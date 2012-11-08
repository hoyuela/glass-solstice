package com.discover.mobile.forgotuidpassword;

import roboguice.activity.RoboListActivity;
import roboguice.inject.ContentView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.discover.mobile.R;
import com.discover.mobile.login.CustomArrayAdapter;
import com.discover.mobile.register.AccountInformationActivity;

@ContentView(R.layout.forgot_login)
public class ForgotCredentialsActivity extends RoboListActivity {
	private String[] vals = {"Forgot User ID","Forgot Password","Forgot Both"};
	private static final String TAG = ForgotCredentialsActivity.class.getSimpleName();
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
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
	 protected void onListItemClick( ListView l, View v, int position, long id ) {  
		handleSelection(position);
	 }
	
	/*
	 * Navigate to the appropriate screen.
	 */
	public void handleSelection(int position){
		Intent forgotSomethingScreen = null;
		
		switch (position){
		case 0:
			forgotSomethingScreen = 
			new Intent(this, ForgotUserIdActivity.class);
			break;
		case 1:
			forgotSomethingScreen = 
			new Intent(this, AccountInformationActivity.class);
			forgotSomethingScreen.putExtra("ScreenType", "Forgot Password");
			break;
		case 2:
			forgotSomethingScreen = 
			new Intent(this, AccountInformationActivity.class);
			forgotSomethingScreen.putExtra("ScreenType", "Forgot Both");
			break;
		default:
			Log.e(TAG, "Invalid Screen Selection!");
		}
		
		if(forgotSomethingScreen != null)
			startActivity(forgotSomethingScreen);
	
	}
	
}
