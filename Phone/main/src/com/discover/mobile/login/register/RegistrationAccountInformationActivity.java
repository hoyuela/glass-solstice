package com.discover.mobile.login.register;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.CommonMethods;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.auth.registration.AccountInformationCall;
import com.discover.mobile.common.auth.registration.AccountInformationDetails;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.error.ErrorHandlerFactory;
import com.discover.mobile.navigation.HeaderProgressIndicator;

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
		super(AnalyticsPage.FORGOT_BOTH_STEP1);
	}
	
	/**
	 * Adds the main input field to the AccountInformaitonDetails object as an account number field.
	 */
	@Override
	protected void addCustomFieldToDetails(final AccountInformationDetails details, final String value) {
		details.acctNbr = CommonMethods.getSpacelessString(value);
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

	@Override
	public void goBack() {
		finish();
	}

	/**
	 * Set the text that is displayed in the top header progress bar.
	 */
	@Override
	protected void setHeaderProgressText() {
			HeaderProgressIndicator headerProgressBar = (HeaderProgressIndicator)findViewById(R.id.header);
			headerProgressBar.setTitle(R.string.enter_info, R.string.create_login, R.string.confirm);
	}
	
	/**
	 * Decide what to do when the strong auth activity exits. If it was successful, then navigate
	 * to the next applicable screen. If not, cancel the registration process.
	 */
	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {	
		Log.d("ACTIVITY DID GIVE RESULT","ACTIVITY DID GIVE RESULT");
		if(requestCode == STRONG_AUTH_ACTIVITY) {
			if(resultCode == RESULT_OK) {
				navToNextScreenWithDetails(accountInformationDetails);
			} else if (resultCode == RESULT_CANCELED){
				finish();
			}
		}
	}

	@Override
	public TextView getErrorLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EditText> getInputFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void showCustomAlert(AlertDialog alert) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showOneButtonAlert(int title, int content, int buttonText) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showDynamicOneButtonAlert(int title, String content,
			int buttonText) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendToErrorPage(int errorCode, int titleText, int errorText) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendToErrorPage(int errorText) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLastError(int errorCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getLastError() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ErrorHandlerFactory getErrorHandlerFactory() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
