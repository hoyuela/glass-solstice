package com.discover.mobile.card.login.register;

import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.common.base.Strings;

import com.discover.mobile.common.DiscoverApplication;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.GenericAsyncCallback;
import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.common.error.BaseExceptionFailureHandler;
import com.discover.mobile.common.error.ErrorHandler;

import com.discover.mobile.card.error.CardErrHandler;
import com.discover.mobile.card.error.CardErrorHandlerUi;

import com.discover.mobile.common.facade.FacadeFactory;
import com.discover.mobile.common.nav.HeaderProgressIndicator;
import com.discover.mobile.common.net.NetworkServiceCall;

import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.error.CardErrorBean;
import com.discover.mobile.card.common.net.error.CardErrorResponseHandler;
import com.discover.mobile.card.common.net.error.CardErrorUIWrapper;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.common.net.utility.NetworkUtility;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;
import com.discover.mobile.card.common.ui.CardNotLoggedInCommonActivity;
import com.discover.mobile.card.common.ui.modals.ModalAlertWithOneButton;
import com.discover.mobile.card.common.uiwidget.NonEmptyEditText;
import com.discover.mobile.card.common.uiwidget.UsernameOrAccountNumberEditText;
import com.discover.mobile.card.common.utils.Utils;

import com.discover.mobile.card.CardSessionContext;
import com.discover.mobile.card.R;
import com.discover.mobile.card.error.CardBaseErrorResponseHandler;
import com.discover.mobile.card.navigation.CardNavigationRootActivity;
import com.discover.mobile.card.push.register.PushRegistrationStatusErrorHandler;
import com.discover.mobile.card.push.register.PushRegistrationStatusSuccessListener;
import com.discover.mobile.card.services.auth.AccountDetails;
import com.discover.mobile.card.services.auth.AuthenticateCall;
import com.discover.mobile.card.services.auth.registration.RegistrationConfirmationDetails;
import com.discover.mobile.card.services.push.registration.GetPushRegistrationStatus;
import com.discover.mobile.card.services.push.registration.PushRegistrationStatusDetail;

import com.xtify.sdk.api.XtifySDK;


/**
 * This class handles the forgot user ID flow.
 * If a user successfully completes this page they will be logged into the application and presented with
 * a dialog that shows them their user ID, email, and last 4 digits of their account number.
 * @author scottseward
 *
 */
public class ForgotUserIdActivity extends CardNotLoggedInCommonActivity implements CardEventListener  {

	private static final String TAG = ForgotUserIdActivity.class.getSimpleName();

	private static final String MAIN_ERROR_LABEL_TEXT_KEY = "a";
	private static final String SHOULD_UPDATE_PASS_APPEARANCE = "b";
	private static final String SHOULD_UPDATE_ACCT_NBR_APPEARANCE = "c";
	private static final String MAIN_ERROR_LABEL_VISIBILITY_KEY = "d";
	private static final String PASS_FIELD_TEXT_KEY = "e";
	private static final String CARD_FIELD_TEXT_KEY = "f";

	private static final String MODAL_IS_SHOWING_KEY = "n";
	private static final String MODAL_BODY_KEY = "o";
	private static final String MODAL_TITLE_KEY = "p";
	private static final String MODAL_CLOSES_ACTIVITY_KEY = "q";

	private int modalTitleText;
	private int modalBodyText;
	private boolean modalClosesActivity = false;

	private RegistrationConfirmationDetails confirmationDetails;

	//BUTTONS
	private Button submitButton;

	//ERROR LABELS
	private TextView mainErrLabel;
	private TextView idErrLabel;
	private TextView passErrLabel;

	//TEXT LABELS
	private TextView cancelLabel;
	private TextView helpNumber;

	//INPUT FIELDS
	private UsernameOrAccountNumberEditText cardNumField;
	private NonEmptyEditText passField;
	private DiscoverApplication globalCache;

	//SCROLL VIEW
	private ScrollView mainScrollView;
	
