package com.discover.mobile.login;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.ScreenType;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;

@ContentView(R.layout.lock_out_user)
public class LockOutUserActivity extends RoboActivity {
	
	@InjectView(R.id.error_text_view)
	private TextView errorTextView;
	
	@InjectView(R.id.secure_card_login_label)
	private TextView errorTitleText;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setScreenType();
	}
	
	@Override
	public void onBackPressed() {
		final Intent navToMain = new Intent(this, LoginActivity.class);
		startActivity(navToMain);
	}
	
	private void setScreenType() {
		final ScreenType screenType = ScreenType.getExtraFromIntent(getIntent());
		if(screenType == null)
			throw new NullPointerException("No ScreenType found in intent");
		
		switch(screenType) {
			case MAINTENANCE:
				// TODO find out analytics for maintenance mode
				// TODO reference maintenance text when error map set up
				errorTextView.setText(Html.fromHtml("We're sorry. We are currently updating our system and cannot complete your request at this time. We apologize for any inconvenience. Please try again later or, for immediate assistance call <a href='tel:18003472683' class='eCertBlueLink'>1-800-347-2683</a>."));
				break;
				
			case LOCKED_OUT_USER:
				TrackingHelper.trackPageView(AnalyticsPage.ACCOUNT_LOCKED);
				// TODO reference lock out text when error map set up
				errorTextView.setText(Html.fromHtml("<p>For security purposes, your online account has been locked.<p/><p>Please call Discover Customer Service at <a href='tel:18882518003' class='eCertBlueLink'>1-888-251-8003</a> for information about accessing your account online.</p>"));
				break;
				
			case BAD_ACCOUNT_STATUS:
				// TODO reference lock out text when error map set up
				errorTextView.setText(R.string.zluba_error);
				break;
				
			case STRONG_AUTH_LOCKED_OUT:
				errorTitleText.setText(getString(R.string.account_security_title_text));
				errorTextView.setText(getString(R.string.account_security_locked_out));
				break;
				
			case ACCOUNT_LOCKED_FAILED_ATTEMPTS:
				errorTitleText.setText(getString(R.string.secure_login));
				errorTextView.setText(getString(R.string.max_attempts_exceeded_text));
				break;
				
			default:
				throw new UnsupportedOperationException("Unable to handle ScreenType: " + screenType);
		}
	}
}
