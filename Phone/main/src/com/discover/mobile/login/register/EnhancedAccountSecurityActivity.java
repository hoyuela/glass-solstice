package com.discover.mobile.login.register;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.IntentExtraKey;

public class EnhancedAccountSecurityActivity extends RoboActivity{
	
	private TextView detailHelpLabel, statusIconLabel;
	private String question, questionId;
	
	@InjectView(R.id.account_security_question_placeholder_label)
	private TextView questionLabel;
	
	
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
		Intent accountInfoTwoActivity = new Intent(this, CreateLoginActivity.class);
		this.startActivity(accountInfoTwoActivity);
		
	}

}