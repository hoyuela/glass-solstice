package com.discover.mobile.card.login.register;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.uiwidget.HeaderProgressIndicator;
import com.discover.mobile.card.common.uiwidget.HeaderTwoStepProgressIndicator;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.card.login.register.EnterNewPasswordActivity;
import com.discover.mobile.card.login.register.ForgotCredentialsActivity;
import com.discover.mobile.card.privacyterms.PrivacyTermsLanding;
import com.discover.mobile.card.services.auth.registration.AccountInformationDetails;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.utils.CommonUtils;

/**
 * ForgotPasswordAccountInformationActivity - This activity extends the
 * AbstractAccountInformationActivity and provides the functionality for the
 * first step of a user forgetting their password.
 * 
 * It implements and overrides methods from AbstractAccountInformationActivity
 * 
 * @author scottseward
 * 
 */
public class AccountUnlockInformationActivity extends ForgotOrRegisterFirstStep {
	// AccountUnlockFirstStep {

	/**
	 * Setup the main input field to accept a username instead of an account
	 * number.
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		accountIdentifierField.setFieldUsername();
	}

	/**
	 * Initiates the proper analytics services call.
	 */
	public AccountUnlockInformationActivity() {
		super(AnalyticsPage.ACCOUNT_UNLOCK_STEP1);
	}

	/**
	 * Hide the text label that contains information that pertains to account
	 * numbers.
	 */
	@Override
	protected void doCustomUiSetup() {
		CommonUtils.setViewGone(accountIdentifierFieldRestrictionsLabel);
		accountIdentifierField.setFieldUsername();
	}

	/**
	 * Add the main input field to the AccountInformationDetails object as a
	 * userId and not a account number.
	 */
	@Override
	protected void addCustomFieldToDetails(
			final AccountInformationDetails details, final String value) {
		details.userId = value;
	}

	/* 13.4 Code CleanUp */
	/*
	 * @Override protected NetworkServiceCall<?> createServiceCall( final
	 * AsyncCallback<Object> callback, final AccountInformationDetails details)
	 * {
	 * 
	 * return new ForgotPasswordCall(this, callback, details); }
	 */

	/**
	 * Returns the activity class that will be the launched activity after this,
	 * upon successful or skipped strong auth.
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
		final HeaderProgressIndicator headerProgressBar = (HeaderTwoStepProgressIndicator) findViewById(R.id.header);
		headerProgressBar.setTitle(R.string.enter_info,
				R.string.create_password, R.string.confirm);
	}

	@Override
	public void goBack() {
		finish();
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
	protected final String getScreenType() {
		return IntentExtraKey.SCREEN_ACCOUNT_UNLOCK;
	}

	@Override
	public void onClick(final View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.provide_feedback_button) {
			if (accountIdentifierField.isUsernameField()) {
				Utils.createProvideFeedbackDialog(
						AccountUnlockInformationActivity.this,
						FORGOTPASSWORDREFERER);
			}
		} else if (v.getId() == R.id.account_info_cancel_label) {
			finish();
		} else if (v.getId() == R.id.privacy_terms) {
			// Changes for 13.4 start
			// FacadeFactory.getBankFacade().navToCardPrivacyTerms();
			Intent privacyTerms = new Intent(
					AccountUnlockInformationActivity.this,
					PrivacyTermsLanding.class);
			startActivity(privacyTerms);
			// Changes for 13.4 end
		}
		// Defect id 95853
	}
}
