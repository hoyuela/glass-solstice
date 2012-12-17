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

@ContentView(R.layout.login_locked_out)
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
				
			case LOCKED_OUT_USER:
				TrackingHelper.trackPageView(AnalyticsPage.ACCOUNT_LOCKED);
				// TODO reference lock out text when error map set up
				errorTextView.setText(R.string.locked_account);
				break;
				
			case BAD_ACCOUNT_STATUS:
				// TODO reference lock out text when error map set up
				errorTextView.setText(R.string.zluba_error);
				break;
				
			case STRONG_AUTH_LOCKED_OUT:
				errorTitleText.setText(getString(R.string.account_security_title_text));
				errorTextView.setText(getString(R.string.account_security_locked_out));
				break;
				
			case STRONG_AUTH_NOT_ENROLLED:
				errorTitleText.setText(getString(R.string.account_security_title_text));
				errorTextView.setText(getString(R.string.account_security_not_enrolled));
				errorTextView.setTextColor(getResources().getColor(R.color.black));
				break;
				
			case ACCOUNT_LOCKED_FAILED_ATTEMPTS:
				errorTitleText.setText(getString(R.string.secure_login));
				errorTextView.setText(getString(R.string.max_attempts_exceeded_text));
				break;
			
			case ACCOUNT_NUMBER_REREGISTERED:
				setAlertBodyText(R.string.account_number_reregistered);
				break;
			
			case ACCOUNT_NOT_YET_SETUP:
				errorTitleText.setText(getString(R.string.secure_login));
				errorTextView.setText(getString(R.string.account_setup_pending));
				break;
				
			case ACCOUNT_NUMBER_CHANGED:
				setAlertBodyText(R.string.account_number_changed);
				break;
				
			case INTERNAL_SERVER_ERROR_500:
				setAlertBodyText(R.string.internal_server_error_500);
				errorTextView.setTextColor(getResources().getColor(R.color.black));
				break;
			
			case INTERNAL_SERVER_ERROR_503:
				setAlertBodyText(R.string.internal_server_error_503);
				errorTextView.setTextColor(getResources().getColor(R.color.black));
				break;
				
			case HTTP_FORBIDDEN:
				setAlertBodyText(R.string.forbidden_403);
				break;
				
			case UNSCHEDULED_MAINTENANCE:
			case TEMPORARY_OUTAGE:
				errorTextView.setTextColor(getResources().getColor(R.color.black));
				setAlertBodyText(R.string.temporary_outage);
				break;
			
			case SCHEDULED_MAINTENANCE:
				errorTextView.setTextColor(getResources().getColor(R.color.black));
				setAlertBodyText(R.string.planned_outage_one);
				break;
			
			case NO_DATA_FOUND:
				setAlertBodyText(R.string.no_data_found);
				break;
				
			case NOT_PRIMARY_CARDHOLDER:
				errorTitleText.setText(R.string.contact_customer_service);
				setAlertBodyText(R.string.not_primary_cardholder);
				break;
				
			default:
				throw new UnsupportedOperationException("Unable to handle ScreenType: " + screenType);
		}
	}
	
	public void setAlertBodyText( final int bodyText ){
		errorTextView.setText( Html.fromHtml(getString(bodyText)));
	}
}
