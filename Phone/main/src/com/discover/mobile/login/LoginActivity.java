package com.discover.mobile.login;

import static com.discover.mobile.common.CommonMethods.setViewGone;
import static com.discover.mobile.common.CommonMethods.setViewInvisible;
import static com.discover.mobile.common.CommonMethods.setViewVisible;
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
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.auth.AccountDetails;
import com.discover.mobile.common.auth.AuthenticateCall;
import com.discover.mobile.common.auth.PreAuthCheckCall;
import com.discover.mobile.common.auth.PreAuthCheckCall.PreAuthResult;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.GenericAsyncCallback;
import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.common.push.registration.GetPushRegistrationStatus;
import com.discover.mobile.common.push.registration.PushRegistrationStatusDetail;
import com.discover.mobile.login.register.ForgotTypeSelectionActivity;
import com.discover.mobile.login.register.RegistrationAccountInformationActivity;
import com.discover.mobile.navigation.NavigationRootActivity;
import com.discover.mobile.push.PushRegistrationStatusErrorHandler;
import com.discover.mobile.push.PushRegistrationStatusSuccessListener;
import com.google.common.base.Strings;
/**
 * LoginActivity - This is the login screen for the application. It makes three service calls - 
 * The first call is 
 * 
 * @author scottseward
 *
 */
@ContentView(R.layout.login_start)
public class LoginActivity extends RoboActivity {
	private final static String emptyString = ""; //$NON-NLS-1$
	
	private final static String TAG = LoginActivity.class.getSimpleName();
	
	private final static String PASS_KEY          = "pass";
	private final static String ID_KEY            = "id";
	private final static String SAVE_ID_KEY       = "save";
	private final static String LOGIN_TYPE_KEY    = "type";
	private final static String PRE_AUTH_KEY      = "pauth";
	private final static String PW_INPUT_TYPE_KEY = "secrets";
	private final static String HIDE_LABEL_KEY    = "hide";
	
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
	
	@InjectView(R.id.register_now_button)
	private Button registerButton;
	
//TEXT LABELS

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
	
	private boolean preAuthHasRun = false;
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		TrackingHelper.trackPageView(AnalyticsPage.CARD_LOGIN);
		res = getResources();

		restoreState(savedInstanceState);
		setupButtons();

