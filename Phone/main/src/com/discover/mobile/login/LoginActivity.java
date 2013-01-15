package com.discover.mobile.login;

import static com.discover.mobile.common.CommonMethods.setViewGone;
import static com.discover.mobile.common.CommonMethods.setViewInvisible;
import static com.discover.mobile.common.CommonMethods.setViewVisible;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.discover.mobile.BaseActivity;
import com.discover.mobile.DefaultExceptionFailureHandler;
import com.discover.mobile.R;
import com.discover.mobile.common.AccountType;
import com.discover.mobile.common.CurrentSessionDetails;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.StandardErrorCodes;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.auth.AccountDetails;
import com.discover.mobile.common.auth.AuthenticateCall;
import com.discover.mobile.common.auth.InputValidator;
import com.discover.mobile.common.auth.PreAuthCheckCall;
import com.discover.mobile.common.auth.PreAuthCheckCall.PreAuthResult;
import com.discover.mobile.common.auth.registration.RegistrationErrorCodes;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.GenericAsyncCallback;
import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.common.callback.LockScreenCompletionListener;
import com.discover.mobile.common.push.PushNotificationService;
import com.discover.mobile.common.push.registration.GetPushRegistrationStatus;
import com.discover.mobile.common.push.registration.PushRegistrationStatusDetail;
import com.discover.mobile.login.register.ForgotTypeSelectionActivity;
import com.discover.mobile.login.register.RegistrationAccountInformationActivity;
import com.discover.mobile.navigation.NavigationRootActivity;
import com.discover.mobile.push.register.PushRegistrationStatusErrorHandler;
import com.discover.mobile.push.register.PushRegistrationStatusSuccessListener;
import com.google.common.base.Strings;
import com.google.inject.Inject;

/**
 * LoginActivity - This is the login screen for the application. It makes three
 * service calls - The first call is pre auth Second is starting xtify services
 * for push notifications And the third is the login call when a user tries to
 * login.
 * 
 * @author scottseward, ekaram
 * 
 */
@ContentView(R.layout.login_start)
public class LoginActivity extends BaseActivity  {
	/*TAG used to print logs for the LoginActivity into logcat*/
	private final static String TAG = LoginActivity.class.getSimpleName();
	
	private final static String emptyString = ""; //$NON-NLS-1$

	/**
	 * These are string values used when passing extras to the saved instance
	 * state bundle for restoring the state of the screen upon orientation
	 * changes.
	 */
	private final static String PASS_KEY = "pass";
	private final static String ID_KEY = "id";
	private final static String SAVE_ID_KEY = "save";
	private final static String LOGIN_TYPE_KEY = "type";
	private final static String PRE_AUTH_KEY = "pauth";
	private final static String PW_INPUT_TYPE_KEY = "secrets";
	private final static String HIDE_LABEL_KEY = "hide";
	private final static String ERROR_MESSAGE_KEY = "errorText";
	private final static String ERROR_MESSAGE_VISIBILITY = "errorVisibility";
	
	/**
	 * Roboguise injections of android interface element references.
	 */
	// INPUT FIELDS

	@InjectView(R.id.username_field)
	private EditText idField;

	@InjectView(R.id.password_field)
	private EditText passField;

	// BUTTONS

	@InjectView(R.id.login_button)
	private Button loginButton;

	@InjectView(R.id.remember_user_id_button)
	private ImageView saveUserButton;

	@InjectView(R.id.register_now_or_atm_button)
	private Button registerOrAtmButton;
	
	@InjectView(R.id.privacy_and_security_button)
	private Button privacySecOrTermButton;

	@InjectView(R.id.customer_service_button)
	private Button customerServiceButton;
	
	// TEXT LABELS

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
	
	@InjectView(R.id.remember_user_id_button)
	private ImageView toggleImage;
	
	@InjectView(R.id.splash_progress)
	private ProgressBar splashProgress;

