package com.discover.mobile.login;

import com.discover.mobile.R;

import android.app.Activity;
import android.os.Bundle;

public class LoggedInLandingPage extends Activity{
	

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setupViews();
	}
	
	private void setupViews() {
		setContentView(R.layout.logged_in_landing);
	}
	
	
}
