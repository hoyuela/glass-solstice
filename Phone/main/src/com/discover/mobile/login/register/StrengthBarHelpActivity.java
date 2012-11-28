package com.discover.mobile.login.register;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.discover.mobile.R;

public class StrengthBarHelpActivity extends Activity {
	
	// FIXME hardcoded strings, use ScreenType
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final Bundle extras = getIntent().getExtras();
		if(extras != null) {
			final String helpLayout = extras.getString("ScreenType");
			if("id".equals(helpLayout))
				setContentView(R.layout.register_help_id_strength);
			else
				setContentView(R.layout.register_help_password_strength);
		}
		
	}
	
	@Override
	public void onBackPressed() {
		goBack(null);
	}
	
	public void goBack(final View v){
		setResult(RESULT_OK);
		finish();
	}
	
}
