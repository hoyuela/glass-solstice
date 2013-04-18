package com.discover.mobile.card.login.register;

import static com.discover.mobile.common.StandardErrorCodes.BAD_ACCOUNT_STATUS;
import static com.discover.mobile.common.StandardErrorCodes.SCHEDULED_MAINTENANCE;
import static com.discover.mobile.common.net.error.RegistrationErrorCodes.ID_AND_PASS_EQUAL;
import static com.discover.mobile.common.net.error.RegistrationErrorCodes.REG_AUTHENTICATION_PROBLEM;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.discover.mobile.card.R;
import com.discover.mobile.card.error.CardErrHandler;
import com.discover.mobile.card.error.CardErrorHandlerUi;
import com.discover.mobile.card.services.auth.forgot.ForgotPasswordTwoCall;
import com.discover.mobile.card.services.auth.forgot.ForgotPasswordTwoDetails;
import com.discover.mobile.card.services.auth.registration.AccountInformationDetails;
import com.discover.mobile.card.services.auth.registration.RegistrationConfirmationDetails;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.callback.AsyncCallbackAdapter;
import com.discover.mobile.common.error.BaseExceptionFailureHandler;
import com.discover.mobile.common.nav.HeaderProgressIndicator;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.discover.mobile.common.net.json.JsonMessageErrorResponse;

import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.error.CardErrorBean;
import com.discover.mobile.card.common.net.error.CardErrorResponseHandler;
import com.discover.mobile.card.common.net.error.CardErrorUIWrapper;
import com.discover.mobile.card.common.net.json.JacksonObjectMapperHolder;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.common.net.utility.NetworkUtility;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;
import com.discover.mobile.card.common.uiwidget.ConfirmationEditText;
import com.discover.mobile.common.utils.CommonUtils;
/**
 * EnterNewPasswordActivit - this activity inherits from AbstractAccountInformationActivity
 * @author scottseward
 *
 */
public class EnterNewPasswordActivity extends ForgotOrRegisterFinalStep implements CardErrorHandlerUi{

	private static final String TAG = EnterNewPasswordActivity.class.getSimpleName();

	private ForgotPasswordTwoDetails passTwoDetails;

	//TEXT LABELS
	private TextView mainErrorMessageLabel;
	private TextView errorMessageLabel;
	private TextView errorLabelOne;	
	private TextView errorLabelTwo;

	//INPUT FIELDS
	private CredentialStrengthEditText passOneField;
	private ConfirmationEditText passTwoField;

	//SCROLL VIEW
	private ScrollView mainScrollView;

	private static final String UPDATE_PASS_ONE_STATE = "a";

