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
import roboguice.inject.InjectResource;
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
	
//IMAGES
	
	@InjectView(R.id.card_check_mark)
	private ImageView cardCheckMark;
	
	@InjectView(R.id.bank_check_mark)
	private ImageView bankCheckMark;
	
//RESOURCES
	
	@InjectResource(R.string.hide)
	private String HIDE;
	
	@InjectResource(R.string.show)
	private String SHOW;
	
//INSTANCE VARS
	
	private Activity activity;
	
	private Resources res;
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		TrackingHelper.trackPageView(AnalyticsPage.CARD_LOGIN);
		res = getResources();

		setupButtons();
	}
	
	/**
	 * setupButtons()
	 * Attach onClickListeners to buttons.
	 * These buttons will execute the specified functionality in onClick
	 * when they are clicked...
	 */
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
	
	/**
	 * When this activity is stopped, usually when a user navigates away from this screen,
	 * the input fields will be cleared.
	 */
	@Override
	public void onStop() {
		super.onStop();
		clearInputs();
	}
		
	/**
	 * clearInputs()
	 * Removes any text in the login input fields.
	 */
	private void clearInputs() {
		idField.setText(emptyString);
		passField.setText(emptyString);
	}
	
	/**
	 * logIn()
	 * If the user has entered some information in both the user id field and the password field
	 * we will submit this info to the server and attempt to log the user in.
	 * 
	 * If not - we don't even try to log in.
	 */
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
	
	/**
	 * runAuthWithUsernameAndPassword(final String username, final String password)
	 * This method submits the users information to the server for verification.
	 * 
	 * The AsyncCallback handles the success and failure of the call and is responsible for handling and 
	 * presenting error messages to the user.
	 */
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
	
	/**
	 * sendToErrorPage(final ScreenType screenType)
	 * This method, on a critical login error, will send the user to a screen that will prevent them
	 * from further action. This is used for various kinda of 'locked out' users.
	 */
	private void sendToErrorPage(final ScreenType screenType) {
		final Intent maintenancePageIntent = new Intent(LoginActivity.this, LockOutUserActivity.class);
		screenType.addExtraToIntent(maintenancePageIntent);
		startActivity(maintenancePageIntent);
	}
	
	/**
	 * showOkAlertDialog(final String title, final String message)
	 * This method shows a pop-up dialog that the user must dismiss.
	 * It is usually shown when an error is being directly passed from the server response
	 * to the user.
	 */
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
	
	
	/**
	 * toggleCheckBox(final View v)
	 * This method handles the state of the check box on the login screen.
	 * 
	 * It changes its image and the state of the saveUserId value.
	 */
	boolean saveUserId = false;
	
	public void toggleCheckBox(final View v) {
		ImageView toggleImage = (ImageView)findViewById(R.id.remember_user_id_button);
		
			if(saveUserId){
				toggleImage.setBackgroundDrawable(res.getDrawable(R.drawable.gray_gradient_square));
				toggleImage.setImageDrawable(res.getDrawable(R.drawable.transparent_square));
				saveUserId = false;
			}
			else{
				toggleImage.setBackgroundDrawable(res.getDrawable(R.drawable.black_gradient_square));
				toggleImage.setImageDrawable(res.getDrawable(R.drawable.white_check_mark));
				saveUserId = true;
			}	
	}
	
	/**
	 * togglePasswordVisibility(final View v)
	 * This method handles showing and hiding of a users password.
	 * It will show a user's password in plain text if the user taps the Show text label
	 * on the home screen. And hide it if it says 'Hide'
	 * 
	 */
	public void togglePasswordVisibility(final View v) {
		String buttonText = hideButton.getText().toString();
		if(HIDE.equals(buttonText)) {
			hideButton.setText(SHOW);
			passField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		}
		else {
			hideButton.setText(HIDE);
			passField.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		}

	}
	
	/**
	 * toggleBankCardLogin(final View v)
	 * This method handles the login choices for loging in as a bank user or a card user.
	 * 
	 * It merely changes the visible position of a check mark and the color of the labels next
	 * to it.
	 */
	public void toggleBankCardLogin(final View v) {
		if(((TextView)v).getText().equals("Card")){
			goToCardLabel.setTextColor(getResources().getColor(R.color.black));
			cardCheckMark.setVisibility(View.VISIBLE);
			
			bankCheckMark.setVisibility(View.INVISIBLE);
			goToBankLabel.setTextColor(getResources().getColor(R.color.blue_link));
		}
		else{
			
			goToCardLabel.setTextColor(getResources().getColor(R.color.blue_link));
			cardCheckMark.setVisibility(View.INVISIBLE);
			
			bankCheckMark.setVisibility(View.VISIBLE);
			goToBankLabel.setTextColor(getResources().getColor(R.color.black));		
		}
	
	}
	
	/**
	 * registerNewUser(final View v)
	 * This method launches the registration screen when a user taps the register now
	 * button in the bottom bar. This method is called from the XML layout file onClick.
	 */
	public void registerNewUser(final View v) {
		final Intent accountInformationActivity = new Intent(this, RegistrationAccountInformationActivity.class);
		this.startActivity(accountInformationActivity);
	}
	
	
	/**
	 * forgotIdAndOrPass()
	 * This method is the same as registerNewUser except that it launches the forgot nav screen
	 * and is instead called from Java.
	 */
	private void forgotIdAndOrPass(){
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
