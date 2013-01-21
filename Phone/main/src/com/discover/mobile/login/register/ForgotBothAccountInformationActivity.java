package com.discover.mobile.login.register;

import static com.discover.mobile.common.StandardErrorCodes.FAILED_SECURITY;
import static com.discover.mobile.common.StandardErrorCodes.LAST_ATTEMPT_WARNING;
import static com.discover.mobile.common.StandardErrorCodes.MAX_LOGIN_ATTEMPTS;
import static com.discover.mobile.common.StandardErrorCodes.STRONG_AUTH_NOT_ENROLLED;
import static com.discover.mobile.common.StandardErrorCodes.UNSCHEDULED_MAINTENANCE;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.LOCKED_OUT_ACCOUNT;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.NOT_PRIMARY_CARDHOLDER;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.REG_AUTHENTICATION_PROBLEM;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.SAMS_CLUB_MEMBER;

import java.net.HttpURLConnection;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.discover.mobile.R;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.ScreenType;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.auth.GetStrongAuthQuestionCall;
import com.discover.mobile.common.auth.registration.AccountInformationCall;
import com.discover.mobile.common.auth.registration.AccountInformationDetails;
import com.discover.mobile.common.auth.strong.StrongAuthCheckCall;
import com.discover.mobile.common.auth.strong.StrongAuthDetails;
import com.discover.mobile.common.auth.strong.StrongAuthErrorResponse;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.AsyncCallbackAdapter;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.discover.mobile.common.net.json.JsonMessageErrorResponse;
import com.discover.mobile.navigation.HeaderProgressIndicator;
import com.discover.mobile.security.EnhancedAccountSecurityActivity;
/**
 * ForgotBothAccountInformationActivity - provides layout customization to the first step of a user forgetting their
 * credentials.
 * 
 * @author scottseward
 *
 */
public class ForgotBothAccountInformationActivity extends AbstractAccountInformationActivity {
	private static final String TAG = ForgotBothAccountInformationActivity.class.getSimpleName();
	public ForgotBothAccountInformationActivity() {
		super(AnalyticsPage.FORGOT_BOTH_STEP1);
	}
	
	/**
	 * Set the main input field to accept an account number as opposed to a username.
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		accountIdentifierField.setFieldAccountNumber();
	}

	/**
	 * Used for correctly formatting the JSON object to be sent to the server upon form completion.
	 * Fills in the value for an account number instead of a username.
	 */
	@Override
	protected void addCustomFieldToDetails(final AccountInformationDetails details, final String value) {
		details.acctNbr = value;
	}
	
	@Override
	protected NetworkServiceCall<?> createServiceCall(final AsyncCallback<Object> callback,
			final AccountInformationDetails details) {
		
		return new AccountInformationCall(this, callback, details);
	}
	
	/**
	 * Calls the onBack method when the hardware back button is pressed.
	 */
	@Override
	public void onBackPressed() {
		goBack(null);
	}
	
	/**
	 * Instead of meerly 'finishing' the activity, when we are in the forgot something phase, we need
	 * to forget where we are in the process and navigate back to the selection screen where the user chooses
	 * what part of their credentials they forgot.
	 */
	@Override
	public void goBack(final View v) {
		Intent forgotCredentials = new Intent(this, ForgotCredentialsActivity.class);
		startActivity(forgotCredentials);
		finish();
	}
	
	/**
	 * Passes the valid form details as an extra, serializable object, from this screen to the next.
	 */
	@Override
	protected void navToNextScreenWithDetails(AccountInformationDetails details) {
		final Intent createLoginActivity = new Intent(this, getSuccessfulStrongAuthIntentClass());
		createLoginActivity.putExtra(ScreenType.INTENT_KEY, ScreenType.FORGOT_BOTH);    
		createLoginActivity.putExtra(IntentExtraKey.REGISTRATION1_DETAILS, details);
		startActivity(createLoginActivity);
		finish();
	}
	
	/**
	 * The Activity that will appear after a successful Strong Auth challenge.
	 */
	@Override
	protected Class<?> getSuccessfulStrongAuthIntentClass() {
		return CreateLoginActivity.class;
	}
	
