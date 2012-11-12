package com.discover.mobile.forgotuidpassword;

import java.net.HttpURLConnection;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.auth.InputValidator;
import com.discover.mobile.common.forgotuidpassword.ForgotUserIdCall;
import com.discover.mobile.common.forgotuidpassword.UserIdDetails;
import com.discover.mobile.common.net.json.MessageErrorResponse;
import com.discover.mobile.common.net.response.AsyncCallbackAdapter;
import com.discover.mobile.common.net.response.ErrorResponse;
import com.discover.mobile.login.register.AccountInformationConfirmationActivity;

@ContentView(R.layout.forgot_id)
public class ForgotUserIdActivity extends RoboActivity {
	
	private static final String TAG = ForgotUserIdActivity.class.getSimpleName();
	
	@InjectView(R.id.forgot_id_submit_button)
	Button submitButton;
	
	@InjectView(R.id.forgot_id_id_error_label)
	TextView idErrLabel;
	@InjectView(R.id.forgot_id_pass_error_label)
	TextView passErrLabel;
	@InjectView(R.id.forgot_id_id_field)
	EditText cardNum;
	@InjectView(R.id.forgot_id_password_field)
	EditText passText;

	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		TrackingHelper.trackPageView(AnalyticsPage.FORGOT_UID);
		
		setSubmitOnClick();
	}
	
	private void setSubmitOnClick() {
		submitButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(final View v){
				checkInputsAndSubmit();
			}
		});
	}
	
	public void goBack(View v){
		//finish() -> same action as pressing the hardware back button.
		finish();
	}
	
	public void checkInputsAndSubmit(){
		final InputValidator validator = new InputValidator();
		
		if(!validator.isCardAccountNumberValid(cardNum.getText().toString()))
			idErrLabel.setText(getString(R.string.invalid_value));
		else
			passErrLabel.setText(getString(R.string.empty));

		if(passText.getText().toString().isEmpty())
			passErrLabel.setText(getString(R.string.invalid_value));
		else
			passErrLabel.setText(getString(R.string.empty));

		if(validator.wasPassValid & validator.wasAccountNumberValid)
			doForgotUserIdCall();
	}
	
	private void doForgotUserIdCall() {
		final ProgressDialog progress = ProgressDialog.show(this, "Discover", "Loading...", true);
		
		final AsyncCallbackAdapter<UserIdDetails> callback = new AsyncCallbackAdapter<UserIdDetails>() {
			@Override
			public void success(final UserIdDetails value) {
				progress.dismiss();
				sendToConfirmationScreen(value.acctLast4, value.email, value.userId);
			}
			
			@Override
			public void failure(final Throwable error) {
				progress.dismiss();
				Log.e(TAG, "Error: " + error.getMessage());
				showOkAlertDialog("Error", error.getMessage());
			}

			@Override
			public boolean handleErrorResponse(final ErrorResponse errorResponse) {
				progress.dismiss();
				
				switch (errorResponse.getHttpStatusCode()) {
					case HttpURLConnection.HTTP_UNAUTHORIZED:
						idErrLabel.setText(getString(R.string.login_error));
						return true;
				}
				
				return false;
			}

			@Override
			public boolean handleMessageErrorResponse(final MessageErrorResponse messageErrorResponse) {
				if(messageErrorResponse.getHttpStatusCode() != HttpURLConnection.HTTP_FORBIDDEN)
					return false;
				
				progress.dismiss();
				clearInputs();
				
				idErrLabel.setText(messageErrorResponse.getMessage());
				
				return true;
			}
		};

		new ForgotUserIdCall(this, callback, cardNum.getText().toString(), passText.getText().toString()).submit();
	}
	
	private void showOkAlertDialog(final String title, final String message) {
		new AlertDialog.Builder(this)
			    .setTitle(title)
			    .setMessage(message)
			    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int which) {
						dialog.dismiss();
						finish();
					}
				})
			    .show();
	}
	
	private void sendToConfirmationScreen(final String last4, final String email, final String uid) {
		final Intent confirmationScreenIntent = 
				new Intent(ForgotUserIdActivity.this, AccountInformationConfirmationActivity.class);
		confirmationScreenIntent.putExtra(IntentExtraKey.ACCOUNT_LAST4, last4);
		confirmationScreenIntent.putExtra(IntentExtraKey.EMAIL, email);
		confirmationScreenIntent.putExtra(IntentExtraKey.UID, uid);
		TrackingHelper.trackPageView(AnalyticsPage.FOROGT_UID_CONFIRMATION);
		startActivity(confirmationScreenIntent);
	}
	
	public void clearInputs() {
		idErrLabel.setText("");
		passText.setText("");
		cardNum.setText("");
	}
	
}
