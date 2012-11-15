package com.discover.mobile.login.register;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.discover.mobile.R;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.ScreenType;

public class StrengthBarHelpActivity extends Activity{
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
    	if(extras != null) {
    		String helpLayout = extras.getString(IntentExtraKey.HELP_TYPE);
    		if(ScreenType.UID_STRENGTH_HELP.equals(helpLayout))
    			setContentView(R.layout.account_info_id_strength_help);
    		else
    			setContentView(R.layout.account_info_password_strength_help);
    	}

	}
	
	public void goBack(View v){
		finish();
	}
}
