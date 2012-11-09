package com.discover.mobile.register;

import android.app.Activity;
import android.os.Bundle;

import com.discover.mobile.R;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.ScreenType;

public class AccountInformationHelpActivity extends Activity {
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		if (savedInstanceState == null) {
			
			final Bundle extras = getIntent().getExtras();
			
        	if(extras != null) {
        		
        		switch(extras.getInt(IntentExtraKey.HELP_TYPE)) {
        			case ScreenType.UID_STRENGTH_HELP:
        				setContentView(R.layout.account_info_id_strength_help);
        				break;
        			case ScreenType.PASSWORD_STRENGTH_HELP:
        				setContentView(R.layout.account_info_password_strength_help);
        				break;
        			default:
        				break;
        		}
        	}
		}

	}
}
