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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.discover.mobile.ErrorHandlerUi;
import com.discover.mobile.NotLoggedInRoboActivity;
import com.discover.mobile.R;
import com.discover.mobile.common.CurrentSessionDetails;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.SharedPreferencesWrapper;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.auth.AccountDetails;
import com.discover.mobile.common.auth.AuthenticateCall;
import com.discover.mobile.common.auth.InputValidator;
import com.discover.mobile.common.auth.PreAuthCheckCall;
import com.discover.mobile.common.auth.PreAuthCheckCall.PreAuthResult;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.GenericAsyncCallback;
import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.common.push.PushNotificationService;
import com.discover.mobile.common.push.registration.GetPushRegistrationStatus;
import com.discover.mobile.common.push.registration.PushRegistrationStatusDetail;
import com.discover.mobile.login.register.ForgotTypeSelectionActivity;
import com.discover.mobile.login.register.RegistrationAccountInformationActivity;
import com.discover.mobile.navigation.NavigationRootActivity;
import com.discover.mobile.push.PushRegistrationStatusErrorHandler;
import com.discover.mobile.push.PushRegistrationStatusSuccessListener;
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
public class LoginActivity extends NotLoggedInRoboActivity implements ErrorHandlerUi {
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
     *	Hide the action bar 
     */
    public void showActionBar(){
    	super.showActionBar();
    	getSupportActionBar().hide();
    }
	
	
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

	@InjectView(R.id.register_now_button)
	private Button registerButton;

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

	// IMAGES

	@InjectView(R.id.card_check_mark)
	private ImageView cardCheckMark;

	@InjectView(R.id.bank_check_mark)
	private ImageView bankCheckMark;
	
	@InjectView(R.id.remember_user_id_button)
	private ImageView toggleImage;

	// RESOURCES

	@InjectResource(R.string.hide)
	private String HIDE;

	@InjectResource(R.string.show)
	private String SHOW;

	/**
	 * Non roboguise attributes
	 */
	
	private Resources res;

	private boolean preAuthHasRun = false;
	boolean saveUserId = false;
	
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
		if(null == extras){return;}
		if(extras.getBoolean(IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE, false)){
			errorTextView.setText(getString(R.string.logout_sucess));
			errorTextView.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * Resume the activity
	 */
	@Override
	public void onResume(){
		super.onResume();
		maybeShowUserLoggedOut();
		loadSavedCredentials();
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
		if (savedInstanceState == null) {
			return;
		}
		idField.setText(savedInstanceState.getString(ID_KEY));
		passField.setText(savedInstanceState.getString(PASS_KEY));
		preAuthHasRun = savedInstanceState.getBoolean(PRE_AUTH_KEY);

		passField.setInputType(savedInstanceState.getInt(PW_INPUT_TYPE_KEY));
		hideButton.setText(savedInstanceState.getString(HIDE_LABEL_KEY));

		setLoginType(savedInstanceState.getInt(LOGIN_TYPE_KEY));
		setCheckMark(savedInstanceState.getBoolean(SAVE_ID_KEY));
		
		errorTextView.setText(savedInstanceState.getString(ERROR_MESSAGE_KEY));
		errorTextView.setVisibility(savedInstanceState.getInt(ERROR_MESSAGE_VISIBILITY));
		
		resetInputFieldColors();
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
		boolean rememberIdCheckState = 
				SharedPreferencesWrapper.getValueFromSharedPrefs(this, SharedPreferencesWrapper.REMEMBER_USER_ID, false);
		
		if(rememberIdCheckState){
			idField.setText(SharedPreferencesWrapper.getValueFromSharedPrefs(this, SharedPreferencesWrapper.USER_ID, ""));
			setCheckMark(rememberIdCheckState);
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
				logIn();
			}
		});

		registerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				setViewGone(errorTextView);
				registerNewUser();
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
	 * logIn() If the user id, or password field are effectively blank, do not
	 * allow a service call to be made display the error message for id/pass not
	 * matching records. If the fields have data - submit it to the server for
	 * validation.
	 */
	private void logIn() {
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
		InputValidator validator = new InputValidator();
		validator.isCardAccountNumberValid(inputId);
		
		if(saveUserId && validator.wasAccountNumberValid) {
			errorTextView.setText(getString(R.string.cannot_save_account_number));
			errorTextView.setVisibility(View.VISIBLE);
			clearInputs();
			toggleCheckBox(idField);
			setInputFieldsDrawableToRed();
			return true;
		}
		else{
			return false;
	
		}
	}

