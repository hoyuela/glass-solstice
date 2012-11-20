package com.discover.mobile.security;

import java.security.NoSuchAlgorithmException;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.auth.StrongAuthAnswerCall;
import com.discover.mobile.common.auth.StrongAuthAnswerDetails;
import com.discover.mobile.common.net.callback.AsyncCallbackAdapter;
import com.discover.mobile.login.LoginActivity;

public class EnhancedAccountSecurityActivity extends RoboActivity{
	
	private final static String TAG = EnhancedAccountSecurityActivity.class.getSimpleName();
	private TextView detailHelpLabel, statusIconLabel;
	private String questionId;
	
	@InjectView(R.id.account_security_question_placeholder_label)
	private TextView questionLabel;
	
	@InjectView(R.id.account_security_question_answer_field)
	private EditText questionAnswerField;
	
	@InjectView(R.id.account_security_choice_radio_group)
	private RadioGroup securityRadioGroup;
	
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enhanced_account_security);
		final Bundle extras = getIntent().getExtras();
    	if(extras != null) {
    		final String question = 
    				extras.getString(IntentExtraKey.STRONG_AUTH_QUESTION);
    		questionId = 
    				extras.getString(IntentExtraKey.STRONG_AUTH_QUESTION_ID);
    		
    		questionLabel.setText(question);
    	}
	}
	
	@Override
	public void onStart(){
		super.onStart();
		setupToggleItems();
	}
	private void setupToggleItems(){
		detailHelpLabel = (TextView)findViewById(R.id.account_security_whats_this_detail_label);
		statusIconLabel = (TextView)findViewById(R.id.account_security_plus_label);

	}
	
	public void expandHelpMenu(final View v){
			if("+".equals(statusIconLabel.getText())){
				statusIconLabel.setText(getString(R.string.account_security_minus_text));
				detailHelpLabel.setMaxLines(10);
			}
			else{
				statusIconLabel.setText(getString(R.string.account_security_plus_text));
				detailHelpLabel.setMaxLines(0);
			}
	}
	
	public void submitSecurityInfo(final View v){
		
		final ProgressDialog progress = ProgressDialog.show(this, "Discover", "Loading...", true);
		
		final AsyncCallbackAdapter<Object> callback = new AsyncCallbackAdapter<Object>() {
			@Override
			public void success(final Object value) {
				progress.dismiss();
				Log.d(TAG, "Strong Auth Succeeded!");
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
		
		if(selectedIndex == 0)
			answerDetails.bindDevice = "true";
		else
			answerDetails.bindDevice = "false";
		
		StrongAuthAnswerCall strongAuthAnswer;
		try {
			
			strongAuthAnswer = new StrongAuthAnswerCall(this, callback, answerDetails);
			strongAuthAnswer.submit();
			
		} catch (final NoSuchAlgorithmException e) {
			
			// TODO Auto-generated catch block
			Log.e(TAG, "Could not encode strong auth response body.");
			e.printStackTrace();
			
		}
		
	}
	
	@Override
	public void onBackPressed() {
		
	   final Intent navToMain = new Intent(this, LoginActivity.class);
	   startActivity(navToMain);
	   
	}
	
	private void finishWithResultOK() {
//		Intent emptyData = new Intent();
//		emptyData.setAction("STRONG_AUTH_SUCCESS");
//		if (getParent() == null) {
//		    setResult(Activity.RESULT_OK, emptyData);
//		} else {
//		    getParent().setResult(Activity.RESULT_OK, emptyData);
//		}
		setResult(RESULT_OK);
		finish();
	}
	
}
