package com.discover.mobile.security;

import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.INCORRECT_STRONG_AUTH_ANSWER;
import static com.discover.mobile.common.auth.registration.RegistrationErrorCodes.STRONG_AUTH_STATUS_INVALID;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.discover.mobile.BankServiceCallFactory;
import com.discover.mobile.NotLoggedInRoboActivity;
import com.discover.mobile.R;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.auth.GetStrongAuthQuestionCall;
import com.discover.mobile.common.auth.bank.strong.BankStrongAuthAnswerDetails;
import com.discover.mobile.common.auth.strong.StrongAuthAnswerCall;
import com.discover.mobile.common.auth.strong.StrongAuthAnswerDetails;
import com.discover.mobile.common.auth.strong.StrongAuthDetails;
import com.discover.mobile.common.callback.AsyncCallbackAdapter;
import com.discover.mobile.common.customui.NonEmptyEditText;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.discover.mobile.common.net.json.JsonMessageErrorResponse;
import com.discover.mobile.error.BaseExceptionFailureHandler;
import com.discover.mobile.error.ErrorHandlerFactory;
import com.discover.mobile.navigation.NavigationRootActivity;
import com.google.common.base.Strings;

/**
 * Class Description of EnhancedAccountSecurity
 * 
 * EnhancedAccountSecurity Activity is the Activity that is used to handle
 * Strong Auth actions. It should always be launched through a
 * startActivityForResult(), as it is essentially a modal view and does control
 * any further navigation itself. During the use of the overall application,
 * Strong Auth may be required for a user to complete certain actions. When
 * Strong Auth is required, it will prevent the user from completing their
 * action until the Strong Auth question is answered correctly.
 * 
 * This Activity is passed its question and question ID as extras. So when the
 * activity is launched it will set its question text label to be the question
 * that was passed in the extras. When the question is answered and submitted a
 * POST request is done to send the question response to the server, if the
 * answer was correct then this Activity will finish with RESULT_OK and return
 * control to the calling Activity. If the user cannot answer the question
 * correctly or presses the hardware back button, this Activity will finish with
 * RESULT_CANCELED and return control to the calling activity. The caller will
 * decide how to handle the responses from this Activity.
 * 
 * @author scottseward
 * 
 */

//@ContentView(R.layout.strongauth_page)
public class EnhancedAccountSecurityActivity extends NotLoggedInRoboActivity {

	/**
	 * Field Description of HELP_DROPDOWN_LINE_HEIGHT The Strong Auth screen has
	 * an expandable menu that provides help to the user, this value is used to
	 * define the number of vertical lines that the menu will occupy when it is
	 * expanded. (When collapsed it is set to 0)
	 */
	private final static int HELP_DROPDOWN_LINE_HEIGHT = 10;

	private final String TAG = EnhancedAccountSecurityActivity.class.getSimpleName();
	
	private final String TRUE = "true";
	private final String FALSE = "false";
	
	private String strongAuthQuestion;
	private String strongAuthQuestionId;
	
	private String questionId;
	private Boolean isCard;
	private RadioGroup securityRadioGroup;
	private TextView detailHelpLabel;
	private TextView statusIconLabel;
	private TextView questionLabel;
	private RelativeLayout whatsThisLayout;
	private Bundle extras;
	
	private String inputErrorText;
	private String serverErrorText;
	private int serverErrorVisibility;
	private int inputErrorVisibility;
	private String dropdownSymbol;
	
	//INPUT FIELDS
	private NonEmptyEditText questionAnswerField;

	//RADIO BUTTONS
	private RadioButton radioButtonOne;
	private RadioButton radioButtonTwo;
	
	//ERROR LABELS
	private TextView serverErrorLabel;
	private TextView errorMessage;
	
	//SCROLL VIEW
	private ScrollView mainScrollView;
	
