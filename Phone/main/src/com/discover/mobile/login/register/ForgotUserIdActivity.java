package com.discover.mobile.login.register;

import static com.discover.mobile.common.StandardErrorCodes.AUTH_BAD_ACCOUNT_STATUS;
import static com.discover.mobile.common.StandardErrorCodes.BAD_ACCOUNT_STATUS;
import static com.discover.mobile.common.StandardErrorCodes.MAX_LOGIN_ATTEMPTS;
import static com.discover.mobile.common.StandardErrorCodes.STRONG_AUTH_NOT_ENROLLED;

import java.net.HttpURLConnection;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
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
import com.discover.mobile.common.callback.AsyncCallbackAdapter;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.discover.mobile.common.net.json.JsonMessageErrorResponse;
import com.discover.mobile.login.LockOutUserActivity;
import com.discover.mobile.navigation.NavigationRootActivity;

@ContentView(R.layout.register_forgot_id)
public class ForgotUserIdActivity extends RoboActivity {
	
	private static final String TAG = ForgotUserIdActivity.class.getSimpleName();
	
//BUTTONS
	
	@InjectView(R.id.forgot_id_submit_button)
	private Button submitButton;
	
//TEXT LABELS
	@InjectView(R.id.forgot_id_submission_error_label)
	private TextView mainErrLabel;
	
	@InjectView(R.id.forgot_id_id_error_label)
	private TextView idErrLabel;
	
	@InjectView(R.id.forgot_id_pass_error_label)
	private TextView passErrLabel;
	
	@InjectView(R.id.account_info_cancel_label)
	private TextView cancelLabel;
	
//INPUT FIELDS
	
	@InjectView(R.id.forgot_id_id_field)
	private EditText cardNumField;
	
	@InjectView(R.id.forgot_id_password_field)
	private EditText passField;
	
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		// FIXME Gives null pointer exception.
		TrackingHelper.trackPageView(AnalyticsPage.FORGOT_UID);
		
		setOnClickActions();
		setupTextChangedListeners();
	}
	
	private void setupTextChangedListeners(){
		
		setupPasswordTextChangedListeners();
		setupCardNumTextChangedListeners();
	}
	
	private void setupCardNumTextChangedListeners() {
		cardNumField.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			InputValidator validator = new InputValidator();
			
				@Override
				public void onFocusChange(final View v, final boolean hasFocus) {
					//This is then a user id that must be validated.
					final String acctNbr = cardNumField.getText().toString();
					if(!hasFocus && !validator.isCardAccountNumberValid(acctNbr)){
						showLabel( idErrLabel );
					}
				}
				
		});
		
		cardNumField.addTextChangedListener(new TextWatcher(){
			InputValidator validator = new InputValidator();
			@Override
			public void afterTextChanged(final Editable s) {
				if(validator.isCardAccountNumberValid(s.toString())){
					hideLabel( idErrLabel );
				}
			}			

			@Override
			public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {/*Intentionally empty*/}

			@Override
			public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {/*Intentionally empty*/}
			
		});
	}
	
		
	private void setupPasswordTextChangedListeners(){
		
		passField.setOnFocusChangeListener(new OnFocusChangeListener() {
			
				@Override
				public void onFocusChange(final View v, final boolean hasFocus) {
					//This is then a user id that must be validated.
					final String pass = passField.getText().toString();
					if(!hasFocus && pass.length() == 0){
						showLabel( passErrLabel );
					}
				}
				
		});
		
		passField.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(final Editable s) {
				if(s.toString().length() > 0){
					hideLabel( passErrLabel );
				}
			}			

			@Override
			public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) 
			{/*Intentionally empty*/}

			@Override
			public void onTextChanged(final CharSequence s, final int start, final int before, final int count) 
			{/*Intentionally empty*/}
			
		});
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
	
	@Override
	public void onBackPressed() {
		goBack();
	}
	
	public void goBack() {
		final Intent forgotCredentials = new Intent(this, ForgotTypeSelectionActivity.class);
		startActivity(forgotCredentials);
		finish();
	}
	
	private void hideAllErrorLabels(){
		hideLabel(idErrLabel);
		hideLabel(passErrLabel);
		hideLabel(mainErrLabel);
	}
	
	private void hideLabel(final View v) {
		v.setVisibility(View.GONE);
	}
	
	private void showLabel(final View v) {
		v.setVisibility(View.VISIBLE);
	}
	
	public void checkInputsAndSubmit(){
		final InputValidator validator = new InputValidator();
		
		if(!validator.isCardAccountNumberValid(cardNumField.getText().toString()))
			showLabel(idErrLabel);
		else
			hideLabel(idErrLabel);

		if(passField.getText().toString().isEmpty())
			showLabel(passErrLabel);
		else
			hideLabel(passErrLabel);

		if(validator.wasAccountNumberValid && !passField.getText().toString().isEmpty())
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
						displayOnMainErrorLabel(getString(R.string.login_error));
						return true;
						
					case HttpURLConnection.HTTP_UNAVAILABLE:
						displayOnMainErrorLabel(getString(R.string.unkown_error_text));
						return true;
						
//					case HttpURLConnection.HTTP_UNAVAILABLE:
//						//FIXME add service unavailable to screen types/error screens.
//						sendToErrorPage(ScreenType.SERVICE_UNAVAILABLE);
//						return true;
				}
				
				return false;
			}
			
			private void displayOnMainErrorLabel(final String text){
				mainErrLabel.setText(text);
				showLabel(mainErrLabel);
			}

			@Override
			public boolean handleMessageErrorResponse(final JsonMessageErrorResponse messageErrorResponse) {
				
				progress.dismiss();
				clearInputs();
				idErrLabel.setText(messageErrorResponse.getMessage());
				
				switch (messageErrorResponse.getMessageStatusCode()){
					case BAD_ACCOUNT_STATUS:
					case AUTH_BAD_ACCOUNT_STATUS:
						sendToErrorPage(ScreenType.BAD_ACCOUNT_STATUS);
						return true;
						
					case STRONG_AUTH_NOT_ENROLLED:
						sendToErrorPage(ScreenType.STRONG_AUTH_NOT_ENROLLED);
						return true;
						
					case MAX_LOGIN_ATTEMPTS:
						sendToErrorPage(ScreenType.LOCKED_OUT_USER);
						return true;

				}

				
				return false;
			}
		};

		new ForgotUserIdCall(this, callback, cardNumField.getText().toString(), passField.getText().toString()).submit();
	}
	
	private void sendToErrorPage(final ScreenType screenType) {
		final Intent maintenancePageIntent = new Intent(this, LockOutUserActivity.class);
		screenType.addExtraToIntent(maintenancePageIntent);
		startActivity(maintenancePageIntent);
		finish();
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
				new Intent(ForgotUserIdActivity.this, NavigationRootActivity.class);
		confirmationScreenIntent.putExtra("ScreenType", "forgotId");
		confirmationScreenIntent.putExtra(IntentExtraKey.ACCOUNT_LAST4, last4);
		confirmationScreenIntent.putExtra(IntentExtraKey.EMAIL, email);
		confirmationScreenIntent.putExtra(IntentExtraKey.UID, uid);
		TrackingHelper.trackPageView(AnalyticsPage.FOROGT_UID_CONFIRMATION);
		startActivity(confirmationScreenIntent);
	}
	
	public void clearInputs() {
		hideAllErrorLabels();
		passField.setText("");
		cardNumField.setText("");
	}
	
}
