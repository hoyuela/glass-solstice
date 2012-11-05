package com.discover.mobile.register;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.login.LoggedInLandingPage;

public class AccountInformationConfirmationActivity extends Activity{
	
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_info_confirm);
		if (savedInstanceState == null) {
			Bundle extras = getIntent().getExtras();
        	if(extras != null) {
        		TextView userIdLabel = (TextView)findViewById(R.id.account_info_confirm_id_label);
        		TextView userEmailLabel = (TextView)findViewById(R.id.account_info_confirm_email_label);
        		TextView userAcctNbrLabel = (TextView)findViewById(R.id.account_info_confirm_account_label);
        		userIdLabel.setText((String)extras.get("id"));
        		userEmailLabel.setText((String)extras.get("email"));
        		userAcctNbrLabel.setText((String)extras.get("acctNbr"));
        	}
		}
		
		
	}
	
	public void navigateToHome(View v){
		Intent homeActivity = new Intent(this, LoggedInLandingPage.class);
		this.startActivity(homeActivity);
	}

}
