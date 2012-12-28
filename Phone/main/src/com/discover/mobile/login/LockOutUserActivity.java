package com.discover.mobile.login;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.widget.TextView;

import com.discover.mobile.R;
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
@ContentView(R.layout.login_locked_out)
public class LockOutUserActivity extends RoboActivity {

	/**
	 * The body text on the screen. Used for a long error message.
	 */
	@InjectView(R.id.error_text_view)
	private TextView errorTextView;

	/**
	 * The title of the error dialog. Defaults to "Secure Credit Card Login"
	 */
	public void setErrorTitleText(int resourceId) {
		errorTextView.setText(getString(resourceId));
	}

	/**
	 * The body of the error dialog.
	 * 
	 * @param resourceId
	 */
	public void setErrorText(int resourceId) {
		errorTextView.setText(getString(resourceId));
	}

	@InjectView(R.id.secure_card_login_label)
	private TextView errorTitleText;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Integer errorText = (Integer) this.getIntent().getExtras().getSerializable(IntentExtraKey.ERROR_TEXT_KEY);
		Integer errorTitleText = (Integer) this.getIntent().getExtras().getSerializable(IntentExtraKey.ERROR_TITLE_TEXT_KEY);
		if (errorText != null)
			setErrorText(errorText);
		if (errorTitleText != null)
			setErrorTitleText(errorTitleText);
	}

}
