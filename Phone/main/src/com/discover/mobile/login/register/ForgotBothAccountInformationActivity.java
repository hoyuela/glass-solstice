package com.discover.mobile.login.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.discover.mobile.R;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.ScreenType;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.auth.registration.AccountInformationCall;
import com.discover.mobile.common.auth.registration.AccountInformationDetails;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.navigation.HeaderProgressIndicator;
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
	
	@Override
	public void goBack() {
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
	protected void setHeaderProgressText() {
			HeaderProgressIndicator headerProgressBar = (HeaderProgressIndicator)findViewById(R.id.header);
			headerProgressBar.setTitle(R.string.enter_info, R.string.create_login, R.string.confirm);
	}
	
}