		if(!preAuthHasRun){
			startPreAuthCheck();
		}
	}
	
	@Override
	public void onSaveInstanceState(final Bundle outState) {
		outState.putString(ID_KEY, idField.getText().toString());
		outState.putString(PASS_KEY, passField.getText().toString());
		outState.putBoolean(SAVE_ID_KEY, saveUserId);
		outState.putBoolean(PRE_AUTH_KEY, preAuthHasRun);
		outState.putInt(PW_INPUT_TYPE_KEY, passField.getInputType());
		outState.putString(HIDE_LABEL_KEY, hideButton.getText().toString());
		outState.putInt(LOGIN_TYPE_KEY, cardCheckMark.getVisibility());
		
		super.onSaveInstanceState(outState);
	}
	
	/**
	 * Restore the state of the screen on oreintation change.
	 * @param savedInstanceState
	 */
	public void restoreState(final Bundle savedInstanceState) {
		if(savedInstanceState == null) {return;}
		idField.setText(savedInstanceState.getString(ID_KEY));
		passField.setText(savedInstanceState.getString(PASS_KEY));
		preAuthHasRun = savedInstanceState.getBoolean(PRE_AUTH_KEY);
		
		passField.setInputType(savedInstanceState.getInt(PW_INPUT_TYPE_KEY));
		
		if(View.VISIBLE == savedInstanceState.getInt(LOGIN_TYPE_KEY))
			toggleBankCardLogin(goToCardLabel);
		else
			toggleBankCardLogin(goToBankLabel);
		hideButton.setText(savedInstanceState.getString(HIDE_LABEL_KEY));
		saveUserId = !savedInstanceState.getBoolean(SAVE_ID_KEY);
		toggleCheckBox(null);
		
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
				setViewGone(errorTextView); 
				logIn();
			}
		});
		
		registerButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(final View v){
				setViewGone(errorTextView); 
				registerNewUser();
			}
		});
		
		forgotUserIdOrPassText.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(final View v){
				setViewGone(errorTextView); 
				forgotIdAndOrPass();
			}
		});
		
	}
		
	/**
	 * clearInputs()
	 * Removes any text in the login input fields.
	 */
	private void clearInputs() {
		idField.setText(emptyString);
		passField.setText(emptyString);
		idField.setError(null);
		passField.setError(null);
	}

	
	/**
	 * logIn()
	 * If the user id, or password field are effectively blank, do not allow a service call to be made
	 * display the error message for id/pass not matching records.
	 * If the fields have data - submit it to the server for validation.
	 */
	private void logIn() {
		if(!showErrorIfAnyFieldsAreEmpty()) {
			clearInputs();
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
					.withErrorResponseHandler(new LoginErrorResponseHandler(activity, errorTextView, idField, passField))
					.build();
		
		new AuthenticateCall(this, callback, username, password).submit();
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
		
		if(v.equals(goToCardLabel)){
			goToCardLabel.setTextColor(getResources().getColor(R.color.black));
			setViewVisible(cardCheckMark);
			
			setViewInvisible(bankCheckMark);
			goToBankLabel.setTextColor(getResources().getColor(R.color.blue_link));
		}
		else{
			
			goToCardLabel.setTextColor(getResources().getColor(R.color.blue_link));
			setViewInvisible(cardCheckMark);
			setViewVisible(bankCheckMark);
			goToBankLabel.setTextColor(getResources().getColor(R.color.black));		
		}
	
	}
	
	/**
	 * registerNewUser()
	 * This method launches the registration screen when a user taps the register now
	 * button in the bottom bar.
	 */
	public void registerNewUser() {
		clearInputs();
		final Intent accountInformationActivity = new Intent(this, RegistrationAccountInformationActivity.class);
		this.startActivity(accountInformationActivity);
	}
	
	
	/**
	 * forgotIdAndOrPass()
	 * This method is the same as registerNewUser except that it launches the forgot nav screen
	 * and is instead called from Java.
	 */
	private void forgotIdAndOrPass(){
		clearInputs();
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
	
	/**
	 * showErrorIfAnyFieldsAreEmpty()
	 * Sets error tags for input fields if a field is empty.
	 * 
	 * @return boolean value to show if any errors should be shown.
	 */
	private boolean showErrorIfAnyFieldsAreEmpty() {
		boolean wasIdEmpty, wasPassEmpty;
		wasIdEmpty = Strings.isNullOrEmpty(idField.getText().toString());
		wasPassEmpty = Strings.isNullOrEmpty(passField.getText().toString());
		
		if(wasIdEmpty || wasPassEmpty) {	
			if(wasIdEmpty) {
				idField.setError("Your ID Cannot be Empty!");
			}
			if(wasPassEmpty) {
				passField.setError("Your Password Cannot be Empty!");
			} 
			return true;
		}
		//All fields were populated.
		return false;
	}
	
	/**
	 * Run the pre-auth call.
	 * Check with the server if the version of the application we are running is OK.
	 * Also checks to see if the server is available and will allow users to login.
	 */
	public void startPreAuthCheck() {
		final SuccessListener<PreAuthResult> optionalUpdateListener = new PreAuthSuccessResponseHandler(activity);
		
		final AsyncCallback<PreAuthResult> callback = GenericAsyncCallback.<PreAuthResult>builder(this)
				.showProgressDialog("Discover", "Loading...", true)
				.withSuccessListener(optionalUpdateListener)
				.withErrorResponseHandler(new PreAuthErrorResponseHandler(activity))
				.build();
		
		new PreAuthCheckCall(this, callback).submit();
		preAuthHasRun = true;

	}
		
		
		
		
}
