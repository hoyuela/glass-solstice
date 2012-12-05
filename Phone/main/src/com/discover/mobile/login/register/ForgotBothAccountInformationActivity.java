package com.discover.mobile.login.register;

import android.content.Intent;
import android.view.View;

import com.discover.mobile.R;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.ScreenType;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.auth.InputValidator;
import com.discover.mobile.common.auth.registration.AccountInformationCall;
import com.discover.mobile.common.auth.registration.AccountInformationDetails;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.NetworkServiceCall;
/**
 * ForgotBothAccountInformationActivity - provides layout customization to the first step of a user forgetting their
 * credentials.
 * 
 * @author scottseward
 *
 */
public class ForgotBothAccountInformationActivity extends AbstractAccountInformationActivity {
	
	public ForgotBothAccountInformationActivity() {
		super(AnalyticsPage.FORGOT_BOTH_STEP1);
	}

	@Override
	protected int getActivityTitleLabelResourceId() {
		return R.string.forgot_both_text;
	}
	
	@Override
	protected void addCustomFieldToDetails(final AccountInformationDetails details, final String value) {
		details.acctNbr = value;
	}
	
	@Override
	protected boolean areDetailsValid(final InputValidator validator) {
		return validator.wasAccountInfoComplete();
	}
	
	@Override
	protected NetworkServiceCall<?> createServiceCall(final AsyncCallback<Object> callback,
			final AccountInformationDetails details) {
		
		return new AccountInformationCall(this, callback, details);
	}
	
	@Override
	public void onBackPressed() {
		goBack(null);
	}
	
	@Override
	public void goBack(final View v) {
		Intent forgotCredentials = new Intent(this, ForgotTypeSelectionActivity.class);
		startActivity(forgotCredentials);
		finish();
	}
	
	@Override
	protected void navToNextScreenWithDetails(AccountInformationDetails details) {
		final Intent createLoginActivity = new Intent(this, getSuccessfulStrongAuthIntentClass());
		createLoginActivity.putExtra(ScreenType.INTENT_KEY, ScreenType.FORGOT_BOTH);    
		createLoginActivity.putExtra(IntentExtraKey.REGISTRATION1_DETAILS, details);
		startActivity(createLoginActivity);
		finish();
	}
	
	@Override
	protected Class<?> getSuccessfulStrongAuthIntentClass() {
		return CreateLoginActivity.class;
	}
	
}
