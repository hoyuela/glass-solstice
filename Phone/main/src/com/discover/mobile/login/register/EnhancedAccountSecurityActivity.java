package com.discover.mobile.login.register;

import java.net.HttpURLConnection;
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
import com.discover.mobile.common.ScreenType;
import com.discover.mobile.common.auth.StrongAuthAnswerCall;
import com.discover.mobile.common.auth.StrongAuthAnswerDetails;
import com.discover.mobile.common.auth.registration.AccountInformationDetails;
import com.discover.mobile.common.forgotpassword.ForgotPasswordDetails;
import com.discover.mobile.common.net.json.MessageErrorResponse;
import com.discover.mobile.common.net.response.AsyncCallbackAdapter;
import com.discover.mobile.common.net.response.ErrorResponse;
import com.discover.mobile.forgotuidpassword.EnterNewPasswordActivity;

public class EnhancedAccountSecurityActivity extends RoboActivity{
	private final static String TAG = 
			EnhancedAccountSecurityActivity.class.getSimpleName();
	private TextView detailHelpLabel, statusIconLabel;
	private String question, questionId, nextScreen;
	private AccountInformationDetails accountInformationDetails;
	private ForgotPasswordDetails forgotPasswordDetails;
	
	@InjectView(R.id.account_security_question_placeholder_label)
	private TextView questionLabel;
	
	@InjectView(R.id.account_security_question_answer_field)
	private EditText questionAnswerField;
	
	@InjectView(R.id.account_security_choice_radio_group)
	private RadioGroup securityRadioGroup;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enhanced_account_security);
		final Bundle extras = getIntent().getExtras();
    	if(extras != null) {
    		question = 
    				extras.getString(IntentExtraKey.STRONG_AUTH_QUESTION);
    		questionId = 
    				extras.getString(IntentExtraKey.STRONG_AUTH_QUESTION_ID);
    		accountInformationDetails = (AccountInformationDetails)
    				extras.getSerializable(IntentExtraKey.REGISTRATION1_DETAILS);
    		forgotPasswordDetails = (ForgotPasswordDetails)
    				extras.getSerializable(IntentExtraKey.FORGOT_PASS_DETAILS);
    		nextScreen = 
    				extras.getString(IntentExtraKey.SCREEN_TYPE);
    		
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
	
	public void expandHelpMenu(View v){
			if("+".equals(statusIconLabel.getText())){
				statusIconLabel.setText(getString(R.string.account_security_minus_text));
				detailHelpLabel.setMaxLines(10);
			}
			else{
				statusIconLabel.setText(getString(R.string.account_security_plus_text));
				detailHelpLabel.setMaxLines(0);
			}
	}
	
	public void submitSecurityInfo(View v){
		
		final ProgressDialog progress = ProgressDialog.show(this, "Discover", "Loading...", true);
		
		final AsyncCallbackAdapter<Object> callback = new AsyncCallbackAdapter<Object>() {
			@Override
			public void success(final Object value) {
				progress.dismiss();
				navToNextScreen();
			}

			@Override
			public boolean handleErrorResponse(final ErrorResponse errorResponse) {
				progress.dismiss();
				
				switch (errorResponse.getHttpStatusCode()) {
					case HttpURLConnection.HTTP_BAD_REQUEST:
						return true;
					case HttpURLConnection.HTTP_UNAUTHORIZED:
						return true;
					case HttpURLConnection.HTTP_INTERNAL_ERROR: //couldn't authenticate user info.
						return true;
				}
				
				return false;
			}

			@Override
			public boolean handleMessageErrorResponse(final MessageErrorResponse messageErrorResponse) {

				progress.dismiss();
				Log.e(TAG, "Error message: " + messageErrorResponse.getMessage());
				
				
				switch(messageErrorResponse.getMessageStatusCode()){
				
				default:
					return false;
				}
				
			}
		};
		
		StrongAuthAnswerDetails answerDetails = new StrongAuthAnswerDetails();
		answerDetails.questionAnswer = questionAnswerField.getText().toString();
		answerDetails.questionId = questionId;
		
		//Find out which radio button is pressed.
		int radioButtonId = securityRadioGroup.getCheckedRadioButtonId();
		View selectedButton = securityRadioGroup.findViewById(radioButtonId);
		int selectedIndex = securityRadioGroup.indexOfChild(selectedButton);
		
		if(selectedIndex == 0)
			answerDetails.bindDevice = "true";
		else
			answerDetails.bindDevice = "false";
		
		StrongAuthAnswerCall strongAuthAnswer;
		try {
			strongAuthAnswer = new StrongAuthAnswerCall(this, callback, answerDetails);
			strongAuthAnswer.submit();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void navToNextScreen(){
		//Go to registration screen -> forgot both/register
		if(nextScreen != null || nextScreen != ""){
			Intent createLoginActivity = new Intent(this,
					CreateLoginActivity.class);
			createLoginActivity.putExtra(IntentExtraKey.REGISTRATION1_DETAILS, 
					accountInformationDetails);
			startActivity(createLoginActivity);
		}
		//Go to enter new password screen
		else if(nextScreen.equals(ScreenType.FORGOT_PASSWORD)){
			Intent createLoginActivity = new Intent(this,
					EnterNewPasswordActivity.class);
			createLoginActivity.putExtra(IntentExtraKey.FORGOT_PASS_DETAILS,
					forgotPasswordDetails);
			startActivity(createLoginActivity);
			
		}
			
			
	}
}