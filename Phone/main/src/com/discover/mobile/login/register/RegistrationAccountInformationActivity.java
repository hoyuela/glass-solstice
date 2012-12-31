package com.discover.mobile.login.register;

import android.content.Intent;
import android.os.Bundle;

import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.auth.registration.AccountInformationCall;
import com.discover.mobile.common.auth.registration.AccountInformationDetails;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.NetworkServiceCall;

/**
 * This activity sets up the abstract account information screen to handle user registration.
 * It's main responsibility is setting the main input field to accept an account number and then
 * handling the account number in the JSON object that is sent to the server for authentication.
 * 
 * @author scottseward
 *
 */
public class RegistrationAccountInformationActivity extends AbstractAccountInformationActivity {
	
	/**
	 * Setup the main input field to be for an account number.
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		accountIdentifierField.setFieldAccountNumber();
	}
	
	public RegistrationAccountInformationActivity() {
		// TODO make sure this shouldn't be specfic to registration without forgetting
		super(AnalyticsPage.FORGOT_BOTH_STEP1);
	}
	
	/**
	 * Adds the main input field to the AccountInformaitonDetails object as an account number field.
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
	 * Returns the Activity that will be launched upon successful or skipped Strong Auth.
	 */
	@Override
	protected Class<?> getSuccessfulStrongAuthIntentClass() {
		return CreateLoginActivity.class;
	}
	

	/**
	 * Put all of the form details as a serializable object extra and pass it to the next activity
	 * which will append more info onto that object.
	 */
	@Override
	protected void navToNextScreenWithDetails(AccountInformationDetails details) {
		final Intent createLoginActivity = new Intent(this, getSuccessfulStrongAuthIntentClass());
		createLoginActivity.putExtra(IntentExtraKey.REGISTRATION1_DETAILS, details);
		startActivity(createLoginActivity);
		finish();
	}
	
	
}