	/**
	 * Send a request to the server to check if Strong Auth is required at this step.
	 */
	@Override
	protected void checkForStrongAuth() {
		final AsyncCallback<StrongAuthDetails> callback = new AsyncCallbackAdapter<StrongAuthDetails>() {
			@Override
			public void success(final StrongAuthDetails value) {
				Log.d(TAG, "Success");
				
			}

			@Override
			public boolean handleErrorResponse(final ErrorResponse errorResponse) {
				
				if(errorResponse instanceof StrongAuthErrorResponse)
					return handleStrongAuthErrorResponse((StrongAuthErrorResponse)errorResponse);
				
				// TODO handle or remove cases where we don't have handling
				switch (errorResponse.getHttpStatusCode()) {
					
					case HttpURLConnection.HTTP_UNAUTHORIZED:
						getStrongAuthQuestion();
						return true;
				}
				
				return false;
			}
			
			private boolean handleStrongAuthErrorResponse(final StrongAuthErrorResponse errorResponse) {
				if(errorResponse.getResult().endsWith("skipped")) {
					navToNextScreenWithDetails(accountInformationDetails);
					return true;
				}
				else if (errorResponse.getResult().endsWith("challenge")) { 
					getStrongAuthQuestion();
					return true;
				}
				else
					return false;
			}

			@Override
			public boolean handleMessageErrorResponse(final JsonMessageErrorResponse messageErrorResponse) {

				Log.e(TAG, "Error message: " + messageErrorResponse.getMessage());

				// FIXME add "assertions" for what the HTTP status code should be
				switch(messageErrorResponse.getMessageStatusCode()){
				
					case LOCKED_OUT_ACCOUNT:
						sendToErrorPage(ScreenType.STRONG_AUTH_LOCKED_OUT);
						return true;
						
					case STRONG_AUTH_NOT_ENROLLED:
						sendToErrorPage(ScreenType.STRONG_AUTH_NOT_ENROLLED);
						return true;
						
					case SAMS_CLUB_MEMBER:
						showMainErrorLabelWithText(getString(R.string.account_info_sams_club_card_error_text));
						return true;
						
					case REG_AUTHENTICATION_PROBLEM: // Provided information was incorrect.
						showMainErrorLabelWithText(getString(R.string.account_info_bad_input_error_text));
						return true;
						
					case FAILED_SECURITY:
						showMainErrorLabelWithText(getString(R.string.account_info_bad_input_error_text));
						return true;
					
					case NOT_PRIMARY_CARDHOLDER:
						sendToErrorPage(ScreenType.NOT_PRIMARY_CARDHOLDER);
						return true;
					
					case UNSCHEDULED_MAINTENANCE:
						sendToErrorPage(ScreenType.UNSCHEDULED_MAINTENANCE);
						return true;
						
					case MAX_LOGIN_ATTEMPTS:
						sendToErrorPage(ScreenType.ACCOUNT_LOCKED_FAILED_ATTEMPTS);
						return true;
						
					case LAST_ATTEMPT_WARNING:
						showMainErrorLabelWithText(getString(R.string.login_attempt_warning));
						return true;
						
					default:
						return false;
				}
			}
		};

		final StrongAuthCheckCall strongAuthCall = new StrongAuthCheckCall(this, callback);
		strongAuthCall.submit();
	}
	
	/**
	 * If strong auth is required, then request the question to be answered from the server.
	 */
	private void getStrongAuthQuestion() {
		final ProgressDialog progress = ProgressDialog.show(this, "Discover", "Loading...", true);

		final AsyncCallback<StrongAuthDetails> callback = new AsyncCallbackAdapter<StrongAuthDetails>() {
			@Override
			public void success(final StrongAuthDetails value) {

				progress.dismiss();
				strongAuthQuestion = value.questionText;
				strongAuthQuestionId = value.questionId;
				
				navToStrongAuth();
			}

			@Override
			public boolean handleErrorResponse(final ErrorResponse errorResponse) {
				progress.dismiss();
				
				// FIXME
				switch (errorResponse.getHttpStatusCode()) {
					case HttpURLConnection.HTTP_BAD_REQUEST:
						return true;
					case HttpURLConnection.HTTP_UNAUTHORIZED:
						return true;
					case HttpURLConnection.HTTP_INTERNAL_ERROR: //couldn't authenticate user info.
						return true;
					case HttpURLConnection.HTTP_FORBIDDEN:
						return true;
				}
				
				return false;
			}
			
			@Override
			public boolean handleMessageErrorResponse(final JsonMessageErrorResponse messageErrorResponse) {
				Log.e(TAG, "Error message: " + messageErrorResponse.getMessage());
				
				// FIXME
				switch(messageErrorResponse.getMessageStatusCode()){
				
				default://TODO properly handle these ^ v
					return true;
				}
				
			}
		};

		final GetStrongAuthQuestionCall strongAuthCall = 
				new GetStrongAuthQuestionCall(this, callback);
		strongAuthCall.submit();
		
	}
	
	private static final int STRONG_AUTH_ACTIVITY = 0;
	
	/**
	 * Launch the strong auth Activity with the question that was retrieved from the get strong auth question call.
	 */
	private void navToStrongAuth() {
		
		final Intent strongAuth = new Intent(this, EnhancedAccountSecurityActivity.class);
		
		strongAuth.putExtra(IntentExtraKey.STRONG_AUTH_QUESTION, strongAuthQuestion);
		strongAuth.putExtra(IntentExtraKey.STRONG_AUTH_QUESTION_ID, strongAuthQuestionId);
		
		startActivityForResult(strongAuth, STRONG_AUTH_ACTIVITY);
		
	}
	
	/**
	 * Handles the result of when the strong auth screen finishes.
	 */
	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {		
		if(requestCode == STRONG_AUTH_ACTIVITY) {
			if(resultCode == RESULT_OK) {
				navToNextScreenWithDetails(accountInformationDetails);
			} else if (resultCode == RESULT_CANCELED){
				finish();
			}
		}
	}

	@Override
	public void goBack() {
		Intent forgotCredentials = new Intent(this, ForgotCredentialsActivity.class);
		startActivity(forgotCredentials);
		finish();
	}
	
	@Override
	protected void setHeaderProgressText() {
			HeaderProgressIndicator headerProgressBar = (HeaderProgressIndicator)findViewById(R.id.header);
			headerProgressBar.setTitle(R.string.enter_info, R.string.create_login, R.string.confirm);
	}
	
}
