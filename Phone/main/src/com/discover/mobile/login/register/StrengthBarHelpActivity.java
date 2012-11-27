package com.discover.mobile.login.register;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.discover.mobile.R;

public class StrengthBarHelpActivity extends Activity {
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final Bundle extras = getIntent().getExtras();
		if(extras != null) {
			final String helpLayout = extras.getString("ScreenType");
			if("id".equals(helpLayout))
				setContentView(R.layout.account_info_id_strength_help);
			else
				setContentView(R.layout.account_info_password_strength_help);
		}
		
	}
	
	@Override
	public void onBackPressed() {
		goBack(null);
	}
	public void goBack(View v){
		setResult(RESULT_OK);
		finish();
	}

}
