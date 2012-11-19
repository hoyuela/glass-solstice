package com.discover.mobile.login.register;

import roboguice.inject.InjectView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.auth.InputValidator;
import com.discover.mobile.common.auth.forgot.ForgotPasswordCall;
import com.discover.mobile.common.auth.registration.AccountInformationDetails;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.response.AsyncCallback;
import com.discover.mobile.login.forgot.EnterNewPasswordActivity;

public class ForgotPasswordAccountInformationActivity extends AbstractAccountInformationActivity {
	
	public ForgotPasswordAccountInformationActivity() {
		super(AnalyticsPage.FORGOT_PASSWORD_STEP1);
	}

	@Override
	protected int getActivityTitleLabelResourceId() {
		return R.string.forgot_password_text;
	}
	
//INPUT FIELDS
	@InjectView (R.id.account_info_main_input_field)
	EditText idField;
	
	@InjectView (R.id.account_info_ssn_input_field)
	EditText ssnField;
	
	@InjectView (R.id.account_info_dob_year_field)
	EditText yearField;
	
//ERROR LABELS
	@InjectView (R.id.account_info_card_account_number_error_label)
	TextView idErrorLabel;
	
	@InjectView (R.id.account_info_dob_year_error_label)
	TextView dobYearErrorLabel;
	
	@InjectView (R.id.account_info_ssn_error_label)
	TextView ssnErrorLabel;
	
	private EditText inputField;
	
	@Override
	protected void setupCustomTextChangedListeners(){
		
		idField.setOnFocusChangeListener(new OnFocusChangeListener() {
			InputValidator validator = new InputValidator();
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				inputField = (EditText)v;
				
				if( !hasFocus && !validator.isUidValid( inputField.getText().toString() ) ) {
					showLabel(idErrorLabel);
				}
			}
		});
		
		idField.addTextChangedListener(new TextWatcher(){
			InputValidator validator = new InputValidator();

			@Override
			public void afterTextChanged(Editable s) {
				if( validator.isPassValid( s.toString() ) ) {
					hideLabel(idErrorLabel);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {/*Intentionally empty*/}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {/*Intentionally empty*/}
			
		});
		
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
