package com.discover.mobile.login.register;

import roboguice.inject.InjectView;
import android.content.Intent;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.ScreenType;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.auth.InputValidator;
import com.discover.mobile.common.auth.forgot.ForgotPasswordCall;
import com.discover.mobile.common.auth.registration.AccountInformationDetails;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.NetworkServiceCall;

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
	
	private EditText genericInputField;
	
	@Override
	protected void setupCustomTextChangedListeners(){
		final InputValidator validator = new InputValidator();

		idField.setOnFocusChangeListener(new OnFocusChangeListener() {
			InputValidator validator = new InputValidator();
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				genericInputField = (EditText)v;
				
				if( !hasFocus && !validator.isUidValid( genericInputField.getText().toString() ) ) {
					showLabel(idErrorLabel);
				}
			}
		});
		
		idField.addTextChangedListener(new TextWatcher(){
			
			// FIXME this may be a bug, is this intended to override the one defined
			// at the beginning of setupCustomTextChangedListeners()?
			InputValidator validator = new InputValidator();

			@Override
			public void afterTextChanged(final Editable s) {/*Intentionally empty*/}

			@Override
			public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {/*Intentionally empty*/}

			@Override
			public void onTextChanged(final CharSequence s, final int start, final int before,
					final int count) {
				//Hide error label.
				if( validator.isPassValid( s.toString() ) ) {
					idErrorLabel.setVisibility(View.GONE);
				}
			}
			
		});
		
	}
	
	@Override
	protected void navToNextScreenWithDetails(AccountInformationDetails details) {
		final Intent createLoginActivity = new Intent(this, getSuccessfulStrongAuthIntentClass());
		createLoginActivity.putExtra(ScreenType.INTENT_KEY, ScreenType.FORGOT_PASSWORD);
		createLoginActivity.putExtra(IntentExtraKey.REGISTRATION1_DETAILS, details);
		startActivity(createLoginActivity);
		finish();
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
	protected void doCustomUiSetup() {
		//Set the detail label under the title to blank.
		hideLabel(accountIdentifierFieldRestrictionsLabel);
			
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
