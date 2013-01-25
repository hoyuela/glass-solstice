package com.discover.mobile.login.register;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
public class ForgotPasswordAccountInformationActivity extends ForgotOrRegisterFirstStep {
	
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
	 * Setup the header progress bar appearance.
	 */
	@Override
	protected void setHeaderProgressText() {
			HeaderProgressIndicator headerProgressBar = (HeaderProgressIndicator)findViewById(R.id.header);
			headerProgressBar.setTitle(R.string.enter_info, R.string.create_password, R.string.confirm);
	}
	
	@Override
	public void finish() {
		super.finish();
		Intent forgotCredentialsActivity = new Intent(this, ForgotCredentialsActivity.class);
		startActivity(forgotCredentialsActivity);
	}

	@Override
	public TextView getErrorLabel() {
		return null;
	}

	@Override
	public List<EditText> getInputFields() {
		return null;
	}

	@Override
	public void showCustomAlert(AlertDialog alert) {
		
	}

	@Override
	public void showOneButtonAlert(int title, int content, int buttonText) {
		
	}

	@Override
	public void showDynamicOneButtonAlert(int title, String content,
			int buttonText) {
		
	}

	@Override
	public Context getContext() {
		return null;
	}

	@Override
	public void setLastError(int errorCode) {
		
	}

	@Override
	public int getLastError() {
		return 0;
	}

	@Override
	public ErrorHandlerFactory getErrorHandlerFactory() {
		return null;
	}

}
