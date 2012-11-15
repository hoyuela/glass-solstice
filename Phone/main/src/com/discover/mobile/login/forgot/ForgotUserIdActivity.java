package com.discover.mobile.login.forgot;

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
import com.discover.mobile.common.ScreenType;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.auth.InputValidator;
import com.discover.mobile.common.auth.forgot.ForgotUserIdCall;
import com.discover.mobile.common.auth.forgot.UserIdDetails;
import com.discover.mobile.common.net.json.MessageErrorResponse;
import com.discover.mobile.common.net.response.AsyncCallbackAdapter;
import com.discover.mobile.common.net.response.ErrorResponse;
import com.discover.mobile.login.LockOutUserActivity;
import com.discover.mobile.login.register.AccountInformationConfirmationActivity;

@ContentView(R.layout.forgot_id)
public class ForgotUserIdActivity extends RoboActivity {
	
	private static final String TAG = ForgotUserIdActivity.class.getSimpleName();
	
	@InjectView(R.id.forgot_id_submit_button)
	private Button submitButton;
	
	@InjectView(R.id.forgot_id_submission_error_label)
	private TextView mainErrLabel;
	
	@InjectView(R.id.forgot_id_id_error_label)
	private TextView idErrLabel;
	
	@InjectView(R.id.forgot_id_pass_error_label)
	private TextView passErrLabel;
	
	@InjectView(R.id.forgot_id_id_field)
	private EditText cardNum;
	
	@InjectView(R.id.forgot_id_password_field)
	private EditText passText;
	
	@InjectView(R.id.account_info_cancel_label)
	private TextView cancelLabel;

	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		// FIXME Gives null pointer exception.
//		TrackingHelper.trackPageView(AnalyticsPage.FORGOT_UID);
		
		setOnClickActions();
	}
	
	private void setOnClickActions() {
		submitButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(final View v){
				checkInputsAndSubmit();
			}
		});
		
		cancelLabel.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(final View v){
				goBack();
			}
		});
	}
	
	public void goBack() {
		finish();
	}
	
	private void hideAllErrorLabels(){
		hideLabel(idErrLabel);
		hideLabel(passErrLabel);
		hideLabel(mainErrLabel);
	}
	
	private void hideLabel(View v) {
		v.setVisibility(View.GONE);
	}
	
	private void showLabel(View v) {
		v.setVisibility(View.VISIBLE);
	}
	
	public void checkInputsAndSubmit(){
		final InputValidator validator = new InputValidator();
		
		if(!validator.isCardAccountNumberValid(cardNum.getText().toString()))
			showLabel(idErrLabel);
		else
			hideLabel(idErrLabel);

		if(passText.getText().toString().isEmpty())
			showLabel(passErrLabel);
		else
			hideLabel(passErrLabel);

		if(validator.wasAccountNumberValid && !passText.getText().toString().isEmpty())
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
						mainErrLabel.setText(getString(R.string.login_error));
						showLabel(mainErrLabel);
						return true;
//					case HttpURLConnection.HTTP_UNAVAILABLE:
//						//FIXME add service unavailable to screen types/error screens.
//						sendToErrorPage(ScreenType.SERVICE_UNAVAILABLE);
//						return true;
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
				
				// FIXME make named constants
				switch (messageErrorResponse.getMessageStatusCode()){
					case 1907:
					case 1102: //User's Account has an invalid online status.
						sendToErrorPage(ScreenType.BAD_ACCOUNT_STATUS);
						return true;
						
					case 1910:
						sendToErrorPage(ScreenType.LOCKED_OUT_USER);
						return true;
					default:
						break;
				}

				
				return true;
			}
		};

		new ForgotUserIdCall(this, callback, cardNum.getText().toString(), passText.getText().toString()).submit();
	}
	
	private void sendToErrorPage(final ScreenType screenType) {
		final Intent maintenancePageIntent = new Intent(this, LockOutUserActivity.class);
		screenType.addExtraToIntent(maintenancePageIntent);
		startActivity(maintenancePageIntent);
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
		hideAllErrorLabels();
		passText.setText("");
		cardNum.setText("");
	}
	
}
