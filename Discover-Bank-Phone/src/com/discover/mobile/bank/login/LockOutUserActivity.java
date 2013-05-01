package com.discover.mobile.bank.login;

import roboguice.activity.RoboActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.discover.mobile.bank.R;
import com.discover.mobile.common.IntentExtraKey;

/**
 * LockOutUserActivity A modal style screen that notifies the user of some error
 * that is preventing them from using the application further.
 * 
 * This class handles a ScreenType parameter from an intent extra and sets the
 * text on this screen to display the proper error message.
 * 
 * @author scottseward, ekaram
 * 
 */
public class LockOutUserActivity extends RoboActivity {

	/**
	 * The body text on the screen. Used for a long error message.
	 */
	
	private TextView errorTextView;

	/**
	 * The title of the error dialog. Defaults to "Secure Credit Card Login"
	 */
	public void setErrorTitleText(final int resourceId) {
		errorTextView.setText(getString(resourceId));
	}

	/**
	 * The body of the error dialog.
	 * 
	 * @param resourceId
	 */
	public void setErrorText(final int resourceId) {
		errorTextView.setText(getString(resourceId));
	}


	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_locked_out);
		errorTextView = (TextView) findViewById(R.id.error_text_view);
		
		final Bundle extras = this.getIntent().getExtras();
		final Integer errorText = (Integer) extras.getSerializable(IntentExtraKey.ERROR_TEXT_KEY);
		final Integer errorTitleText = (Integer) extras.getSerializable(IntentExtraKey.ERROR_TITLE_TEXT_KEY);
		
		if (errorText != null){
			setErrorText(errorText);
		}
		if (errorTitleText != null){
			setErrorTitleText(errorTitleText);
		}
	}
	
	@Override
	public void onBackPressed() {
		final Intent loginScreen = new Intent(this, LoginActivity.class);
		startActivity(loginScreen);
		this.finish();
	}

}