	private int errorCode = 0x0;

	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_forgot_id);

		/*final HeaderProgressIndicator progress = (HeaderProgressIndicator) findViewById(R.id.header);
		progress.initChangePasswordHeader(0);
		progress.hideStepTwo();*/

		loadAllViews();
		setupInputFields();
		globalCache=(DiscoverApplication)getApplicationContext();

		TrackingHelper.trackPageView(AnalyticsPage.FORGOT_UID);

		setOnClickActions();
		attachErrorLabels();

		restoreState(savedInstanceState);
	}

	/**
	 * Restore the state of input fields and error states if needed.
	 * @param savedInstanceState
	 */
	public void restoreState(final Bundle savedInstanceState) {
		if(savedInstanceState != null){
			mainErrLabel.setText(savedInstanceState.getString(MAIN_ERROR_LABEL_TEXT_KEY));
			mainErrLabel.setVisibility(savedInstanceState.getInt(MAIN_ERROR_LABEL_VISIBILITY_KEY));
			if(savedInstanceState.getBoolean(SHOULD_UPDATE_PASS_APPEARANCE))
				passField.updateAppearanceForInput();

			if(savedInstanceState.getBoolean(SHOULD_UPDATE_ACCT_NBR_APPEARANCE))
				cardNumField.updateAppearanceForInput();

			final String cardText = savedInstanceState.getString(CARD_FIELD_TEXT_KEY);
			final String passText = savedInstanceState.getString(PASS_FIELD_TEXT_KEY);

			if(!Strings.isNullOrEmpty(passText))
				passField.setText(passText);

			if(!Strings.isNullOrEmpty(cardText))
				cardNumField.setText(cardText);

			modalIsPresent = savedInstanceState.getBoolean(MODAL_IS_SHOWING_KEY);
			if(modalIsPresent){
				displayModal(savedInstanceState.getInt(MODAL_TITLE_KEY), 
						savedInstanceState.getInt(MODAL_BODY_KEY), 			
						savedInstanceState.getBoolean(MODAL_CLOSES_ACTIVITY_KEY));
			}
		}
	}

	/**
	 * Save the state of the error label on the screen so that upon rotation change, we can 
	 * restore them.
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState) {
		outState.putString(MAIN_ERROR_LABEL_TEXT_KEY, mainErrLabel.getText().toString());
		outState.putInt(MAIN_ERROR_LABEL_VISIBILITY_KEY, mainErrLabel.getVisibility());

		if(passErrLabel.getVisibility() != View.GONE)
			outState.putBoolean(SHOULD_UPDATE_PASS_APPEARANCE, true);

		if(idErrLabel.getVisibility() != View.GONE)
			outState.putBoolean(SHOULD_UPDATE_ACCT_NBR_APPEARANCE, true);	

		outState.putString(PASS_FIELD_TEXT_KEY, passField.getText().toString());
		outState.putString(CARD_FIELD_TEXT_KEY, cardNumField.getText().toString());

		outState.putBoolean(MODAL_IS_SHOWING_KEY, modalIsPresent);
		outState.putInt(MODAL_TITLE_KEY, modalTitleText);
		outState.putInt(MODAL_BODY_KEY, modalBodyText);
		outState.putBoolean(MODAL_CLOSES_ACTIVITY_KEY, modalClosesActivity);

		super.onSaveInstanceState(outState);
	}


	/**
	 * Get the views that we need from the layout and assign them to local references.
	 */
	private void loadAllViews() {
		submitButton = (Button)findViewById(R.id.forgot_id_submit_button);

		mainErrLabel = (TextView)findViewById(R.id.forgot_id_submission_error_label);
		idErrLabel = (TextView)findViewById(R.id.forgot_id_id_error_label);
		passErrLabel = (TextView)findViewById(R.id.forgot_id_pass_error_label);

		cancelLabel = (TextView)findViewById(R.id.account_info_cancel_label);
		helpNumber = (TextView)findViewById(R.id.help_number_label);

		cardNumField = (UsernameOrAccountNumberEditText)findViewById(R.id.forgot_id_id_field);
		passField = (NonEmptyEditText)findViewById(R.id.forgot_id_password_field);

		mainScrollView = (ScrollView)findViewById(R.id.main_scroll);
	}

	/**
	 * Attach error labels to input fields.
	 */
	private void attachErrorLabels(){
		passField.attachErrorLabel(passErrLabel);
		cardNumField.attachErrorLabel(idErrLabel);
	}

	/**
	 * Set the card field to accept an account number.
	 * 
	 * Set the input fields to be able to control the enabled state of the submit button.
	 * If both pass and card are valid, the continue button gets enabled.
	 * 
	 */
	private void setupInputFields() {
		cardNumField.setFieldAccountNumber();
	}

	/**
	 * Assign click listeners to buttons and phone number.
	 */
	private void setOnClickActions() {
		final String helpNumberString = helpNumber.getText().toString();
		final Context currentContext = this;
		helpNumber.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				Utils.dialNumber(helpNumberString, currentContext);
			}
		});

		submitButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(final View v){
				checkInputsAndSubmit();
			}
		});

		cancelLabel.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(final View v){
				goBack();
			}
		});
	}

	/**
	 * When the hardware back button is pressed, call goBack().
	 */
	@Override
	public void onBackPressed() {
		goBack();
	}


	/**
	 * 
	 */
	private void checkInputsAndSubmit() {
		cardNumField.updateAppearanceForInput();
		passField.updateAppearanceForInput();
		Utils.setViewGone(mainErrLabel);

		if(cardNumField.isValid() && passField.isValid())
			doForgotUserIdCall();
		else{
			mainScrollView.smoothScrollTo(0, 0);
			displayOnMainErrorLabel(getString(R.string.login_error));
		}

	}
	/**
	 * Submit the form info to the server and handle success or error.
	 */
	private void doForgotUserIdCall() {
	//	final ProgressDialog progress = ProgressDialog.show(this, "Discover", "Loading...", true);

		//Lock orientation while request is being processed
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		//if(Utils.checkNetworkConnection(this))
		{
			
			//new ForgotUserIdPassword().execute();
			String [] data = new String[2];
			data[0] = cardNumField.getText().toString().replace(" ", "");
			data[1] = passField.getText().toString();
			
			//  Cts:Commented code was to check global cache functionality. 
			/*
			if(globalCache.getData() != null)
			{
				RegistrationConfirmationDetails cachedData=(RegistrationConfirmationDetails)globalCache.getData().get(data[0]);
				if(cachedData!=null)
				{
					getDataFromAsync(cachedData);
				}
				else
				*/
				{
					//new ForgotUserIDAsyncTask(this).execute(data);
					callForgotUserID(data);
				}
			//}
			
		}
	}

	/**
	 * This method calls the genralised AsyncTask class WsAsyncTask passing in the DataHolder seralizable class which will be used byJackson to convert Json into PoJO objects
	 * @param data username and password passed as a string array.
	 */
	private void callForgotUserID(String[] data)
	{
		WSRequest request = new WSRequest();
		final String authString = NetworkUtility.getAuthorizationString(data[0],data[1]);
		
		// Setting the headers available for the service
		HashMap<String,String> headers = request.getHeaderValues();
		headers.put("Authorization", authString);
		headers.put("X-Override-UID", "true");

		String url = NetworkUtility.getWebServiceUrl(this, R.string.forgotUserID_url) ;
		
		request.setUrl(url);
		request.setHeaderValues(headers);
	    
		WSAsyncCallTask serviceCall = new WSAsyncCallTask(this, new RegistrationConfirmationDetails(), "Discover", "Authenticating...",this);
		serviceCall.execute(request);
	}
	
	private void displayOnMainErrorLabel(final String text){
		mainErrLabel.setText(text);
		Utils.setViewVisible(mainErrLabel);
	}

	private void resetScrollPosition(){
		mainScrollView.smoothScrollTo(0, 0);
	}

	private void showOkAlertDialog(final String title, final String message) {
		new AlertDialog.Builder(this)
		.setTitle(title)
		.setMessage(message)
		.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				dialog.dismiss();
				finish();
			}
		})
		.show();
	}

	/**
	 * This method submits the users information to the Card server for verification.
	 * 
	 * The AsyncCallback handles the success and failure of the call and is
	 * responsible for handling and presenting error messages to the user.
	 * 
	 */
	private void getAccountDetails() {
		final AsyncCallback<AccountDetails> callback = GenericAsyncCallback
				.<AccountDetails> builder(this)
				.showProgressDialog("Discover", "Loading...", true)
				.withErrorResponseHandler(new CardBaseErrorResponseHandler(this))
				.withExceptionFailureHandler(new BaseExceptionFailureHandler())
			
				.withSuccessListener(new SuccessListener<AccountDetails>() {

					@Override
					public CallbackPriority getCallbackPriority() {
						return CallbackPriority.MIDDLE;
					}

					@Override
					public void success(final NetworkServiceCall<?> sender, final AccountDetails value) {
						// Set logged in to be able to save user name in
						// persistent storage
						Globals.setLoggedIn(true);

						// Update current account based on user logged

						CardSessionContext.getCurrentSessionDetails()
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
							.withErrorResponseHandler(new PushRegistrationStatusErrorHandler(FacadeFactory.getLoginFacade().getLoginActivity()))
							.withExceptionFailureHandler(new BaseExceptionFailureHandler())
							
							.finishCurrentActivityOnSuccess(this)
							.build();

			new GetPushRegistrationStatus(this, callback).submit();
		}else{
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
		public void success(final NetworkServiceCall<?> sender, final PushRegistrationStatusDetail value) {
			super.success(sender, value);
			navigateToConfirmationScreenWithResponseData(confirmationDetails);

		}
	}

	/**
	 * Start the next activity after this one is complete.
	 * @param responseData
	 */
	private void navigateToConfirmationScreenWithResponseData(final RegistrationConfirmationDetails responseData){
		final Intent confirmationScreen = new Intent(this, CardNavigationRootActivity.class);
		confirmationScreen.putExtra(IntentExtraKey.UID, responseData.userId);
		confirmationScreen.putExtra(IntentExtraKey.EMAIL, responseData.email);
		confirmationScreen.putExtra(IntentExtraKey.ACCOUNT_LAST4, responseData.acctLast4);

		confirmationScreen.putExtra(IntentExtraKey.SCREEN_TYPE, IntentExtraKey.SCREEN_FOROGT_USER);
		TrackingHelper.trackPageView(AnalyticsPage.FORGOT_BOTH_CONFIRMATION);

		this.startActivity(confirmationScreen);
	}

	private void displayModal(final int titleText, final int bodyText, final boolean finishActivityOnClose){
		modalBodyText = bodyText;
		modalTitleText = titleText;
		modalClosesActivity = finishActivityOnClose;

		showErrorModal(titleText, bodyText, finishActivityOnClose);
	}

	@Override
	public void goBack() {
		finish();
		/*final Intent forgotCredentialsActivity = new Intent(this, ForgotCredentialsActivity.class);
		startActivity(forgotCredentialsActivity);*/
		final Bundle bundle = new Bundle();
        bundle.putBoolean(IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE, false);
        bundle.putBoolean(IntentExtraKey.SESSION_EXPIRED, false);
        FacadeFactory.getLoginFacade().navToLoginWithMessage(this, bundle);
	}

	@Override
	public TextView getErrorLabel() {
		return mainErrLabel;
	}

	@Override
	public List<EditText> getInputFields() {
		return null;
	}

	@Override
	public Context getContext() {
	    // TODO Auto-generated method stub
	    return this;
	}
	/* (non-Javadoc)
	 * @see com.discover.mobile.common.NotLoggedInRoboActivity#getErrorHandler()
	 */

	
	

	public void getDataFromAsync(final RegistrationConfirmationDetails user)
	{
		CardShareDataStore.getInstance(this).addToAppCache(cardNumField.getText().toString().replace(" ", ""), user);
		confirmationDetails = user;
        getAccountDetails();
        
//		CardSessionContext.getCurrentSessionDetails().setAccountDetails(new AccountDetails());
//		navigateToConfirmationScreenWithResponseData(user);
	}

	@Override
	public void onSuccess(Object data) 
	{
			getDataFromAsync((RegistrationConfirmationDetails)data);
	}

	@Override
	public void OnError(Object data) {
		CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(this);
		cardErrorResHandler.handleCardError((CardErrorBean) data);
		
	}

   

    @Override
    public CardErrHandler getCardErrorHandler() {
        return CardErrorUIWrapper.getInstance();
    }
	
   
}