	private int activityResult = RESULT_CANCELED;
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.strongauth_page);
		loadAllViews();
		setupRadioGroupListener();

		restoreState(savedInstanceState);
	}
	
	private final String SERVER_ERROR_VISIBILITY ="a";
	private final String SERVER_ERROR_TEXT = "c";
	private final String ANSWER_ERROR_VISIBILITY = "b";
	private final String ANSWER_ERROR_TEXT = "d";
	private final String WHATS_THIS_STATE = "e";
	
	@Override
	public void onSaveInstanceState(final Bundle outState) {
		outState.putInt(SERVER_ERROR_VISIBILITY, serverErrorLabel.getVisibility());
		outState.putString(SERVER_ERROR_TEXT, serverErrorLabel.getText().toString());
		
		outState.putInt(ANSWER_ERROR_VISIBILITY, errorMessage.getVisibility());
		outState.putString(ANSWER_ERROR_TEXT, errorMessage.getText().toString());
		
		outState.putString(WHATS_THIS_STATE, statusIconLabel.getText().toString());
		super.onSaveInstanceState(outState);
	}
	
	private void restoreState(final Bundle savedInstanceState) {
		if(savedInstanceState != null){
			serverErrorText = savedInstanceState.getString(SERVER_ERROR_TEXT);
			serverErrorVisibility = savedInstanceState.getInt(SERVER_ERROR_VISIBILITY);
			
			inputErrorVisibility = savedInstanceState.getInt(ANSWER_ERROR_VISIBILITY);
			inputErrorText = savedInstanceState.getString(ANSWER_ERROR_TEXT);
			
			dropdownSymbol = savedInstanceState.getString(WHATS_THIS_STATE);
			
			serverErrorLabel.setText(serverErrorText);
			serverErrorLabel.setVisibility(serverErrorVisibility);
			restoreInputField();
			
			restoreExpandableHelpMenu();		
		}	
		
	}
	
	private void loadAllViews() {
		questionAnswerField = (NonEmptyEditText) findViewById(R.id.account_security_question_answer_field);
		securityRadioGroup = (RadioGroup) findViewById(R.id.account_security_choice_radio_group);
		detailHelpLabel = (TextView) findViewById(R.id.account_security_whats_this_detail_label);
		errorMessage = (TextView) findViewById(R.id.error_message_strong_auth);
		statusIconLabel = (TextView) findViewById(R.id.account_security_plus_label);
		questionLabel = (TextView) findViewById(R.id.account_security_question_placeholder_label);
		whatsThisLayout = (RelativeLayout) findViewById(R.id.account_security_whats_this_relative_layout);
		radioButtonOne = (RadioButton)securityRadioGroup.findViewById(R.id.account_security_choice_one_radio);
		radioButtonTwo = (RadioButton)securityRadioGroup.findViewById(R.id.account_security_choice_two_radio);
		serverErrorLabel = (TextView)findViewById(R.id.account_security_server_error);
		mainScrollView = (ScrollView)findViewById(R.id.scrollView1);
		questionAnswerField.attachErrorLabel(errorMessage);
	}
	
	private void setupRadioGroupListener() {
		securityRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			final int subCopyColor = getResources().getColor(R.color.sub_copy);
		    final int fieldCopyColor = getResources().getColor(R.color.field_copy);

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) { 
            	
            	RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
            	if (checkedRadioButton == radioButtonOne){
            		radioButtonOne.setTextColor(subCopyColor);
            		radioButtonTwo.setTextColor(fieldCopyColor);
            	}else {
            		radioButtonOne.setTextColor(fieldCopyColor);
            		radioButtonTwo.setTextColor(subCopyColor);
            	}
            }
		});
	}
	
	/**
	 * Moved intent logic to onResume instead of onCreate. onNewIntent will update the intent before onResume is 
	 * called. 
	 */
	@Override
	public void onResume(){
		super.onResume();

		extras = getIntent().getExtras();
		if (extras != null) {
			strongAuthQuestion = extras
					.getString(IntentExtraKey.STRONG_AUTH_QUESTION);
			strongAuthQuestionId = extras
					.getString(IntentExtraKey.STRONG_AUTH_QUESTION_ID);
			isCard = extras.getBoolean(IntentExtraKey.IS_CARD_ACCOUNT, true);
			questionLabel.setText(strongAuthQuestion);
		}

		if (!isCard) {
			whatsThisLayout.setVisibility(View.GONE);
		}
		
	}
	
	/**
	 * When the activity is finished, set the result so that the calling activity knows if strong auth exited
	 * properly or not.
	 */
	@Override
	public void finish(){
		setResult(activityResult);
		super.finish();
	}
	
	/**
	 * Restore the sate of the input field based on its error label.
	 * If the label is present, its in an error state and must be updated.
	 */
	private void restoreInputField() {
		errorMessage.setText(inputErrorText);
		errorMessage.setVisibility(inputErrorVisibility);
		 
		if(errorMessage.getVisibility() == View.VISIBLE)
			questionAnswerField.updateAppearanceForInput();

	}
	
	/**
	 * When orientation changes, we need to restore the state of the dropdown menu.
	 * This is done by comparing the String character of the menu to known open and close
	 * characters. Then we open or close the menu based on that.
	 */
	private void restoreExpandableHelpMenu() {
		statusIconLabel.setText(dropdownSymbol);
		if("+".equals(statusIconLabel.getText().toString()))
			closeHelpMenu();
		else
			openHelpMenu();
	}
	
	@Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        
        //Grab the updated intent
        setIntent(intent);
    }

	/**
	 *  Toggles the help menu based on its current state.
	 *  If the menu is closed and it is clicked, it gets opened.
	 *  If the menu is open and its gets clicked, it closes.
	 */
	public void expandHelpMenu(final View v) {
		if ("+".equals(statusIconLabel.getText().toString())) { //$NON-NLS-1$
			openHelpMenu();
		} else {
			closeHelpMenu();
		}
	}

	/**
	 * Open the help menu by changing its character from a '+' to a '-'
	 * and setting its content to be visible by changing its line height.
	 */
	private void openHelpMenu() {
		statusIconLabel.setText(getString(R.string.account_security_minus_text));
		detailHelpLabel.setMaxLines(HELP_DROPDOWN_LINE_HEIGHT);
	}

	/**
	 * Close the help menu by changing its character from a '-' to a '+'
	 * and setting its content to be invisible by changing its line height to zero.
	 */
	private void closeHelpMenu() {
		statusIconLabel.setText(getString(R.string.account_security_plus_text));
		detailHelpLabel.setMaxLines(0);
	}

	/**
	 * Check to see if the user provided an answer to the strong auth question. If they did, then 
	 * submit the info to the server for validation.
	 * If they did not, present an error message.
	 * 
	 * @param v
	 */
	public void submitSecurityInfo(final View v) {
		mainScrollView.smoothScrollTo(0, 0);
		// Store answer in a string
		final String answer = questionAnswerField.getText().toString();

		if( !Strings.isNullOrEmpty(answer) ) {
			// Find out which radio button is pressed.
			final int radioButtonId = securityRadioGroup.getCheckedRadioButtonId();
			final View selectedButton = securityRadioGroup.findViewById(radioButtonId);
			final int selectedIndex = securityRadioGroup.indexOfChild(selectedButton);
			
			if (!isCard) {
				submitBankSecurityInfo(answer);
			} else {
				final ProgressDialog progress = ProgressDialog.show(this,
						"Discover", "Loading...", true); //$NON-NLS-1$ //$NON-NLS-2$
				submitCardSecurityInfo(progress, selectedIndex, answer);
			}
		} else {
			ErrorHandlerFactory.getInstance().showErrorsOnScreen(
					this, this.getResources().getString(R.string.error_strongauth_noanswer));
		}

	}
	
	/* (non-Javadoc)
	 * @see com.discover.mobile.ErrorHandlerUi#getErrorLabel()
	 */
	@Override
	public TextView getErrorLabel() {
		return errorMessage;
	}

	/* (non-Javadoc)
	 * @see com.discover.mobile.ErrorHandlerUi#getInputFields()
	 */
	@Override
	public List<EditText> getInputFields() {
		List<EditText> inputFields = new ArrayList<EditText>();
		inputFields.add(questionAnswerField);
		return inputFields;
	}

	/**
	 * Submits the bank strong auth answer. 
	 * 
	 * @param answer
	 */
	private void submitBankSecurityInfo(String answer) {
		BankStrongAuthAnswerDetails details = new BankStrongAuthAnswerDetails();
		details.question = answer;
		details.questionId = questionId;
		
		//Create a strong auth post request to send credentials to the server 
		BankServiceCallFactory.createStrongAuthRequest(this, details).submit();
	}

	private void startHomeFragment() {
		final Intent strongAuth = new Intent(this, NavigationRootActivity.class);

		startActivityForResult(strongAuth, 0);
	}

	private void submitCardSecurityInfo(final ProgressDialog progress, int selectedIndex, String answer) {
		serverErrorLabel.setVisibility(View.GONE);
		questionAnswerField.updateAppearanceForInput();
		
		//Lock Orientation while processing request
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		
		final AsyncCallbackAdapter<StrongAuthAnswerDetails> callback = new AsyncCallbackAdapter<StrongAuthAnswerDetails>() {
			@Override
			public void success(final StrongAuthAnswerDetails value) {
				progress.dismiss();
				finishWithResultOK();
			}
			
			@Override
			public void complete(final Object result) {
				//Unlock orientation to be able to support orientation
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
			}
			
			@Override
			public void failure(final Throwable executionException, final NetworkServiceCall<StrongAuthAnswerDetails> networkServiceCall) {
				BaseExceptionFailureHandler exceptionHandler = new BaseExceptionFailureHandler();
				exceptionHandler.handleFailure(executionException, networkServiceCall);
			}
			
			/**
			 * Catch all modal popup.
			 */
			@Override
			public boolean handleErrorResponse(final ErrorResponse<?> errorResponse){
				switch(errorResponse.getHttpStatusCode()){
					default:
						showErrorModal(R.string.could_not_complete_request, R.string.internal_server_error_500, false);
						return true;
				}
			}
			
			@Override
			public boolean handleMessageErrorResponse(final JsonMessageErrorResponse value) {
				Log.d(TAG, "ERROR CODE " + value.toString());
				progress.dismiss();
				switch(value.getMessageStatusCode()){
				
					case STRONG_AUTH_STATUS_INVALID:
						showErrorModal(R.string.account_security_text, R.string.account_security_locked_out, true);
						return true;
					
					case INCORRECT_STRONG_AUTH_ANSWER:
						getStrongAuthQuestion();
						serverErrorLabel.setText(R.string.account_security_answer_doesnt_match);
						serverErrorLabel.setVisibility(View.VISIBLE);
						return true;

					default:
						return false;
				}
			}
			
		};

		final StrongAuthAnswerDetails answerDetails = new StrongAuthAnswerDetails();
		answerDetails.questionAnswer = answer;
		answerDetails.questionId = strongAuthQuestionId;

		if (selectedIndex == 0) {
			answerDetails.bindDevice = TRUE;
		} else {
			answerDetails.bindDevice = FALSE;
		}

		StrongAuthAnswerCall strongAuthAnswer;
		try {

			strongAuthAnswer = new StrongAuthAnswerCall(this, callback,	answerDetails);
			strongAuthAnswer.submit();

		} catch (final NoSuchAlgorithmException e) {
			Log.e(TAG,
					"Could not Base 64 encode Strong Auth response body: " + e);//$NON-NLS-1$
		}
	}

	/**
	 * If the back button is pressed then cancel the strong auth activity and notify the
	 * calling activity that this activity was canceled.
	 */
	@Override
	public void onBackPressed() {
		activityResult = RESULT_CANCELED;
		finish();
	}

	/**
	 * If Strong Auth finishes with success, notify the calling activity of this and close.
	 */
	private void finishWithResultOK() {
		activityResult = RESULT_OK;
		finish();
	}

	/**
	 * If the software back button is pressed, call the onBackPressed() method.
	 */
	@Override
	public void goBack() {	
		onBackPressed();
	}
	
	/**
	 * If strong auth is required, this call is made to retrieve the question and question ID for Strong Auth.
	 * On success Strong Auth is launched and the question and id are passed to the activity.
	 * It is launched for intent, so once Strong Auth is done, we come back to the launching activity
	 * and decide how to proceed.
	 */
	private void getStrongAuthQuestion() {
		final ProgressDialog progress = ProgressDialog.show(this, "Discover", "Loading...", true);
		final Activity currentActivity = this;
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		
		final AsyncCallbackAdapter<StrongAuthDetails> callback = new AsyncCallbackAdapter<StrongAuthDetails>() {
			@Override
			public void success(final StrongAuthDetails value) {
				//If we get a new question, close the loading dialog and setup the new question.
				progress.dismiss();

				strongAuthQuestionId = value.questionId;
				strongAuthQuestion = value.questionText;
				questionLabel.setText(strongAuthQuestion);
			}
			
			@Override
			public void complete(final Object result) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
			}
			
			
			@Override
			public void failure(final Throwable executionException, final NetworkServiceCall<StrongAuthDetails> networkServiceCall) {
				BaseExceptionFailureHandler exceptionHandler = new BaseExceptionFailureHandler();
				exceptionHandler.handleFailure(executionException, networkServiceCall);
			}

			@Override
			public boolean handleErrorResponse(final ErrorResponse errorResponse) {
				progress.dismiss();
				switch (errorResponse.getHttpStatusCode()) {
					//catch all for strong auth question retrival.
					default:
						showErrorModal(R.string.could_not_complete_request, R.string.internal_server_error_500, false);
						return true;
				}
				
			}
		};

		final GetStrongAuthQuestionCall strongAuthCall = new GetStrongAuthQuestionCall(this, callback);
		strongAuthCall.submit();
		
	}
	
}
