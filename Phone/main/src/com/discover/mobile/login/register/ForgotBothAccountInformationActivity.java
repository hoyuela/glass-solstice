package com.discover.mobile.login.register;

import com.discover.mobile.R;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.auth.InputValidator;
import com.discover.mobile.common.auth.registration.AccountInformationCall;
import com.discover.mobile.common.auth.registration.AccountInformationDetails;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.NetworkServiceCall;

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
	protected Class<?> getSuccessfulStrongAuthIntentClass() {
		return CreateLoginActivity.class;
	}
	
}
