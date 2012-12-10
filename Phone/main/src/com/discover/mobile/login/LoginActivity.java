package com.discover.mobile.login;

import static com.discover.mobile.common.StandardErrorCodes.AUTH_BAD_ACCOUNT_STATUS;
import static com.discover.mobile.common.StandardErrorCodes.EXCEEDED_LOGIN_ATTEMPTS;
import static com.discover.mobile.common.StandardErrorCodes.MAINTENANCE_MODE_1;
import static com.discover.mobile.common.StandardErrorCodes.MAINTENANCE_MODE_2;
import static com.discover.mobile.common.StandardErrorCodes.STRONG_AUTH_NOT_ENROLLED;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.LOCKED_OUT_ACCOUNT;

import java.net.HttpURLConnection;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.CurrentSessionDetails;
import com.discover.mobile.common.ScreenType;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.auth.AccountDetails;
import com.discover.mobile.common.auth.AuthenticateCall;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.GenericAsyncCallback;
import com.discover.mobile.common.callback.GenericCallbackListener.ErrorResponseHandler;
import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.discover.mobile.common.net.json.JsonMessageErrorResponse;
import com.discover.mobile.common.push.registration.GetPushRegistrationStatus;
import com.discover.mobile.common.push.registration.PushRegistrationStatusDetail;
import com.discover.mobile.login.register.ForgotTypeSelectionActivity;
import com.discover.mobile.login.register.RegistrationAccountInformationActivity;
import com.discover.mobile.navigation.NavigationRootActivity;
import com.discover.mobile.push.PushRegistrationStatusErrorHandler;
import com.discover.mobile.push.PushRegistrationStatusSuccessListener;
import com.google.common.base.Strings;
/**
 * LoginActivity - This is the login screen for the application. It makes two service calls - one to attempt to log the
 * user into the system and the other to check the Xtify push notification status.
 * 
 * @author scottseward
 *
 */
@ContentView(R.layout.login_start)
public class LoginActivity extends RoboActivity {
	private final static String emptyString = ""; //$NON-NLS-1$
	

//INPUT FIELDS
	
	@InjectView(R.id.username_field)
	private EditText idField;
	
	@InjectView(R.id.password_field)
	private EditText passField;

//BUTTONS
	
	@InjectView(R.id.login_button)
	private Button loginButton;
	
	@InjectView(R.id.remember_user_id_button)
	private ImageView saveUserButton;
	
//TEXT LABELS
	
	@InjectView(R.id.register_text)
	private TextView registerText;

	@InjectView(R.id.error_text_view)
	private TextView errorTextView;
	
	@InjectView(R.id.forgot_uid_or_pass_text)
	private TextView forgotUserIdOrPassText;
	
	@InjectView(R.id.toggle_password_visibility_label)
	private TextView hideButton;
	
	@InjectView(R.id.go_to_bank_label)
	private TextView goToBankLabel;
	
	@InjectView(R.id.go_to_card_label)
	private TextView goToCardLabel;
	
	private Activity activity;
	
	private Resources res;
	
	private String HIDE;
	private String SHOW;
		
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		TrackingHelper.trackPageView(AnalyticsPage.CARD_LOGIN);
		res = getResources();
		
