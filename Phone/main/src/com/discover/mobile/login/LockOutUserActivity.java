package com.discover.mobile.login;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.IntentExtraKey;
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
		
		if (savedInstanceState == null)
			setScreenType();
	}
	
	@Override
	public void onBackPressed() {
	   Intent navToMain = new Intent(this, LoginActivity.class);
	   startActivity(navToMain);
	}
	
	private void setScreenType() {
		try {
			switch(getIntent().getExtras().getInt(IntentExtraKey.SCREEN_TYPE)) {
				case ScreenType.MAINTENANCE:
					// TODO find out analytics for maintenance mode
					// TODO reference maintenance text when error map set up
					errorTextView.setText("Maintenance Text...");
					break;
				case ScreenType.LOCKED_OUT_USER:
					TrackingHelper.trackPageView(AnalyticsPage.ACCOUNT_LOCKED);
					// TODO reference lock out text when error map set up
					errorTextView.setText("Locked out user Text...");
					break;
				case ScreenType.BAD_ACCOUNT_STATUS:
					// TODO reference lock out text when error map set up
					errorTextView.setText("Bad account status text...");
					break;
				case ScreenType.STRONG_AUTH_LOCKED_OUT:
					errorTitleText.setText(getString(
							R.string.account_security_title_text));
					errorTextView.setText(getString(
							R.string.account_security_locked_out));
					break;
				case ScreenType.ACCOUNT_LOCKED_FAILED_ATTEMPTS:
					errorTitleText.setText(getString(
							R.string.secure_login));
					errorTextView.setText(getString(
							R.string.max_attempts_exceeded_text));
					break;
				default:
					break;
			}
			
		} catch (final Exception e) {
			throw new UnsupportedOperationException("Need to declare what type of screen.", e);
		}
	}
}
