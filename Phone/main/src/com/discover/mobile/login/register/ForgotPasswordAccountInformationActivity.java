package com.discover.mobile.login.register;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.CommonMethods;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.auth.forgot.ForgotPasswordCall;
import com.discover.mobile.common.auth.registration.AccountInformationDetails;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.error.ErrorHandlerFactory;
import com.discover.mobile.navigation.HeaderProgressIndicator;

/**
 * ForgotPasswordAccountInformationActivity - This activity extends the AbstractAccountInformationActivity
 * and provides the functionality for the first step of a user forgetting their password.
 * 
 * It implements and overrides methods from AbstractAccountInformationActivity
 * 
 * @author scottseward
 *
 */
public class ForgotPasswordAccountInformationActivity extends AbstractAccountInformationActivity {
	
	/**
	 * Setup the main input field to accept a username instead of an account number.
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		accountIdentifierField.setFieldUsername();
	}
	
	/**
	 * Initiates the proper analytics services call.
	 */
	public ForgotPasswordAccountInformationActivity() {
		super(AnalyticsPage.FORGOT_PASSWORD_STEP1);
	} 
	
	/**
	 * When the hardware back button is pressed, call the goBack method.
	 */
	@Override
	public void onBackPressed() {
		goBack(null);
	}
	
	/**
	 * goBack will navigate the user back to the forgot selection activity. This is the screen that lets
	 * the user choose between forgotten password/user id/ or both. We also finish this activity so that
	 * they cannot navigate back to this screen.
	 */
	@Override
	public void goBack(final View v) {
		Intent forgotCredentials = new Intent(this, ForgotCredentialsActivity.class);
		startActivity(forgotCredentials);
		finish();
	}
	
	/**
	 * Hide the text label that contains information that pertains to account numbers.
	 */
	@Override
	protected void doCustomUiSetup() {
		CommonMethods.setViewGone(accountIdentifierFieldRestrictionsLabel);
		accountIdentifierFieldLabel.setText(R.string.user_id);
		accountIdentifierField.setFieldUsername();
	}
	
	/**
	 * Add the main input field to the AccountInformationDetails object as a userId and not a account number.
	 */
	@Override
	protected void addCustomFieldToDetails(final AccountInformationDetails details, final String value) {
		details.userId = value;
	}
	
	@Override
	protected NetworkServiceCall<?> createServiceCall(final AsyncCallback<Object> callback,
			final AccountInformationDetails details) {
		
		return new ForgotPasswordCall(this, callback, details);
	}
	
	/**
	 * Returns the activity class that will be the launched activity after this, upon successful or skipped
	 * strong auth.
	 */
	@Override
	protected Class<?> getSuccessfulStrongAuthIntentClass() {
		return EnterNewPasswordActivity.class;
	}

	/**
	 * The inherited goBack method from NotLoggedInRoboActivity for the software back button.
	 */
	@Override
	public void goBack() {
		Intent forgotCredentials = new Intent(this, ForgotCredentialsActivity.class);
		startActivity(forgotCredentials);
		finish();		
	}
	
	/**
	 * Setup the header progress bar appearance.
	 */
	@Override
	protected void setHeaderProgressText() {
			HeaderProgressIndicator headerProgressBar = (HeaderProgressIndicator)findViewById(R.id.header);
			headerProgressBar.setTitle(R.string.enter_info, R.string.create_password, R.string.confirm);
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
	
	/**
	 * This method handles the result of the Strong Auth activity.
	 * When Strong Auth finishes, either navigate to the next screen, or cancel the registration process.
	 */
	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {	
		Log.d("ACTIVITY DID GIVE RESULT","ACTIVITY DID GIVE RESULT");
		if(requestCode == STRONG_AUTH_ACTIVITY) {
			if(resultCode == RESULT_OK) {
				navToNextScreenWithDetails(null);
			} else if (resultCode == RESULT_CANCELED){
				finish();
			}
		}
	}

	/**
	 * Inherited method from AbstractAccountInformation Activity. However, ForgotPassword step 1 does not
	 * need to send any details object to the next activity.
	 */
	@Override
	protected void navToNextScreenWithDetails(AccountInformationDetails details) {
		Intent createNewPassword = new Intent(this, getSuccessfulStrongAuthIntentClass());
		startActivity(createNewPassword);
		finish();
	}

}
