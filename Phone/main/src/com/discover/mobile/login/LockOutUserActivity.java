package com.discover.mobile.login;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.ScreenType;

@ContentView(R.layout.lock_out_user)
public class LockOutUserActivity extends RoboActivity {
	
	@InjectView(R.id.error_text_view)
	private TextView errorTextView;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (savedInstanceState == null)
			setScreenType();
	}
	
	private void setScreenType() {
		try {
			switch(getIntent().getExtras().getInt("ScreenType")) {
				case ScreenType.MAINTENANCE:
					// TODO reference maintenance text when error map set up
					errorTextView.setText("Maintenance Text...");
					break;
				case ScreenType.LOCKED_OUT_USER:
					// TODO reference lock out text when error map set up
					errorTextView.setText("Locked out user Text...");
					break;
			}
			
		} catch (final Exception e) {
			throw new UnsupportedOperationException("Need to declare what type of screen.");
		}
	}
}
