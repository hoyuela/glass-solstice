package com.discover.mobile.security;

import java.security.NoSuchAlgorithmException;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.auth.strong.StrongAuthAnswerCall;
import com.discover.mobile.common.auth.strong.StrongAuthAnswerDetails;
import com.discover.mobile.common.callback.AsyncCallbackAdapter;

/** Class Description of EnhancedAccountSecurity
 * 
 *  EnhancedAccountSecurity Activity is the Activity that is used to handle Strong Auth actions. It should always
 *  be launched through a startActivityForResult(), as it is essentially a modal view and does control any further
 *  navigation itself.
 *  During the use of the overall application, Strong Auth may be required for a user to complete certain
 *  actions. When Strong Auth is required, it will prevent the user from completing their action until the 
 *  Strong Auth question is answered correctly.
 *  
 *  This Activity is passed its question and question ID as extras. So when the activity is launched it will set
 *  its question text label to be the question that was passed in the extras. When the question is answered and 
 *  submitted a POST request is done to send the question response to the server, if the answer was correct then
 *  this Activity will finish with RESULT_OK and return control to the calling Activity. If the user cannot answer
 *  the question correctly or presses the hardware back button, this Activity will finish with RESULT_CANCELED and
 *  return control to the calling activity. The caller will decide how to handle the responses from this Activity.
 * 
 * @author scottseward
 * 
 */

@ContentView(R.layout.strongauth_page)
public class EnhancedAccountSecurityActivity extends RoboActivity {
	
	/**Field Description of HELP_DROPDOWN_LINE_HEIGHT
	 * The Strong Auth screen has an expandable menu that provides help to the user,
	 * this value is used to define the number of vertical lines that the menu will occupy when
	 * it is expanded. (When collapsed it is set to 0)
	 */
	private final static int HELP_DROPDOWN_LINE_HEIGHT = 10;

	private final static String TAG = EnhancedAccountSecurityActivity.class.getSimpleName();
	private final static String TRUE = "true"; //$NON-NLS-1$
	private final static String FALSE = "false"; //$NON-NLS-1$

	private String questionId;	

//INPUT FIELDS
	
	@InjectView(R.id.account_security_question_answer_field)
	private EditText questionAnswerField;

//RADIO BUTTONS AND GROUPS
	
	@InjectView(R.id.account_security_choice_radio_group)
	private RadioGroup securityRadioGroup;

//TEXT LABELS
	
	@InjectView (R.id.account_security_whats_this_detail_label)
	private TextView detailHelpLabel;
	
	@InjectView (R.id.account_security_plus_label)
	private TextView statusIconLabel;
	
	@InjectView(R.id.account_security_question_placeholder_label)
	private TextView questionLabel;
	
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		final Bundle extras = getIntent().getExtras();
    	if(extras != null) {
    		final String question = extras.getString(IntentExtraKey.STRONG_AUTH_QUESTION);
    		questionId = extras.getString(IntentExtraKey.STRONG_AUTH_QUESTION_ID);
    		questionLabel.setText(question);
    	}
	}
		
	//Open and close the expandable help menu.
	public void expandHelpMenu(final View v){
	    if("+".equals(statusIconLabel.getText())){	//$NON-NLS-1$
			openHelpMenu();
		}
	    else{
	    	closeHelpMenu();
	    }
	}
	
	private void openHelpMenu() {
		statusIconLabel.setText(getString(R.string.account_security_minus_text));
		detailHelpLabel.setMaxLines(HELP_DROPDOWN_LINE_HEIGHT);
	}
	
	private void closeHelpMenu() {
		statusIconLabel.setText(getString(R.string.account_security_plus_text));
    	detailHelpLabel.setMaxLines(0);
	}
			
	
	public void submitSecurityInfo(final View v){
		
		final ProgressDialog progress = ProgressDialog.show(this, "Discover", "Loading...", true); //$NON-NLS-1$ //$NON-NLS-2$
		
		final AsyncCallbackAdapter<Object> callback = new AsyncCallbackAdapter<Object>() {
			@Override
			public void success(final Object value) {
				progress.dismiss();
				finishWithResultOK();
			}
		};
		
		final StrongAuthAnswerDetails answerDetails = new StrongAuthAnswerDetails();
		answerDetails.questionAnswer = questionAnswerField.getText().toString();
		answerDetails.questionId = questionId;
		
		//Find out which radio button is pressed.
		final int radioButtonId = securityRadioGroup.getCheckedRadioButtonId();
		final View selectedButton = securityRadioGroup.findViewById(radioButtonId);
		final int selectedIndex = securityRadioGroup.indexOfChild(selectedButton);
		
		if(selectedIndex == 0) {
			answerDetails.bindDevice = TRUE;
		}
		else {
			answerDetails.bindDevice = FALSE;
		}
		
		StrongAuthAnswerCall strongAuthAnswer;
		try {
			
			strongAuthAnswer = new StrongAuthAnswerCall(this, callback, answerDetails);
			strongAuthAnswer.submit();
			
		} catch (final NoSuchAlgorithmException e) {
			Log.e(TAG, "Could not Base 64 encode Strong Auth response body: " + e);//$NON-NLS-1$
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
	
}
