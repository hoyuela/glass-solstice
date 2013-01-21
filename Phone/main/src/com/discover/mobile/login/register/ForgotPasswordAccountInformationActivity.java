package com.discover.mobile.login.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.discover.mobile.R;
import com.discover.mobile.common.CommonMethods;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.ScreenType;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.auth.forgot.ForgotPasswordCall;
import com.discover.mobile.common.auth.registration.AccountInformationDetails;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.NetworkServiceCall;
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
public class ForgotPasswordAccountInformationActivity extends AbstractAccountInformationActivity {
	
	/**
	 * Setup the main input field to accept a username instead of an account number.
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		accountIdentifierField.setFieldUsername();
		
	}
	
	public ForgotPasswordAccountInformationActivity() {
		super(AnalyticsPage.FORGOT_PASSWORD_STEP1);
	} 
	/**
	 * Put all of the form details as a serializable object extra and pass it to the next activity
	 * which will append more info onto that object.
	 */
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
	
	/**
	 * goBack will navigate the user back to the forgot selection activity. This is the screen that lets
	 * the user choose between forgotten password/user id/ or both. We also finish this activity so that
	 * they cannot navigate back to this screen.
	 */
	@Override
	public void goBack(final View v) {
		Intent forgotCredentials = new Intent(this, ForgotCredentialsActivity.class);
		startActivity(forgotCredentials);
		finish();
	}
	
	/**
	 * Hide the text label that contains information that pertains to account numbers.
	 */
	@Override
	protected void doCustomUiSetup() {
		CommonMethods.setViewGone(accountIdentifierFieldRestrictionsLabel);
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

	@Override
	public void goBack() {
		Intent forgotCredentials = new Intent(this, ForgotCredentialsActivity.class);
		startActivity(forgotCredentials);
		finish();		
	}
	
	@Override
	protected void setHeaderProgressText() {
			HeaderProgressIndicator headerProgressBar = (HeaderProgressIndicator)findViewById(R.id.header);
			headerProgressBar.setTitle(R.string.enter_info, R.string.create_password, R.string.confirm);
	}
	
//	AJ AND SCOTT TO FIX 
	
//	@Override
//	protected void setupCustomTextChangedListeners(){
//		final InputValidator validator = new InputValidator();
//
//		idField.setOnFocusChangeListener(new OnFocusChangeListener() {
//			InputValidator validator = new InputValidator();
//			
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				
//				if( !hasFocus && !validator.isUidValid( idField.getText().toString() ) ) {
//					showLabel(idErrorLabel);
//				}
//			}
//		});
//		
//		idField.addTextChangedListener(new TextWatcher(){
//			
//			// FIXME this may be a bug, is this intended to override the one defined
//			// at the beginning of setupCustomTextChangedListeners()?
//			InputValidator validator = new InputValidator();
//
//			@Override
//			public void afterTextChanged(final Editable s) {/*Intentionally empty*/}
//
//			@Override
//			public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {/*Intentionally empty*/}
//
//			@Override
//			public void onTextChanged(final CharSequence s, final int start, final int before,
//					final int count) {
//				//Hide error label.
//				if( validator.isPassValid( s.toString() ) ) {
//					idErrorLabel.setVisibility(View.GONE);
//				}
//			}
//			HeaderProgressIndicator progress = (HeaderProgressIndicator) findViewById(R.id.header);
//    		progress.initChangePasswordHeader(1);
//			
//		});
//		
//	}
}
