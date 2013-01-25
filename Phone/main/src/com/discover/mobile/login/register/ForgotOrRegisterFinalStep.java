package com.discover.mobile.login.register;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.discover.mobile.NotLoggedInRoboActivity;
import com.discover.mobile.R;
import com.discover.mobile.common.CommonMethods;
import com.discover.mobile.common.CurrentSessionDetails;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.ScreenType;
import com.discover.mobile.common.auth.AccountDetails;
import com.discover.mobile.common.auth.AuthenticateCall;
import com.discover.mobile.common.auth.registration.RegistrationConfirmationDetails;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.GenericAsyncCallback;
import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.common.push.registration.GetPushRegistrationStatus;
import com.discover.mobile.common.push.registration.PushRegistrationStatusDetail;
import com.discover.mobile.login.LockOutUserActivity;
import com.discover.mobile.login.LoginActivity;
import com.discover.mobile.navigation.NavigationRootActivity;
import com.discover.mobile.push.register.PushRegistrationStatusErrorHandler;
import com.discover.mobile.push.register.PushRegistrationStatusSuccessListener;
import com.xtify.sdk.api.XtifySDK;

/**
 * This class provides common server calls to the group of classes during registration or
 * forgot credentials that are responsible for logging the user in during the final step.
 * 
 * @author scottseward
 *
 */
public class ForgotOrRegisterFinalStep extends NotLoggedInRoboActivity {
	/**
	 * The details object that the server will return upon successful flow through
	 * forgot or register.
	 */
	protected RegistrationConfirmationDetails confirmationDetails;
	protected final Activity currentContext = this;
	protected boolean isForgot = false;

	/**
	 * This method submits the users information to the Card server for verification.
	 * 
	 * The AsyncCallback handles the success and failure of the call and is
	 * responsible for handling and presenting error messages to the user.
	 * 
	 */
	protected void retrieveAccountDetailsFromServer() {
		final AsyncCallback<AccountDetails> callback = GenericAsyncCallback
				.<AccountDetails> builder(this)
				.showProgressDialog("Discover", "Loading...", true)
				.withSuccessListener(new SuccessListener<AccountDetails>() {

					@Override
					public CallbackPriority getCallbackPriority() {
						return CallbackPriority.MIDDLE;
					}

					@Override
					public void success(final AccountDetails value) {
						// Set logged in to be able to save user name in
						// persistent storage
						Globals.setLoggedIn(true);

						// Update current account based on user logged

						CurrentSessionDetails.getCurrentSessionDetails()
						.setAccountDetails(value);

						getXtifyRegistrationStatus();

					}
				})
				.build();

		new AuthenticateCall(this, callback).submit();
	}

	/**
	 * Do a GET request to the server to check to see if this vendor id is
	 * registered to this user.
	 * 
	 * @author jthornton
	 */
	protected void getXtifyRegistrationStatus(){
		if(XtifySDK.getXidKey(this) != null){
			final AsyncCallback<PushRegistrationStatusDetail> callback = 
					GenericAsyncCallback.<PushRegistrationStatusDetail>builder(this)
					.showProgressDialog(getResources().getString(R.string.push_progress_get_title), 
							getResources().getString(R.string.push_progress_registration_loading), 
							true)
							.withSuccessListener(new PushConfirmationSuccessListener())
							.withErrorResponseHandler(new PushRegistrationStatusErrorHandler(new LoginActivity()))
							.finishCurrentActivityOnSuccess(this)
							.build();

			new GetPushRegistrationStatus(this, callback).submit();
		}else{
			//If the device does not have an XidKey, navigate to the next screen anyway.
			navigateToConfirmationScreenWithResponseData(confirmationDetails);
			finish();
		}

	}

	/**
	 * The original success listener needed to be extended to support navigating to another screen
	 * on success. This specific class handles navigating to the home screen after a user completes
	 * registration and the push notification status is retrieved.
	 * @author scottseward
	 *
	 */
	private class PushConfirmationSuccessListener extends PushRegistrationStatusSuccessListener implements SuccessListener<PushRegistrationStatusDetail>{

		/**
		 * Constructor that takes in a context so that it can manipulate the flow of the app.
		 */
		public PushConfirmationSuccessListener(){}

		/**
		 * Set the priority level of the success handler
		 * @return CallbackPriority - the priority of the callback
		 */
		@Override
		public CallbackPriority getCallbackPriority() {
			return CallbackPriority.LAST;
		}

		/**
		 * Send the app on the correct path when the call is successful
		 * @param value - the returning push registration detail from the server
		 */
		@Override
		public void success(final PushRegistrationStatusDetail value) {
			super.success(value);
			navigateToConfirmationScreenWithResponseData(confirmationDetails);
		}
	}


	/**
	 * If the server call succeeds then we navigate the user to the account home page with a confirmation
	 * dialog presented.
	 * @param responseData
	 */
	protected void navigateToConfirmationScreenWithResponseData(final RegistrationConfirmationDetails responseData) {
		final Intent confirmationAndLoginScreen = new Intent(this, NavigationRootActivity.class);
		confirmationAndLoginScreen.putExtra(IntentExtraKey.UID, responseData.userId);
		confirmationAndLoginScreen.putExtra(IntentExtraKey.EMAIL, responseData.email);
		confirmationAndLoginScreen.putExtra(IntentExtraKey.ACCOUNT_LAST4, responseData.acctLast4);
		
		if(isForgot){
			confirmationAndLoginScreen.putExtra(IntentExtraKey.SCREEN_TYPE, IntentExtraKey.SCREEN_FORGOT_PASS);
		} else{
			confirmationAndLoginScreen.putExtra(IntentExtraKey.SCREEN_TYPE, IntentExtraKey.SCREEN_REGISTRATION);
		}
		this.startActivity(confirmationAndLoginScreen);
		finish();
	}
	
	/**
	 * Make the help number at the bottom of the screen clickable and when clicked, dial its number.
	 */
	protected void setupHelpNumber() {
		final TextView helpText = (TextView)findViewById(R.id.help_number_label);
		helpText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CommonMethods.dialNumber(helpText.getText().toString(), currentContext);
			}
		});
	}

	/**
	 * Sends a user to a modal 'lockout' screen. This terminates the registration process.
	 * @param screenType
	 */
	protected void sendToErrorPage(final ScreenType screenType) {
		final Intent maintenancePageIntent = new Intent(this, LockOutUserActivity.class);
		screenType.addExtraToIntent(maintenancePageIntent);
		startActivity(maintenancePageIntent);
		finish();
	}
	
	@Override
	public TextView getErrorLabel() {
		return null;
	}

	@Override
	public List<EditText> getInputFields() {
		return null;
	}
	
	/**
	 * Close this activity and start the forgot credentials activity.
	 * @param v
	 */
	public void cancel(final View v) {
		final Intent forgotCredentials = new Intent(this, ForgotCredentialsActivity.class);
		startActivity(forgotCredentials);
		finish();
	}
	
	@Override
	public void goBack() {	
		cancel(null);
	}
	
	@Override 
	public void onBackPressed() {
		cancel(null);
	}

}
