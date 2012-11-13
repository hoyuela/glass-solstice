package com.discover.mobile.login.register;

import android.text.InputFilter;
import android.text.InputType;
import android.view.View;

import com.discover.mobile.R;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.auth.InputValidator;
import com.discover.mobile.common.auth.registration.AccountInformationDetails;
import com.discover.mobile.common.forgotpassword.ForgotPasswordCall;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.response.AsyncCallback;
import com.discover.mobile.forgotuidpassword.EnterNewPasswordActivity;

public class ForgotPasswordAccountInformationActivity extends AbstractAccountInformationActivity {
	
	public ForgotPasswordAccountInformationActivity() {
		super(AnalyticsPage.FORGOT_PASSWORD_STEP1);
	}

	@Override
	protected int getActivityTitleLabelResourceId() {
		return R.string.forgot_password_text;
	}
	
	@Override
	protected void doCustomUiSetup() {
		//Set the detail label under the title to blank.
		accountIdentifierFieldRestrictionsLabel.setVisibility(View.GONE);
			
		//Set title label of the input field
		accountIdentifierFieldLabel.setText(getString(R.string.forgot_password_field_title_text));
		
		//Change the input type for the edit text to be valid for
		//user credentials and not the default card number stuff
		accountIdentifierField.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		
		//Change the max input length to 32 characters.
		final InputFilter[] filterArray = new InputFilter[1];
		filterArray[0] = new InputFilter.LengthFilter(32);
		accountIdentifierField.setFilters(filterArray);
	}
	
	@Override
	protected void addCustomFieldToDetails(final AccountInformationDetails details, final String value) {
		details.userId = value;
	}
	
	@Override
	protected boolean areDetailsValid(final InputValidator validator) {
		return validator.wasForgotPasswordInfoComplete();
	}
	
	@Override
	protected NetworkServiceCall<?> createServiceCall(final AsyncCallback<Object> callback,
			final AccountInformationDetails details) {
		
		return new ForgotPasswordCall(this, callback, details);
	}
	
	@Override
	protected Class<?> getSuccessfulStrongAuthIntentClass() {
		return EnterNewPasswordActivity.class;
	}
	
}
