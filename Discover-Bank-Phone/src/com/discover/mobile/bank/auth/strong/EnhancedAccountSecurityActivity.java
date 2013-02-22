package com.discover.mobile.bank.auth.strong;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.discover.mobile.bank.BankServiceCallFactory;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.error.BankErrorHandler;
import com.discover.mobile.bank.services.auth.strong.BankStrongAuthAnswerDetails;
import com.discover.mobile.bank.services.auth.strong.BankStrongAuthDetails;
import com.discover.mobile.common.AccountType;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.NotLoggedInRoboActivity;
import com.discover.mobile.common.facade.FacadeFactory;
import com.discover.mobile.common.ui.widgets.NonEmptyEditText;
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
	private static final int HELP_DROPDOWN_LINE_HEIGHT = 10;

	private static final String TAG = EnhancedAccountSecurityActivity.class.getSimpleName();

	private String strongAuthQuestion;
	private String strongAuthQuestionId;
	/**
	 * Holds a reference to a BankStrongAuthDetails which is provide after requesting a Strong Challenge Question
	 * via an Intent in the onResume() method of this activity or via updateQuestion().
	 */
	private BankStrongAuthDetails strongAuthDetails;

	private String questionId;

	private RadioGroup securityRadioGroup;
	private TextView detailHelpLabel;
	private TextView statusIconLabel;
	private TextView questionLabel;
	private RelativeLayout whatsThisLayout;
	/**
	 * Holds reference to the button that triggers the NetworkServiceCall<> to POST the 
	 * answer in the TextView with id account_security_question_answer_field.
	 */
	private Button continueButton;

	private String inputErrorText;
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

	private static final String SERVER_ERROR_VISIBILITY ="a";
	private static final String SERVER_ERROR_TEXT = "c";
	private static final String ANSWER_ERROR_VISIBILITY = "b";
	private static final String ANSWER_ERROR_TEXT = "d";
	private static final String WHATS_THIS_STATE = "e";
	/**
	 * Minimum string length allowed to be sent as an answer to a Strong Auth Challenge Question
	 */
	private static final int MIN_ANSWER_LENGTH = 2;

	/**
	 * Callback to watch the text field for empty/non-empty entered text from user
	 */
	private final TextWatcher mTextWatcher = new TextWatcher() {

		@Override
		public void beforeTextChanged(final CharSequence s, final int start, final int before, final int after) { }

		@Override
		public void onTextChanged(final CharSequence s, final int start, final int before, final int after) {
			EnhancedAccountSecurityActivity.this.onTextChanged(s);
		}

		@Override
		public void afterTextChanged(final Editable s) {
		}
	};


	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.bank_strongauth_page);
		loadAllViews();
		setupRadioGroupListener();

		restoreState(savedInstanceState);

		//Disabling continue button only applies to Bank
		if( Globals.getCurrentAccount() == AccountType.BANK_ACCOUNT) {
			//Add text change listener to determine when the user has entered text
			//to enable/disable continue button
			questionAnswerField.addTextChangedListener(mTextWatcher);

			//Disable continue button by default
			continueButton.setEnabled(false);
		}
	}

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
			final String serverErrorText = savedInstanceState.getString(SERVER_ERROR_TEXT);
			final int serverErrorVisibility = savedInstanceState.getInt(SERVER_ERROR_VISIBILITY);

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
		continueButton = (Button)findViewById(R.id.account_security_continue_button);
		questionAnswerField.attachErrorLabel(errorMessage);
	}

	private void setupRadioGroupListener() {
		securityRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			final int subCopyColor = getResources().getColor(R.color.sub_copy);
			final int fieldCopyColor = getResources().getColor(R.color.field_copy);

			@Override
			public void onCheckedChanged(final RadioGroup group, final int checkedId) { 

				final RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
				if (checkedRadioButton.equals(radioButtonOne) ){
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

		final Bundle extras = getIntent().getExtras();
		if (extras != null) {
			//Determine if the activity was created from a Card or a Bank logical path

			//Check if activity was created via a Card or Bank logical path

			//Read Strong Auth Details provided via intent that started this activity
			strongAuthDetails = (BankStrongAuthDetails)extras.getSerializable(IntentExtraKey.STRONG_AUTH_DETAILS);
			//Set question for the strong auth challenge
			questionLabel.setText(strongAuthDetails.question);

		}


		whatsThisLayout.setVisibility(View.GONE);


	}

	/**
	 * Method used to update the challenge question on the page.
	 * 
	 * @param details Holds reference to Question for the StrongAuth details among other details
	 */
	public void updateQuestion(final BankStrongAuthDetails details ) {
		//Close any opened dialog
		this.closeDialog();

		//Update strong auth details reference to new object 
		strongAuthDetails = details;

		//Update UI with new question being asekd
		questionLabel.setText(details.question);

		//What's this layout is hidden for bank
		whatsThisLayout.setVisibility(View.GONE);
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

		if(errorMessage.getVisibility() == View.VISIBLE) {
			questionAnswerField.updateAppearanceForInput();
		}

	}

	/**
	 * When orientation changes, we need to restore the state of the dropdown menu.
	 * This is done by comparing the String character of the menu to known open and close
	 * characters. Then we open or close the menu based on that.
	 */
	private void restoreExpandableHelpMenu() {
		statusIconLabel.setText(dropdownSymbol);
		if("+".equals(statusIconLabel.getText().toString())) {
			closeHelpMenu();
		} else {
			openHelpMenu();
		}
	}

	@Override
	protected void onNewIntent(final Intent intent){
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


			submitBankSecurityInfo(answer, (selectedIndex==0));

		} else {
			BankErrorHandler.getInstance().showErrorsOnScreen(
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
		final List<EditText> inputFields = new ArrayList<EditText>();
		inputFields.add(questionAnswerField);
		return inputFields;
	}

	/**
	 * Submits the bank strong auth answer. 
	 * 
	 * @param answer Holds a strong with answer to the Bank Strong Auth Challenge
	 * @param bindDevice Holds a boolean indicating whether the device should be bound with the user's Bank account
	 */
	private void submitBankSecurityInfo(final String answer, final boolean bindDevice) {
		//Create an object to hold the response to the challenge
		final BankStrongAuthAnswerDetails details = new BankStrongAuthAnswerDetails(strongAuthDetails, answer, bindDevice);

		//Create a strong auth post request to send credentials to the server 
		BankServiceCallFactory.createStrongAuthRequest(details).submit();
	}

	private void startHomeFragment() {
		FacadeFactory.getCardFacade().navToHomeFragment(this);
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
	 * Event handler for text change events on the TextView with id account_security_question_answer_field.
	 * If no text is detected then the continue button at the bottom of the page is disabled, else it 
	 * is enabled.
	 * 
	 * @param newText Text that is provided by the TextView whenever a change has been detected
	 */
	private void onTextChanged(final CharSequence newText) {
		if( newText != null && newText.length() >= MIN_ANSWER_LENGTH ) {
			continueButton.setEnabled(true);
		} else {
			continueButton.setEnabled(false);
		}
	}

	/* (non-Javadoc)
	 * @see com.discover.mobile.common.NotLoggedInRoboActivity#getErrorHandler()
	 */
	@Override
	public com.discover.mobile.common.error.ErrorHandler getErrorHandler() {
		return BankErrorHandler.getInstance();
	}
}
