package com.discover.mobile.card.login.register;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.discover.mobile.card.R;
import com.discover.mobile.card.error.CardErrHandler;
import com.discover.mobile.card.services.auth.registration.AccountInformationCall;
import com.discover.mobile.card.services.auth.registration.AccountInformationDetails;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.error.ErrorHandler;
import com.discover.mobile.common.facade.FacadeFactory;
import com.discover.mobile.common.nav.HeaderProgressIndicator;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.utils.CommonUtils;

import com.discover.mobile.card.common.utils.Utils;
/**
 * ForgotBothAccountInformationActivity - provides layout customization to the first step of a user forgetting their
 * credentials.
 * 
 * @author scottseward
 *
 */
public class ForgotBothAccountInformationActivity extends ForgotOrRegisterFirstStep {

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
		//Value is a stylized account number with spaces, remove these spaces and continue.
		details.acctNbr = CommonUtils.getSpacelessString(value);
	}

	@Override
	protected NetworkServiceCall<?> createServiceCall(final AsyncCallback<Object> callback,
			final AccountInformationDetails details) {

		return new AccountInformationCall(this, callback, details);
	}

	/**
	 * The Activity that will appear after a successful Strong Auth challenge.
	 */
	@Override
	protected Class<?> getSuccessfulStrongAuthIntentClass() {
		return CreateLoginActivity.class;
	}

	@Override
	protected boolean isForgotFlow() {
		return true;
	}

	@Override
	protected void setHeaderProgressText() {
		final HeaderProgressIndicator headerProgressBar = (HeaderProgressIndicator)findViewById(R.id.header);
		headerProgressBar.setTitle(R.string.enter_info, R.string.create_login, R.string.confirm);
	}

	@Override
	public void goBack() {
		finish();
		final Intent forgotCredentials = new Intent(this, ForgotCredentialsActivity.class);
		startActivity(forgotCredentials);
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
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.provide_feedback_button) {
            if(!(accountIdentifierField.isUsernameField()))
            Utils.createProvideFeedbackDialog(ForgotBothAccountInformationActivity.this, FORGOTBOTHREFERER);
        }else if(v.getId()==R.id.account_info_cancel_label)
        {
        	finish();
    		final Bundle bundle = new Bundle();
            bundle.putBoolean(IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE, false);
            bundle.putBoolean(IntentExtraKey.SESSION_EXPIRED, false);
            FacadeFactory.getLoginFacade().navToLoginWithMessage(this, bundle);
        }
    }
    
}
