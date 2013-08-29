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
import com.discover.mobile.card.common.uiwidget.HeaderProgressIndicator;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.card.privacyterms.PrivacyTermsLanding;
import com.discover.mobile.card.services.auth.registration.AccountInformationDetails;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.error.ErrorHandler;
import com.discover.mobile.common.facade.FacadeFactory;
import com.discover.mobile.common.utils.CommonUtils;

/**
 * This activity sets up the abstract account information screen to handle user
 * registration. It's main responsibility is setting the main input field to
 * accept an account number and then handling the account number in the JSON
 * object that is sent to the server for authentication.
 * 
 * @author scottseward
 * 
 */
public class RegistrationAccountInformationActivity extends
        ForgotOrRegisterFirstStep {

    /**
     * Setup the main input field to be for an account number.
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountIdentifierField.setFieldAccountNumber();
        Utils.hideSpinner();
        /* 13.4 Changes Start */
        welcomeHeading.setText(R.string.registration_title);
        welcomeHeading.setVisibility(View.GONE);
    }

    public RegistrationAccountInformationActivity() {
        super(AnalyticsPage.FORGOT_BOTH_STEP1);
    }

    /**
     * Adds the main input field to the AccountInformaitonDetails object as an
     * account number field.
     */
    @Override
    protected void addCustomFieldToDetails(
            final AccountInformationDetails details, final String value) {
        details.acctNbr = CommonUtils.getSpacelessString(value);
    }

    /* 13.4 Code CleanUp */
    /*
     * @Override protected NetworkServiceCall<?> createServiceCall( final
     * AsyncCallback<Object> callback, final AccountInformationDetails details)
     * {
     * 
     * return new AccountInformationCall(this, callback, details); }
     */

    @Override
    public void goBack() {
        finish();
        // ;FacadeFactory.getLoginFacade().navToLogin(this);//DEFECT 97478
    }

    /**
     * Returns the Activity that will be launched upon successful or skipped
     * Strong Auth.
     */
    @Override
    protected Class<?> getSuccessfulStrongAuthIntentClass() {
        return CreateLoginActivity.class;
    }

    /**
     * Set the text that is displayed in the top header progress bar.
     */
    @Override
    protected void setHeaderProgressText() {
        final HeaderProgressIndicator headerProgressBar = (HeaderProgressIndicator) findViewById(R.id.header);
        headerProgressBar.setTitle(R.string.enter_info, R.string.create_login,
                R.string.confirm);
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
    public void showCustomAlert(final AlertDialog alert) {

    }

    @Override
    public void showOneButtonAlert(final int title, final int content,
            final int buttonText) {

    }

    @Override
    public void showDynamicOneButtonAlert(final int title,
            final String content, final int buttonText) {

    }

    @Override
    public Context getContext() {

        return null;
    }

    @Override
    public void setLastError(final int errorCode) {

    }

    @Override
    public int getLastError() {

        return 0;
    }

    @Override
    public ErrorHandler getErrorHandler() {

        return null;
    }

    @Override
    public void onClick(final View v) {

        if (v.getId() == R.id.provide_feedback_button) {
            if (!accountIdentifierField.isUsernameField()) {
                Utils.createProvideFeedbackDialog(
                        RegistrationAccountInformationActivity.this,
                        FORGOTBOTHREFERER);
            }
        } else if (v.getId() == R.id.account_info_cancel_label) {
            finish();
            final Bundle bundle = new Bundle();
            bundle.putBoolean(IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE,
                    false);
            bundle.putBoolean(IntentExtraKey.SESSION_EXPIRED, false);
            FacadeFactory.getLoginFacade().navToLoginWithMessage(this, bundle);
            // Defect id 95853
        } else if (v.getId() == R.id.privacy_terms) {
            // Changes for 13.4 start
            // FacadeFactory.getBankFacade().navToCardPrivacyTerms();
            Intent privacyTerms = new Intent(
                    RegistrationAccountInformationActivity.this,
                    PrivacyTermsLanding.class);
            startActivity(privacyTerms);
            // Changes for 13.4 end
        }
        // Defect id 95853

    }

	@Override
	protected String getScreenType() {
		return IntentExtraKey.SCREEN_REGISTRATION;
	}
}
