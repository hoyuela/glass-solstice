package com.discover.mobile.security;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.BankServiceCallFactory;
import com.discover.mobile.NotLoggedInRoboActivity;
import com.discover.mobile.R;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.auth.bank.strong.BankStrongAuthAnswerDetails;
import com.discover.mobile.common.auth.strong.StrongAuthAnswerCall;
import com.discover.mobile.common.auth.strong.StrongAuthAnswerDetails;
import com.discover.mobile.common.callback.AsyncCallbackAdapter;
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

	private final static String TAG = EnhancedAccountSecurityActivity.class
			.getSimpleName();
	private final static String TRUE = "true"; //$NON-NLS-1$
	private final static String FALSE = "false"; //$NON-NLS-1$

	private String questionId;
	private Boolean isCard;
	EditText questionAnswerField;
	RadioGroup securityRadioGroup;
	TextView detailHelpLabel;
	TextView statusIconLabel;
	TextView questionLabel;
	TextView errorMessage;
	RelativeLayout whatsThisLayout;
	Bundle extras;
	

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.strongauth_page);
		// INPUT FIELDS

		questionAnswerField = (EditText) findViewById(R.id.account_security_question_answer_field);

		// RADIO BUTTONS AND GROUPS

		securityRadioGroup = (RadioGroup) findViewById(R.id.account_security_choice_radio_group);

		// TEXT LABELS

		detailHelpLabel = (TextView) findViewById(R.id.account_security_whats_this_detail_label);
		
		errorMessage = (TextView) findViewById(R.id.error_message_strong_auth);

		statusIconLabel = (TextView) findViewById(R.id.account_security_plus_label);

		questionLabel = (TextView) findViewById(R.id.account_security_question_placeholder_label);

		whatsThisLayout = (RelativeLayout) findViewById(R.id.account_security_whats_this_relative_layout);
		final RadioButton radioButtonOne = (RadioButton)securityRadioGroup.findViewById(R.id.account_security_choice_one_radio);
		final RadioButton radioButtonTwo = (RadioButton)securityRadioGroup.findViewById(R.id.account_security_choice_two_radio);
		
		securityRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) { 
            	RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
            	if (checkedRadioButton == radioButtonOne){
            		radioButtonOne.setTextColor(getResources().getColor(R.color.black));
            		radioButtonTwo.setTextColor(getResources().getColor(R.color.abs__primary_text_disable_only_holo_dark));
            	}else {
            		radioButtonTwo.setTextColor(getResources().getColor(R.color.black));
            		radioButtonOne.setTextColor(getResources().getColor(R.color.abs__primary_text_disable_only_holo_dark));
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
			final String question = extras
					.getString(IntentExtraKey.STRONG_AUTH_QUESTION);
			questionId = extras
					.getString(IntentExtraKey.STRONG_AUTH_QUESTION_ID);
			isCard = extras.getBoolean(IntentExtraKey.IS_CARD_ACCOUNT, true);
			questionLabel.setText(question);
		}
		if (!isCard) {
			whatsThisLayout.setVisibility(View.GONE);
		}
	}
	
	@Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        
        //Grab the updated intent
        setIntent(intent);
    }

	// Open and close the expandable help menu.
	public void expandHelpMenu(final View v) {
		if ("+".equals(statusIconLabel.getText())) { //$NON-NLS-1$
			openHelpMenu();
		} else {
			closeHelpMenu();
		}
	}

	private void openHelpMenu() {
		statusIconLabel
				.setText(getString(R.string.account_security_minus_text));
		detailHelpLabel.setMaxLines(HELP_DROPDOWN_LINE_HEIGHT);
	}

	private void closeHelpMenu() {
		statusIconLabel.setText(getString(R.string.account_security_plus_text));
		detailHelpLabel.setMaxLines(0);
	}

	public void submitSecurityInfo(final View v) {
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

	private void submitCardSecurityInfo(final ProgressDialog progress,
			int selectedIndex, String answer) {
		final AsyncCallbackAdapter<Object> callback = new AsyncCallbackAdapter<Object>() {
			@Override
			public void success(final Object value) {
				progress.dismiss();
				finishWithResultOK();
			}
		};

		final StrongAuthAnswerDetails answerDetails = new StrongAuthAnswerDetails();
		answerDetails.questionAnswer = answer;
		answerDetails.questionId = questionId;

		if (selectedIndex == 0) {
			answerDetails.bindDevice = TRUE;
		} else {
			answerDetails.bindDevice = FALSE;
		}

		StrongAuthAnswerCall strongAuthAnswer;
		try {

			strongAuthAnswer = new StrongAuthAnswerCall(this, callback,
					answerDetails);
			strongAuthAnswer.submit();

		} catch (final NoSuchAlgorithmException e) {
			Log.e(TAG,
					"Could not Base 64 encode Strong Auth response body: " + e);//$NON-NLS-1$
		}
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		finish();
	}

	private void finishWithResultOK() {
		setResult(RESULT_OK);
		finish();
	}

	@Override
	public void goBack() {
		// TODO Auto-generated method stub
		
	}
}