		HIDE = getString(R.string.hide);
		SHOW = getString(R.string.show);
		setupButtons();
	}
	
	private void setupButtons() {
		loginButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(final View v){
				errorTextView.setText(emptyString); 
				logIn();
			}
		});
		
		registerText.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(final View v){
				errorTextView.setText(emptyString); 
				registerNewUser(v);
			}
		});
		
		forgotUserIdOrPassText.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(final View v){
				errorTextView.setText(emptyString); 
				forgotIdAndOrPass();
			}
		});
	}
	
	@Override
	public void onStop() {
		super.onStop();
		clearInputs();
	}
		
	private void clearInputs() {
		idField.setText(emptyString);
//		passField.setText(emptyString);
	}
	
	private void logIn() {
		//If the user id, or password field are effectively blank, do not allow a service call to be made
		//display the error message for id/pass not matching records.
		if(Strings.isNullOrEmpty(idField.getText().toString()) ||
			Strings.isNullOrEmpty(passField.getText().toString())) {
			errorTextView.setText(getString(R.string.login_error));
			
		} else {
			runAuthWithUsernameAndPassword(idField.getText().toString(), passField.getText().toString());
		}
	}
	
	private void runAuthWithUsernameAndPassword(final String username, final String password) {
		final AsyncCallback<AccountDetails> callback = GenericAsyncCallback.<AccountDetails>builder(this)
					.showProgressDialog("Discover", "Loading...", true)
					.clearTextViewsOnComplete(errorTextView, passField, idField)
					.withSuccessListener(new SuccessListener<AccountDetails>() {
						
						@Override
						public CallbackPriority getCallbackPriority() {
							return CallbackPriority.MIDDLE;
						}
						
						@Override
						public void success(final AccountDetails value) {
							CurrentSessionDetails.getCurrentSessionDetails().setAccountDetails(value);
							getXitifyRegistrationStatus();
						}
					})
					
					// FIXME DO NOT COPY THIS CODE
					.withErrorResponseHandler(new ErrorResponseHandler() {
						@Override
						public CallbackPriority getCallbackPriority() {
							return CallbackPriority.MIDDLE;
						}
						
						@Override
						public boolean handleFailure(final ErrorResponse<?> errorResponse) {
							if(errorResponse instanceof JsonMessageErrorResponse)
								return handleMessageErrorResponse((JsonMessageErrorResponse)errorResponse);
							
							switch(errorResponse.getHttpStatusCode()) {
								case HttpURLConnection.HTTP_UNAUTHORIZED:
									errorTextView.setText(getString(R.string.login_error));
									return true;
								
								// FIXME other cases
							}
							
							return false;
						}
						
						public boolean handleMessageErrorResponse(final JsonMessageErrorResponse messageErrorResponse) {
							TrackingHelper.trackPageView(AnalyticsPage.LOGIN_ERROR);
							
							if(messageErrorResponse.getHttpStatusCode() != HttpURLConnection.HTTP_FORBIDDEN)
								return false;
							
							// FIXME convert other error codes to standard constants
							switch(messageErrorResponse.getMessageStatusCode()) {
								case MAINTENANCE_MODE_1:
								case MAINTENANCE_MODE_2: 
									sendToErrorPage(ScreenType.MAINTENANCE);
									return true;
								
								case STRONG_AUTH_NOT_ENROLLED:
									sendToErrorPage(ScreenType.STRONG_AUTH_NOT_ENROLLED);
									return true;
									
								case AUTH_BAD_ACCOUNT_STATUS:
									sendToErrorPage(ScreenType.BAD_ACCOUNT_STATUS);
									return true;
									
								case EXCEEDED_LOGIN_ATTEMPTS:
								case LOCKED_OUT_ACCOUNT:
									sendToErrorPage(ScreenType.LOCKED_OUT_USER);
									return true;
									
								default:
									errorTextView.setText(messageErrorResponse.getMessage());
									return true;
							}
						}
					})
					
					.build();
		
		new AuthenticateCall(this, callback, username, password).submit();
	}
	
	private void sendToErrorPage(final ScreenType screenType) {
		final Intent maintenancePageIntent = new Intent(LoginActivity.this, LockOutUserActivity.class);
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
	
	boolean isChecked = false;
	
	public void toggleCheckBox(final View v) {
		ImageView toggleImage = (ImageView)findViewById(R.id.remember_user_id_button);
		
			if(isChecked){
				toggleImage.setBackgroundDrawable(res.getDrawable(R.drawable.gray_gradient_square));
				toggleImage.setImageDrawable(res.getDrawable(R.drawable.transparent_square));
				isChecked = false;
			}
			else{
				toggleImage.setBackgroundDrawable(res.getDrawable(R.drawable.black_gradient_square));
				toggleImage.setImageDrawable(res.getDrawable(R.drawable.white_check_mark));
				isChecked = true;
			}	
	}
	
	public void togglePasswordVisibility(final View v) {
		String buttonText = hideButton.getText().toString();
		
		if(HIDE.equals(buttonText)) {
			hideButton.setText(SHOW);
			passField.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
		}
		else {
			hideButton.setText(HIDE);
			passField.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		}

	}
	
	public void toggleBankCardLogin(final View v) {
//		if(((TextView)v).equals(goToCardLabel)){
//			
//		}
//		else{
//			
//			goToBankLabel.setTextColor(getResources().getColor(R.color.blue_link));
//		}
//		goToCardLabel;
//		goToBankLabel;
	}
	
	public void registerNewUser(final View v) {
		final Intent accountInformationActivity = new Intent(this, RegistrationAccountInformationActivity.class);
		this.startActivity(accountInformationActivity);
	}
	
	public void forgotIdAndOrPass(){
		final Intent forgotIdAndOrPassActivity = new Intent(this, ForgotTypeSelectionActivity.class);
		this.startActivity(forgotIdAndOrPassActivity);
	}
	
	/**
	 * Do a GET request to the server to check to see if this vendor id is registered to this user.
	 * @author jthornton
	 */
	protected void getXitifyRegistrationStatus(){
		final AsyncCallback<PushRegistrationStatusDetail> callback = 
				GenericAsyncCallback.<PushRegistrationStatusDetail>builder(this)
				.showProgressDialog(getResources().getString(R.string.push_progress_get_title), 
									getResources().getString(R.string.push_progress_registration_loading), 
									true)
				.withSuccessListener(new PushRegistrationStatusSuccessListener())
				.withErrorResponseHandler(new PushRegistrationStatusErrorHandler(this))
				.launchIntentOnSuccess(NavigationRootActivity.class)
				.build();
	
		new GetPushRegistrationStatus(this, callback).submit();
	}
}
