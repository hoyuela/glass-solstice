package com.discover.mobile.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.discover.mobile.R;

public class EnhancedAccountSecurity extends Activity{
	
	private TextView detailHelpLabel, statusIconLabel;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enhanced_account_security);
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
			if(statusIconLabel.getText().equals("+")){
				statusIconLabel.setText(getString(R.string.account_security_minus_text));
				detailHelpLabel.setMaxLines(10);
			}
			else{
				statusIconLabel.setText(getString(R.string.account_security_plus_text));
				detailHelpLabel.setMaxLines(0);
			}
	}
	
	public void submitSecurityInfo(View v){
		Intent accountInfoTwoActivity = new Intent(this, AccountInformationTwoActivity.class);
		this.startActivity(accountInfoTwoActivity);
		
	}

}