	private static final String MAIN_ERROR_STRING = "b";
	private static final String MAIN_ERROR_VISIBILITY = "c";

	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_create_password);
		loadAllViews();
		getPreviousScreenType();
		setupInputFields();
		mergeAccountDetails();
		setupProgressHeader();
		setupHelpNumber();
		restoreState(savedInstanceState);
	}

	/**
	 * Get a passed boolean to see if the screen that launched this activity was a forgot step.
	 * Default to false if it was not provided.
	 */
	protected void getPreviousScreenType() {
		isForgotFlow = getIntent().getBooleanExtra(IntentExtraKey.SCREEN_FORGOT_BOTH, false);
		isForgotPassword = getIntent().getBooleanExtra(IntentExtraKey.SCREEN_FORGOT_PASS, false);
	}

	/**
	 * Restore the state of the input fields on the screen upon orientation change.
	 * @param savedInstanceState
	 */
	private void restoreState(final Bundle savedInstanceState) {
		if(savedInstanceState != null) {
			if(!savedInstanceState.getBoolean(UPDATE_PASS_ONE_STATE))
				passOneField.updateAppearanceForInput();

			mainErrorMessageLabel.setVisibility(savedInstanceState.getInt(MAIN_ERROR_VISIBILITY));
			mainErrorMessageLabel.setText(savedInstanceState.getString(MAIN_ERROR_STRING));		

		}
	}

	@Override
	public void onSaveInstanceState(final Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putBoolean(UPDATE_PASS_ONE_STATE, passOneField.isInDefaultState);

		outState.putString(MAIN_ERROR_STRING, mainErrorMessageLabel.getText().toString());
		outState.putInt(MAIN_ERROR_VISIBILITY, mainErrorMessageLabel.getVisibility());
	}

	private void setupProgressHeader() {
		final HeaderProgressIndicator progress = (HeaderProgressIndicator) findViewById(R.id.header);
		progress.initChangePasswordHeader(1);
	}

	/**
	 * Get all of the view elements from the layout and assign them to local variables that will be 
	 * used to access them.
	 */
	private void loadAllViews() {
		passOneField = (CredentialStrengthEditText)findViewById(R.id.account_info_two_pass_field);
		passTwoField = (ConfirmationEditText)findViewById(R.id.account_info_two_pass_confirm_field);

		errorLabelTwo = (TextView)findViewById(R.id.enter_new_pass_error_two_label);
		errorLabelOne = (TextView)findViewById(R.id.enter_new_pass_error_one_label);
		errorMessageLabel = (TextView)findViewById(R.id.account_info_error_label);
		mainErrorMessageLabel = (TextView)findViewById(R.id.account_info_main_error_label);

		mainScrollView = (ScrollView)findViewById(R.id.main_scroll_view);

	}

	/**
	 * Setup input fields, attach error labels and set the type of input that the fields will receive.
	 */
	private void setupInputFields() {
		passOneField.setCredentialType(CredentialStrengthEditText.PASSWORD);
		passTwoField.attachEditTextToMatch(passOneField);
		passOneField.attachErrorLabel(errorLabelOne);
		passTwoField.attachErrorLabel(errorLabelTwo);
	}

	/**
	 * Take the details from the first page and merge them into a POJO that will be sent to the server
	 * from this page.
	 */
	private void mergeAccountDetails() {
		final Bundle extras = getIntent().getExtras();
		if(extras != null) {
			passTwoDetails = new ForgotPasswordTwoDetails();
			final AccountInformationDetails passOneDetails = 
					(AccountInformationDetails) getIntent().getSerializableExtra(IntentExtraKey.REGISTRATION1_DETAILS);
			if(passOneDetails != null){
				passTwoDetails.userId = passOneDetails.userId;
				passTwoDetails.dateOfBirthDay = passOneDetails.dateOfBirthDay;
				passTwoDetails.dateOfBirthMonth = passOneDetails.dateOfBirthMonth;
				passTwoDetails.dateOfBirthYear = passOneDetails.dateOfBirthYear;
				passTwoDetails.expirationMonth = passOneDetails.expirationMonth;
				passTwoDetails.expirationYear = passOneDetails.expirationYear;
				passTwoDetails.socialSecurityNumber = passOneDetails.socialSecurityNumber;
			}
		}

	}

	/**
	 * Take the information provided by the user and send it to the server for serverside validation.
	 */
	private void submitFormInfo() {
	/*	final ProgressDialog progress = ProgressDialog.show(this, "Discover", "Loading...", true);

		//Lock orientation while request is being processed
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

		final AsyncCallbackAdapter<RegistrationConfirmationDetails> callback = 
				new AsyncCallbackAdapter<RegistrationConfirmationDetails>() {

			@Override
			public void complete(final NetworkServiceCall<?> sender, final Object result) {
				//Unlock orientation after request has been processed
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
			}

			@Override
			public void success(final NetworkServiceCall<?> sender, final RegistrationConfirmationDetails responseData) {
				progress.dismiss();
				confirmationDetails = responseData;
				retrieveAccountDetailsFromServer();
			}

			@Override
			public boolean handleErrorResponse(final NetworkServiceCall<?> sender, final ErrorResponse<?> errorResponse) {
				progress.dismiss();

				switch (errorResponse.getHttpStatusCode()) {	
				default:
					Log.e(TAG, "RegistrationCallOne.errorResponse(ErrorResponse): " + errorResponse.toString());
					CommonUtils.showLabelWithStringResource(errorMessageLabel,R.string.unkown_error_text, currentContext);
					return true;
				}

			}

			@Override
			public void failure(final NetworkServiceCall<?> sender, final Throwable executionException) {
				//Catch all exception handler
				final BaseExceptionFailureHandler exceptionHandler = new BaseExceptionFailureHandler();
				exceptionHandler.handleFailure(sender, executionException);
			}

			@Override
			public boolean handleMessageErrorResponse(final NetworkServiceCall<?> sender, final JsonMessageErrorResponse messageErrorResponse) {
				progress.dismiss();
				switch(messageErrorResponse.getMessageStatusCode()){

				case REG_AUTHENTICATION_PROBLEM: 
					CommonUtils.showLabelWithStringResource(errorMessageLabel, R.string.account_info_bad_input_error_text, currentContext);
					return true;

				case BAD_ACCOUNT_STATUS:
					CommonUtils.showLabelWithStringResource(errorMessageLabel,R.string.login_attempt_warning, currentContext);
					return true;

				case ID_AND_PASS_EQUAL:
					CommonUtils.showLabelWithStringResource(mainErrorMessageLabel, R.string.account_info_bad_input_error_text, currentContext);
					CommonUtils.showLabelWithStringResource(errorMessageLabel, R.string.account_info_two_id_matches_pass_error_text, currentContext);
					return true;

				case SCHEDULED_MAINTENANCE:
					showErrorModal(R.string.could_not_complete_request, R.string.unscheduled_maintenance, false);
					return true;

				default:
					Log.e(TAG, "UNHANDLED ERROR " + messageErrorResponse.toString());
					return false;

				}
			}
		};

		final ForgotPasswordTwoCall forgotPassTwoCall = 
				new ForgotPasswordTwoCall(this, callback, passTwoDetails);
		forgotPassTwoCall.submit();
	    */
	    CardEventListener cardEventListener = new CardEventListener() {
            
            @Override
            public void onSuccess(Object data) {
                // TODO Auto-generated method stub
                
                RegistrationConfirmationDetails registrationConfirmationDetails =  (RegistrationConfirmationDetails)data;
                retrieveAccountDetailsFromServer(registrationConfirmationDetails);
            }
            
            @Override
            public void OnError(Object data) {
                // TODO Auto-generated method stub
                CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                        (CardErrorHandlerUi) EnterNewPasswordActivity.this);
                cardErrorResHandler.handleCardError((CardErrorBean) data);
                
            }
        };
	       WSRequest request = new WSRequest();

	        // Setting the headers available for the service
	        HashMap<String, String> headers = request.getHeaderValues();
	        headers.put("X-SEC-Token", "");
	        String url = NetworkUtility.getWebServiceUrl(this,
	                R.string.createpassword_url);

	        request.setUrl(url);
	        request.setHeaderValues(headers);
	        request.setMethodtype("POST");

	        ByteArrayOutputStream baos = new ByteArrayOutputStream();

	        try {
                JacksonObjectMapperHolder.getMapper().writeValue(baos,
                        passTwoDetails);
            } catch (JsonGenerationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JsonMappingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

	        request.setInput(baos.toByteArray());

	        WSAsyncCallTask serviceCall = new WSAsyncCallTask(this, new RegistrationConfirmationDetails(),
	                "Discover", "Loading...", cardEventListener);
	        serviceCall.execute(request);
	        
	      

	}

	
    /**
	 * If all of the information is valid on the page then submit the info to get validated
	 * by the server.
	 * @param v
	 */
	public void checkInputsThenSubmit(final View v) {
		passOneField.updateAppearanceForInput();
		passTwoField.updateAppearanceForInput();
		CommonUtils.setViewGone(mainErrorMessageLabel);

		//If the info was all valid - submit it to the service call.
		if(passOneField.isValid() && passTwoField.isValid()){
			final String passOneFieldValue = passOneField.getText().toString();

			passTwoDetails.password = passOneFieldValue;
			passTwoDetails.passwordConfirm = passTwoDetails.password;
			submitFormInfo();
		}
		else {
			mainScrollView.smoothScrollTo(0, 0);
			if(!passOneField.isValid())
				passOneField.setStrengthMeterInvalid();

			CommonUtils.showLabelWithStringResource(mainErrorMessageLabel, 
					R.string.account_info_bad_input_error_text, this);
		}

	}

    /* (non-Javadoc)
     * @see com.discover.mobile.card.error.CardErrorHandlerUi#getCardErrorHandler()
     */
    @Override
    public CardErrHandler getCardErrorHandler() {
        // TODO Auto-generated method stub
        return CardErrorUIWrapper.getInstance();
    }

}