	// RESOURCES

	@InjectResource(R.string.hide)
	private String HIDE;

	@InjectResource(R.string.show)
	private String SHOW;

	/**
	 * Non roboguise attributes
	 */
	
	private Resources res;

	/*Used to specify whether the pre-authenciation call has been made for the application. 
	 * Should only be done at application start-up.
	 */
	private boolean preAuthHasRun = false;


	private boolean saveUserId = false;
	
	/**
	 * Used to remember the lastLoginAccount at startup of the application, in case the user toggles to a different account
	 * and does not login. This variable will be used to revert the application back to the original last logged in account.
	 */
	private AccountType lastLoginAcct = AccountType.CARD_ACCOUNT;
	
	private final static int LOGOUT_TEXT_COLOR = R.color.body_copy;
	
	@Inject
	private PushNotificationService pushNotificationService;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TrackingHelper.startActivity(this);
		TrackingHelper.trackPageView(AnalyticsPage.STARTING);
		TrackingHelper.trackPageView(AnalyticsPage.CARD_LOGIN);
		res = getResources();
		restoreState(savedInstanceState);
		setupButtons();

		//Check to see if pre-auth request is required. Should only 
		//be done at application start-up
		if (!preAuthHasRun) {
			startPreAuthCheck();
		}
	}

	/**
	 * Check to see if the user just logged out, if the user just logged out show the message.
	 */
	private void maybeShowUserLoggedOut(){
		final Intent intent = this.getIntent();
		final Bundle extras = intent.getExtras();

		if(extras != null){
			if(extras.getBoolean(IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE, false)){
				errorTextView.setText(getString(R.string.logout_sucess));
				errorTextView.setVisibility(View.VISIBLE);
				errorTextView.setTextColor(getResources().getColor(LOGOUT_TEXT_COLOR));
				this.getIntent().putExtra(IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE, false);
			}
		}
	}
	
	/**
	 * Resume the activity
	 */
	@Override
	public void onResume(){
		super.onResume();
		maybeShowUserLoggedOut();
		
		int lastError = getLastError();
		
		//Do not load saved credentials if there was a previous login attempt 
		//which failed because of a lock out
		if( StandardErrorCodes.EXCEEDED_LOGIN_ATTEMPTS != lastError &&
			RegistrationErrorCodes.LOCKED_OUT_ACCOUNT != lastError) {
			loadSavedCredentials();
		} else {
			//Clear Text Fields for username and password
			clearInputs();
			
			//Uncheck remember user id checkbox without remembering change
			setCheckMark(false, false);
		}
		
		//Default to the last path user chose for login Card or Bank
		this.setApplicationAccount();		
		
		//Show splash screen while completing pre-auth, if pre-auth has not been done
		showSplashScreen(!preAuthHasRun);
	}
	

	/**
	 * Ran at the start of an activity when an activity is brought to the front.
	 * This also will trigger the Xtify SDK to start.
	 * Check to see if the user just logged out, if the user just logged out show the message.
	 */
	@Override
	public void onStart() {
		super.onStart();
		pushNotificationService.start(this);
	}

	/**
	 * Place all necessary information to be restored in a bundle.
	 * This info is used when the screen orientation changes.
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState) {
		outState.putString(ID_KEY, idField.getText().toString());
		outState.putString(PASS_KEY, passField.getText().toString());
		outState.putBoolean(SAVE_ID_KEY, saveUserId);
		outState.putBoolean(PRE_AUTH_KEY, preAuthHasRun);
		outState.putInt(PW_INPUT_TYPE_KEY, passField.getInputType());
		outState.putString(HIDE_LABEL_KEY, hideButton.getText().toString());
		outState.putInt(LOGIN_TYPE_KEY, cardCheckMark.getVisibility());
		outState.putString(ERROR_MESSAGE_KEY, errorTextView.getText().toString());
		outState.putInt(ERROR_MESSAGE_VISIBILITY, errorTextView.getVisibility());
		super.onSaveInstanceState(outState);
	}

	/**
	 * Restore the state of the screen on orientation change.
	 * 
	 * @param savedInstanceState A bundle of state information to be restored to the screen.
	 */
	public void restoreState(final Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			idField.setText(savedInstanceState.getString(ID_KEY));
			passField.setText(savedInstanceState.getString(PASS_KEY));
			preAuthHasRun = savedInstanceState.getBoolean(PRE_AUTH_KEY);

			passField.setInputType(savedInstanceState.getInt(PW_INPUT_TYPE_KEY));
			hideButton.setText(savedInstanceState.getString(HIDE_LABEL_KEY));

			setLoginType(savedInstanceState.getInt(LOGIN_TYPE_KEY));
			setCheckMark(savedInstanceState.getBoolean(SAVE_ID_KEY), true);

			restoreErrorTextView(savedInstanceState);
			resetInputFieldColors();
		}

	
	}
	
	/**
	 * When restoring the state of the screen on rotation. Restore the error text at the top of the screen.
	 *
	 * @param savedInstanceState
	 */
	private void restoreErrorTextView(final Bundle savedInstanceState) {
		errorTextView.setText(savedInstanceState.getString(ERROR_MESSAGE_KEY));
		errorTextView.setVisibility(savedInstanceState.getInt(ERROR_MESSAGE_VISIBILITY));
		if(!errorIsVisible())
			errorTextView.setTextColor(getResources().getColor(LOGOUT_TEXT_COLOR));
		
	}
	
	/**
	 * Set the input fields to red if an error is being displayed.
	 */
	private void resetInputFieldColors() {		
		if(errorIsVisible()) {
			setInputFieldsDrawableToRed();
		}
	}
	
	/**
	 * Checks to see if an error message is displayed.
	 * 
	 * @return returns true if an error is being displayed in the errorTextView
	 */
	private boolean errorIsVisible() {
		boolean errorTextViewIsVisible = errorTextView.getVisibility() == View.VISIBLE;
		boolean errorTextIsNotLogoutMessage = 
				!getResources().getString(R.string.logout_sucess).equals(errorTextView.getText().toString());
				
		if( errorTextViewIsVisible & errorTextIsNotLogoutMessage )
			return true;
		else
			return false;
	}
	
	/**
	 * Load user credentials from shared preferences.
	 * Set user ID field to the saved value, if it was supposed to be saved.
	 */
	private void loadSavedCredentials() {
		boolean rememberIdCheckState = false;
	
		rememberIdCheckState = Globals.isRememberId();
		
		if(rememberIdCheckState){
			clearInputs();
			
			idField.setText(Globals.getCurrentUser());

			setCheckMark(rememberIdCheckState, true);
		} else {
			setCheckMark(rememberIdCheckState, false);
		}

	}

	/**
	 * setupButtons() Attach onClickListeners to buttons. These buttons will
	 * execute the specified functionality in onClick when they are clicked...
	 */
	private void setupButtons() {
	
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				setViewGone(errorTextView);
				
				//Clear the last error that occurred
				setLastError(0);
				
				login();
			}
		});

		registerOrAtmButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				
				String regOrAtmText = registerOrAtmButton.getText().toString();
				String regText = getResources().getString(R.string.register_now);
				
				//Check if registerOrAtm button is displaying text for Card or Bank
				if( regOrAtmText.equals(regText) ) {
					registerNewUser();
				} else {
					openAtmLocator();
				}
				
			}
		});
		
		privacySecOrTermButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				
				String curText = privacySecOrTermButton.getText().toString();
				String privSecText = getResources().getString(R.string.privacy_and_security);
				
				//Check if privacySecOrTermButton button is displaying text for Card or Bank
				if( curText.equals(privSecText) ) {
					openPrivacyAndSecurity();
				} else {
					openPrivacyAndTerms();
				}
				
			}
		});
		
		customerServiceButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				openCustomerService();
			}
		});

		forgotUserIdOrPassText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				setViewGone(errorTextView);
				forgotIdAndOrPass();
			}
		});

	}

	/**
	 * If the user id, or password field are effectively blank, do not
	 * allow a service call to be made display the error message for id/pass not
	 * matching records. If the fields have data - submit it to the server for
	 * validation.
	 */
	private void login() {
		setInputFieldsDrawablesToDefault();
		if (!showErrorIfAnyFieldsAreEmpty() && !showErrorWhenAttemptingToSaveAccountNumber()) {
			runAuthWithUsernameAndPassword(idField.getText().toString(),
					passField.getText().toString());
		}
	}
	
	/**
	 * Set the background color of the input fields back to their default values
	 */
	private void setInputFieldsDrawablesToDefault() {
		idField.setBackgroundResource(R.drawable.edit_text_default);
		passField.setBackgroundResource(R.drawable.edit_text_default);
	}
	
	/**
	 * Set the input fields to be highlighted in red.
	 */
	private void setInputFieldsDrawableToRed() {
		idField.setBackgroundResource(R.drawable.edit_text_red);
		passField.setBackgroundResource(R.drawable.edit_text_red);
	}
	
	/**
	 * If a user tries to save their login ID but provides an account number, we need to show an error
	 * clear the input fields and un-check the save-user-id box.
	 * 
	 * @return a boolean that represents if an error was displayed or not.
	 */
	private boolean showErrorWhenAttemptingToSaveAccountNumber() {
		String inputId = idField.getText().toString();
		
		if(saveUserId && InputValidator.isCardAccountNumberValid(inputId)) {
			errorTextView.setTextColor(getResources().getColor(R.color.red));
			errorTextView.setText(getString(R.string.cannot_save_account_number));
			errorTextView.setVisibility(View.VISIBLE);
			clearInputs();
			toggleCheckBox(idField, true);
			setInputFieldsDrawableToRed();
			return true;
		}
		else{
			return false;
		}
	}

	/**
	 * This method submits the users information to the Card or Bank server for
	 * verification depending on what is selected in login page.
	 * 
	 * The AsyncCallback handles the success and failure of the call and is
	 * responsible for handling and presenting error messages to the user.
	 * 
	 */
	private void runAuthWithUsernameAndPassword(final String username, final String password) {
		//Check if card account has been selected
		if( View.VISIBLE == cardCheckMark.getVisibility() ) {
			cardLogin(username, password) ;
		} else {
			bankLogin(username, password);
		}
	}
	
	/**
	 * This method submits the users information to the Card server for verification.
	 * 
	 * The AsyncCallback handles the success and failure of the call and is
	 * responsible for handling and presenting error messages to the user.
	 * 
	 */
	private void cardLogin(final String username, final String password) {
		final AsyncCallback<AccountDetails> callback = GenericAsyncCallback
				.<AccountDetails> builder(this)
				.showProgressDialog("Discover", "Loading...", true)
				.withSuccessListener(new SuccessListener<AccountDetails>() {

					@Override
					public CallbackPriority getCallbackPriority() {
						return CallbackPriority.MIDDLE;
					}

					@Override
					public void success(final AccountDetails value) {
						// Set logged in to be able to save user name in
						// persistent storage
						Globals.setLoggedIn(true);

						// Update current account based on user logged
						updateAccountInformation(AccountType.CARD_ACCOUNT);

						CurrentSessionDetails.getCurrentSessionDetails()
								.setAccountDetails(value);

						getXtifyRegistrationStatus();

					}
				})
				.withErrorResponseHandler(new LoginErrorResponseHandler(this))
				.build();

		new AuthenticateCall(this, callback, username, password).submit();
	}

	/**
 	 * This method submits the users information to the Bank server for verification.
	 * 
	 * The AsyncCallback handles the success and failure of the call and is
	 * responsible for handling and presenting error messages to the user.
	 * 
	 */
	private void bankLogin(final String username, final String password) {
		/*********TODO: REMOVE THIS BLOCK OF CODE AFTER COMPLETING BANK LOGIN*************/
		CharSequence text = "Bank Login Under Development";
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(this, text, duration);
		toast.show();
		
		//Set logged in to be able to save user name in persistent storage
		Globals.setLoggedIn(true);
		
		/*********TODO: REMOVE THIS BLOCK OF CODE AFTER COMPLETING BANK LOGIN*************/
	
		//TODO: Add Bank Login Logic here
		
		//Update current account based on user logged in and account type
		updateAccountInformation(AccountType.BANK_ACCOUNT);
	}

	/**
	 * toggleCheckBox(final View v) This method handles the state of the check
	 * box on the login screen.
	 * 
	 * It changes its image and the state of the saveUserId value.
	 * 
	 * @param v Reference to view which contains the remember user id check
	 * @param cache Specifies whether to remember the state change
	 */
	public void toggleCheckBox(final View v, final boolean cache) {
	
		if (saveUserId) {
			toggleImage.setBackgroundDrawable(res.getDrawable(R.drawable.gray_gradient_square));
			toggleImage.setImageDrawable(res.getDrawable(R.drawable.transparent_square));
			saveUserId = false;
		} else {
			toggleImage.setBackgroundDrawable(res.getDrawable(R.drawable.black_gradient_square));
			toggleImage.setImageDrawable(res.getDrawable(R.drawable.white_check_mark));
			saveUserId = true;
		}

		//Check whether to save change in persistent storage
		if(cache) {
			Globals.setRememberId(saveUserId);
		}
	}
	
	/**
	 * Calls toggleCheckBox(v, true)
	 * This method allows us to call toggleCheckBox from XML, we always want to save the state of the button
	 * so we always pass true as the second argument.
	 * 
	 * @param v the calling view.
	 */
	public void toggleCheckBoxFromXml(final View v) {
		toggleCheckBox(v, true);
	}

	/**
	 * togglePasswordVisibility(final View v) This method handles showing and
	 * hiding of a users password. It will show a user's password in plain text
	 * if the user taps the Show text label on the home screen. And hide it if
	 * it says 'Hide'
	 */
	public void togglePasswordVisibility(final View v) {
		final String buttonText = hideButton.getText().toString();
		//Retain the position of the selector.
		int selectionPosition = passField.getSelectionStart();
		if(HIDE.equals(buttonText)) {
			hideButton.setText(SHOW);
			passField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		} else {
			hideButton.setText(HIDE);
			passField.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
		}
		//Restore the position of the selector.
		passField.setSelection(selectionPosition);
		
	}

	/**
	 * Updates the view based on the application account selected by the user. Called by application at start-up.
	 */
	private void setApplicationAccount() {
		lastLoginAcct = Globals.getCurrentAccount();
		if (AccountType.BANK_ACCOUNT == lastLoginAcct) {
			toggleBankCardLogin(goToBankLabel);	
		} else {
			toggleBankCardLogin(goToCardLabel);	
		}
	}

	/**
	 * toggleBankCardLogin(final View v) This method handles the login choices
	 * for logging in as a bank user or a card user.
	 * 
	 * It changes the visible position of a check mark and the color of
	 * the labels next to it. In addition, it updates the text for the bottom
	 * row buttons.
	 */
	public void toggleBankCardLogin(final View v) { 
		if (v.equals(goToCardLabel)) {
			goToCardLabel.setTextColor(getResources().getColor(R.color.black));
			setViewVisible(cardCheckMark);

			setViewInvisible(bankCheckMark);
			goToBankLabel.setTextColor(getResources().getColor(R.color.blue_link));
			
			registerOrAtmButton.setText(R.string.register_now);
			privacySecOrTermButton.setText(R.string.privacy_and_security);
			setViewVisible(this.forgotUserIdOrPassText);
			
			//Load Card Account Preferences for refreshing UI only
			Globals.loadPreferences(this, AccountType.CARD_ACCOUNT);
		} else {
			goToCardLabel.setTextColor(getResources().getColor(
					R.color.blue_link));
			setViewInvisible(cardCheckMark);
			setViewVisible(bankCheckMark);
			goToBankLabel.setTextColor(getResources().getColor(R.color.black));
			
			registerOrAtmButton.setText(R.string.atm_locator);
			privacySecOrTermButton.setText(R.string.privacy_and_terms);
			setViewInvisible(this.forgotUserIdOrPassText);
			
			//Load Bank Account Preferences for refreshing UI only
			Globals.loadPreferences(this, AccountType.BANK_ACCOUNT);
		}
		
		//Refresh Screen based on Selected Account Preferences
		loadSavedCredentials();
		
		//Revert data back to original last logged in account.
		//Last logged in is only remembered if user logins successfully
		if( lastLoginAcct != Globals.getCurrentAccount() ) {
			Globals.loadPreferences(this, lastLoginAcct);
		}
	}

	/**
	 * clearInputs() Removes any text in the login input fields.
	 */
	private void clearInputs() {
		idField.setText(emptyString);
		passField.setText(emptyString);
		setInputFieldsDrawablesToDefault();
	}
	
	/**
	 * Sets the check mark on the login screen to the given boolean (checked/unchecked) state.
	 * 
	 * @param shouldBeChecked Sets the check mark to checked or unchecked for true or false respectively.
	 * @param cached Sets whether the state change should be remembered
	 */

	private void setCheckMark(boolean shouldBeChecked, boolean cached) {
		saveUserId = !shouldBeChecked;
		toggleCheckBox(toggleImage, cached);
	}
	
	
	/**
	 * Sets the login type of the login screen. This is for users who want to log in with their "Card" or "Bank" info.
	 * 
	 * @param loginType The visibility of the Card login check mark. If visible - then login as Card, else Bank.
	 */
	private void setLoginType(int loginType) {
		if (View.VISIBLE == loginType)
			toggleBankCardLogin(goToCardLabel);		
		else
			toggleBankCardLogin(goToBankLabel);

	}

	/**
	 * registerNewUser() This method launches the registration screen when a
	 * user taps the register now button in the bottom bar.
	 */
	public void registerNewUser() {
		final Intent accountInformationActivity = new Intent(this, RegistrationAccountInformationActivity.class);
		this.startActivity(accountInformationActivity);
		clearInputs();
		errorTextView.setText(emptyString);
		setViewGone(errorTextView);
	}

	/**
	 * Opens ATM Locator screen when user taps the ATM Locator button while 
	 * in the BANK Login Screen
	 */
	public void openAtmLocator() {
		//TODO: Add ATM handler here
		
		//TODO: Remove this code once implemented. This is only for QA testing purposes only
		CharSequence text = "ATM Locator Under Development";
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(this, text, duration);
		toast.show();
	}
	
	/**
	 * Opens Privacy and Security screen when user taps the Privacy and Security button while 
	 * in the Card Login Screen
	 */
	public void openPrivacyAndSecurity() {
		//TODO: Remove this code once implemented. This is only for QA testing purposes only
		CharSequence text = "Privacy & Security Under Development";
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(this, text, duration);
		toast.show();
	}
	
	/**
	 * Opens Privacy and Terms screen when user taps the Privacy and Terms button while 
	 * in the Bank Login Screen
	 */
	public void openPrivacyAndTerms() {
		//TODO: Remove this code once implemented. This is only for QA testing purposes only
		CharSequence text = "Privacy & Terms Under Development";
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(this, text, duration);
		toast.show();
	}
	
	/**
	 * Opens Privacy and Terms screen when user taps the Privacy and Terms button while 
	 * in the Bank Login Screen
	 */
	private void openCustomerService() {
		//TODO: Remove this code once implemented. This is only for QA testing purposes only
		CharSequence text = "Customer Service Under Development";
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(this, text, duration);
		toast.show();
	}
	
	/**
	 * forgotIdAndOrPass() This method is the same as registerNewUser except
	 * that it launches the forgot nav screen and is instead called from Java.
	 */
	private void forgotIdAndOrPass() {
		final Intent forgotIdAndOrPassActivity = new Intent(this, ForgotTypeSelectionActivity.class);
		this.startActivity(forgotIdAndOrPassActivity);
		clearInputs();
	}

	
	/**
	 * Do a GET request to the server to check to see if this vendor id is
	 * registered to this user.
	 * 
	 * @author jthornton
	 */
	protected void getXtifyRegistrationStatus(){
		final AsyncCallback<PushRegistrationStatusDetail> callback = 
				GenericAsyncCallback.<PushRegistrationStatusDetail>builder(this)
				.showProgressDialog(getResources().getString(R.string.push_progress_get_title), 
									getResources().getString(R.string.push_progress_registration_loading), 
									true)
				.withSuccessListener(new PushRegistrationStatusSuccessListener())
				.withErrorResponseHandler(new PushRegistrationStatusErrorHandler(this))
				.launchIntentOnSuccess(NavigationRootActivity.class)
				.finishCurrentActivityOnSuccess(this)
				.clearTextViewsOnComplete(idField, passField)
				.build();
	
		new GetPushRegistrationStatus(this, callback).submit();
	}

	/**
	 * showErrorIfAnyFieldsAreEmpty() Sets error tags for input fields if a
	 * field is empty.
	 * 
	 * @return boolean value to show if any errors should be shown.
	 */
	private boolean showErrorIfAnyFieldsAreEmpty() {

		final boolean wasIdEmpty = Strings.isNullOrEmpty(idField.getText().toString());
		final boolean wasPassEmpty = Strings.isNullOrEmpty(passField.getText().toString());
		
		if(wasIdEmpty || wasPassEmpty) {	
			errorTextView.setTextColor(getResources().getColor(R.color.red));
			errorTextView.setText(R.string.login_error);
			errorTextView.setVisibility(View.VISIBLE);
			setInputFieldsDrawableToRed();
			return true;
		}
		// All fields were populated.
		return false;
	}

	/**
	 * Run the pre-auth call. Check with the server if the version of the
	 * application we are running is OK. Also checks to see if the server is
	 * available and will allow users to login.
	 */
	public void startPreAuthCheck() {
		final AsyncCallback<PreAuthResult> callback = GenericAsyncCallback.<PreAuthResult> builder(this)
				.withSuccessListener(new PreAuthSuccessResponseHandler(this))
				.withErrorResponseHandler(new PreAuthErrorResponseHandler(this))
				.withExceptionFailureHandler(new DefaultExceptionFailureHandler() )
				.withCompletionListener(new LockScreenCompletionListener(this)).build();

		new PreAuthCheckCall(this, callback).submit();
	}

	/* (non-Javadoc)
	 * @see com.discover.mobile.ErrorHandlerUi#getErrorLabel()
	 */
	@Override
	public TextView getErrorLabel() {
		return errorTextView;
	}

	/* (non-Javadoc)
	 * @see com.discover.mobile.ErrorHandlerUi#getInputFields()
	 */
	@Override
	public List<EditText> getInputFields() {
		List<EditText> inputFields = new ArrayList<EditText>();
		inputFields.add(idField);
		inputFields.add(passField);
		return inputFields;
	}
	
	/**
	 * Used to update the globals data stored at login for CARD or BANK and retrieves
	 * user information. Should only be called if logged in otherwise will return false.
	 * 
	 * @param account Specify either Globals.CARD_ACCOUNT or Globals.BANK_ACCOUNT
	 * 
	 * @return Returns true if successful, false otherwise.
	 */
	public boolean updateAccountInformation(AccountType account) {
		boolean ret = false;
		
		//Only update account information if logged in
		if( Globals.isLoggedIn() ) {
			//Load preferences
			Globals.loadPreferences(getContext(), account);
			
			//Set current user for the current session  
			Globals.setCurrentUser(idField.getText().toString());
			
			//Set the current account selected by the user
			Globals.setCurrentAccount(account);
			
			//Set remember ID value in globals. This will be used to determine whether
			//Current User is stored in persistent storage by the Globals class
			Globals.setRememberId(saveUserId);	
			
			ret = true;
		} else {
			if( Log.isLoggable(TAG, Log.ERROR)) {
				Log.w(TAG, "Unable to update account information.");
			}
		}
			
		return ret;
	}

	/**
	 * Used to display splash screen at start-up of the application prior to pre-authentication. If show is
	 * set to true shows splash screen back ground and a progress bar with animation. If show is set to false, 
	 * then login and toolbar views are shown. The login view use an aninmation to fade in.
	 * 
	 * @param show True hides all login views and toolbar, false hides progress bar and fades in login views.
	 */
	public void showSplashScreen(boolean show) {
		ViewGroup loginStartLayout = (ViewGroup) this.findViewById( R.id.login_start_layout );
		final ViewGroup loginPane = (ViewGroup) this.findViewById(R.id.login_pane);
		final ViewGroup toolbar = (ViewGroup)this.findViewById(R.id.login_bottom_button_row);
		
		
		//Verify loginStartLayout has a valid instance of ViewGroup
		if( null != loginStartLayout && null != loginPane && null != toolbar ) {

				//Show progress bar and hide login view
		        if( show ) {
		        	splashProgress.setVisibility(View.VISIBLE);
		  
		        	loginPane.setVisibility(View.GONE);
		        	toolbar.setVisibility(View.GONE);
		        }
		        //Hide progress bar and fade in login view
		        else {
		        	//If login views already visible nothing more needs to be done
		        	if( loginPane.getVisibility() != View.VISIBLE ) {
			        	splashProgress.setVisibility(View.GONE);	
			        	
			        	loginPane.setVisibility(View.VISIBLE);
			        	
			        	Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
			        	animationFadeIn.setAnimationListener(new AnimationListener() {
		                    public void onAnimationStart(Animation anim)
		                    {
		                    };
		                    public void onAnimationRepeat(Animation anim)
		                    {
		                    };
		                    public void onAnimationEnd(Animation anim)
		                    {
		                       toolbar.setVisibility(View.VISIBLE);
		                    };
		                });
						loginPane.startAnimation(animationFadeIn);    
		        	} else {
		        		if( Log.isLoggable(TAG, Log.WARN)) {
		    				Log.w(TAG,"login views already visible");
		    			}
		        	}
		        }
		} else {
			if( Log.isLoggable(TAG, Log.ERROR)) {
				Log.e(TAG,"Unable to find views");
			}
		}
	}
	
	/**
	 * Sets a flag which is used to determine whether Pre-Authentication has been performed
	 * by the application already. This flag helps avoid Pre-Authentication being performed
	 * more than once by the application. In addition, it hides the splash screen and shows 
	 * login views in the case the splash screen is being displayed.
	 * 
	 * @param result True if Pre-Authentication has been completed, false otherwise.
	 */
	public void preAuthComplete(boolean result) {
		
		preAuthHasRun = result;
		
		showSplashScreen(false);
	}
	
	/**
	 * @return True if Pre-authentication has been performed already, false otherwise.
	 */
	public boolean getPreAuthHasRun() {
		return this.preAuthHasRun;
	}
}
