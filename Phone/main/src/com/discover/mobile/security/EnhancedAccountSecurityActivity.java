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

@ContentView(R.layout.strongauth_page)
public class EnhancedAccountSecurityActivity extends RoboActivity {
	
	private final static String TAG = EnhancedAccountSecurityActivity.class.getSimpleName();
	private final static String TRUE = "true"; //$NON-NLS-1$
	private final static String FALSE = "false"; //$NON-NLS-1$
	private final static int HELP_DROPDOWN_LINE_HEIGHT = 10;

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
		
	public void expandHelpMenu(final View v){
	    if("+".equals(statusIconLabel.getText())){	//$NON-NLS-1$
			statusIconLabel.setText(getString(R.string.account_security_minus_text));
			detailHelpLabel.setMaxLines(HELP_DROPDOWN_LINE_HEIGHT);
		}
	    else{
	    	statusIconLabel.setText(getString(R.string.account_security_plus_text));
	    	detailHelpLabel.setMaxLines(0);
	    }
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
			Log.e(TAG, "Could not B64 encode strong auth response body: " + e);//$NON-NLS-1$
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
