package com.discover.mobile.login.register;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.login.LoggedInLandingPage;

public class AccountInformationConfirmationActivity extends RoboActivity {
	
	@InjectView(R.id.account_info_confirm_id_label)
	TextView userIdLabel;
	@InjectView(R.id.account_info_confirm_email_label)
	TextView userEmailLabel;
	@InjectView(R.id.account_info_confirm_account_label)
	TextView userAcctNbrLabel;
	@InjectView(R.id.account_info_confirm_step_title_label)
	TextView titleLabel;
	@InjectView(R.id.account_info_confirm_first_paragraph_label)
	TextView firstParagraph;
	
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_info_confirm);
		if (savedInstanceState == null) {
			final Bundle extras = getIntent().getExtras();
        	if(extras != null) {
        		userIdLabel.setText(extras.getString(IntentExtraKey.UID));
        		userEmailLabel.setText(extras.getString(IntentExtraKey.EMAIL));
        		userAcctNbrLabel.setText(extras.getString(IntentExtraKey.ACCOUNT_LAST4));
        		if("forgotPass".equals(extras.getString("ScreenType"))){
        			titleLabel.setText(
        					R.string.password_confirmation_title_text);
        			firstParagraph.setText(
        					R.string.password_confirmation_changed_text);
        		}
        		else if("forgotBoth".equals(extras.getString("ScreenType"))){
        			titleLabel.setText(R.string.forgot_both_title_text);
        			firstParagraph.setText(R.string.forgot_both_changed_text);
        		}
        	}
		}
	}
	
	public void navigateToHome(final View v){
		final Intent homeActivity = new Intent(this, LoggedInLandingPage.class);
		this.startActivity(homeActivity);
	}

}