	/**
	 * runAuthWithUsernameAndPassword(final String username, final String
	 * password) This method submits the users information to the server for
	 * verification.
	 * 
	 * The AsyncCallback handles the success and failure of the call and is
	 * responsible for handling and presenting error messages to the user.
	 */
	private void runAuthWithUsernameAndPassword(final String username,
			final String password) {
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
						saveCredentials();
						CurrentSessionDetails.getCurrentSessionDetails()
								.setAccountDetails(value);
						getXtifyRegistrationStatus();
						clearInputs();
					}
				})
				.withErrorResponseHandler(new LoginErrorResponseHandler(this))
								.build();

		new AuthenticateCall(this, callback, username, password).submit();
	}
	
	/**
	 * Saves a successful user id to to file for use later. Also saves the state of the check box.
	 * If the checkbox was not checked when we log in, any previously saved ID will be deleted.
	 */
	public void saveCredentials() {
		
		if(saveUserId){
			SharedPreferencesWrapper.saveToSharedPrefs(this, SharedPreferencesWrapper.USER_ID, idField.getText().toString());
		}
		else{
			SharedPreferencesWrapper.saveToSharedPrefs(this, SharedPreferencesWrapper.USER_ID, emptyString);
		}
		SharedPreferencesWrapper.saveToSharedPrefs(this, SharedPreferencesWrapper.REMEMBER_USER_ID, saveUserId);
	}

	/**
	 * toggleCheckBox(final View v) This method handles the state of the check
	 * box on the login screen.
	 * 
	 * It changes its image and the state of the saveUserId value.
	 */
	public void toggleCheckBox(final View v) {
	
		if (saveUserId) {
			toggleImage.setBackgroundDrawable(res.getDrawable(R.drawable.gray_gradient_square));
			toggleImage.setImageDrawable(res.getDrawable(R.drawable.transparent_square));
			saveUserId = false;
		} else {
			toggleImage.setBackgroundDrawable(res.getDrawable(R.drawable.black_gradient_square));
			toggleImage.setImageDrawable(res.getDrawable(R.drawable.white_check_mark));
			saveUserId = true;
		}

		SharedPreferencesWrapper.saveToSharedPrefs(this, SharedPreferencesWrapper.REMEMBER_USER_ID, saveUserId);
	}

	/**
	 * togglePasswordVisibility(final View v) This method handles showing and
	 * hiding of a users password. It will show a user's password in plain text
	 * if the user taps the Show text label on the home screen. And hide it if
	 * it says 'Hide'
	 */
	public void togglePasswordVisibility(final View v) {
		final String buttonText = hideButton.getText().toString();
		if(HIDE.equals(buttonText)) {
			hideButton.setText(SHOW);
			passField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		} else {
			hideButton.setText(HIDE);
			passField.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		}

	}

	/**
	 * toggleBankCardLogin(final View v) This method handles the login choices
	 * for loging in as a bank user or a card user.
	 * 
	 * It merely changes the visible position of a check mark and the color of
	 * the labels next to it.
	 */
	public void toggleBankCardLogin(final View v) {

		if (v.equals(goToCardLabel)) {
			goToCardLabel.setTextColor(getResources().getColor(R.color.black));
			setViewVisible(cardCheckMark);

			setViewInvisible(bankCheckMark);
			goToBankLabel.setTextColor(getResources().getColor(R.color.blue_link));
		} else {

			goToCardLabel.setTextColor(getResources().getColor(
					R.color.blue_link));
			setViewInvisible(cardCheckMark);
			setViewVisible(bankCheckMark);
			goToBankLabel.setTextColor(getResources().getColor(R.color.black));
		}

	}

	/**
	 * clearInputs() Removes any text in the login input fields.
	 */
	private void clearInputs() {
		idField.setText(emptyString);
		passField.setText(emptyString);
	}
	/**
	 * Sets the check mark on the login screen to the given boolean (checked/unchecked) state.
	 * 
	 * @param shouldBeChecked Sets the check mark to checked or unchecked for true or false respectively.
	 */
	private void setCheckMark(boolean shouldBeChecked) {
		saveUserId = !shouldBeChecked;
		toggleCheckBox(toggleImage);
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
		clearInputs();
		final Intent accountInformationActivity = new Intent(this, RegistrationAccountInformationActivity.class);
		this.startActivity(accountInformationActivity);
	}

	/**
	 * forgotIdAndOrPass() This method is the same as registerNewUser except
	 * that it launches the forgot nav screen and is instead called from Java.
	 */
	private void forgotIdAndOrPass() {
		clearInputs();
		final Intent forgotIdAndOrPassActivity = new Intent(this, ForgotTypeSelectionActivity.class);
		this.startActivity(forgotIdAndOrPassActivity);
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
		final SuccessListener<PreAuthResult> optionalUpdateListener = new PreAuthSuccessResponseHandler(
				this);

		final AsyncCallback<PreAuthResult> callback = GenericAsyncCallback
				.<PreAuthResult> builder(this)
				.showProgressDialog("Discover", "Loading...", true)
				.withSuccessListener(optionalUpdateListener)
				.withErrorResponseHandler(
						new PreAuthErrorResponseHandler(this)).build();

		new PreAuthCheckCall(this, callback).submit();
		preAuthHasRun = true;

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
}
