package com.discover.mobile.card.login.register;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.discover.mobile.card.R;
import com.discover.mobile.card.error.CardErrHandler;
import com.discover.mobile.card.services.auth.registration.AccountInformationCall;
import com.discover.mobile.card.services.auth.registration.AccountInformationDetails;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.error.ErrorHandler;
import com.discover.mobile.common.facade.FacadeFactory;
import com.discover.mobile.common.nav.HeaderProgressIndicator;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.utils.CommonUtils;

/**
 * This activity sets up the abstract account information screen to handle user registration.
 * It's main responsibility is setting the main input field to accept an account number and then
 * handling the account number in the JSON object that is sent to the server for authentication.
 * 
 * @author scottseward
 *
 */
public class RegistrationAccountInformationActivity extends ForgotOrRegisterFirstStep {

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
		details.acctNbr = CommonUtils.getSpacelessString(value);
	}

	@Override
	protected NetworkServiceCall<?> createServiceCall(final AsyncCallback<Object> callback, 
			final AccountInformationDetails details) {

		return new AccountInformationCall(this, callback, details);
	}

	@Override
	public void goBack() {
		finish();
		FacadeFactory.getLoginFacade().navToLogin(this);
	}

	/**
	 * Returns the Activity that will be launched upon successful or skipped Strong Auth.
	 */
	@Override
	protected Class<?> getSuccessfulStrongAuthIntentClass() {
		return CreateLoginActivity.class;
	}

	@Override
	protected boolean isForgotFlow() {
		return false;
	}

	/**
	 * Set the text that is displayed in the top header progress bar.
	 */
	@Override
	protected void setHeaderProgressText() {
		final HeaderProgressIndicator headerProgressBar = (HeaderProgressIndicator)findViewById(R.id.header);
		headerProgressBar.setTitle(R.string.enter_info, R.string.create_login, R.string.confirm);
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
	public void showCustomAlert(final AlertDialog alert) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showOneButtonAlert(final int title, final int content, final int buttonText) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showDynamicOneButtonAlert(final int title, final String content,
			final int buttonText) {
		// TODO Auto-generated method stub

	}

	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLastError(final int errorCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getLastError() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ErrorHandler getErrorHandler() {
		// TODO Auto-generated method stub
		return null;
	}
}
