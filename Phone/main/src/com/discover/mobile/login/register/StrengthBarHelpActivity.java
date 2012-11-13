package com.discover.mobile.login.register;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.discover.mobile.R;

public class StrengthBarHelpActivity extends Activity{
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		if (savedInstanceState == null) {
			
			Bundle extras = getIntent().getExtras();
        	if(extras != null) {
        		String helpLayout = extras.getString("helpType");
        		if("id".equals(helpLayout))
        			setContentView(R.layout.account_info_id_strength_help);
        		else
        			setContentView(R.layout.account_info_password_strength_help);
        	}
		}

	}
	
	public void goBack(View v){
		finish();
	}
}
