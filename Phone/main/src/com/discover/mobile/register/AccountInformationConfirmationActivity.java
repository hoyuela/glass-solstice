package com.discover.mobile.register;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.discover.mobile.R;
import com.discover.mobile.login.LoggedInLandingPage;

public class AccountInformationConfirmationActivity extends Activity{
	
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_info_confirm);

		
	}
	
	public void navigateToHome(View v){
		Intent homeActivity = new Intent(this, LoggedInLandingPage.class);
		this.startActivity(homeActivity);
	}

}
