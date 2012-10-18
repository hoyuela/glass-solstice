package com.discover.mobile.login;

import com.discover.mobile.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.discover.mobile.commons.*;

public class LoginActivity extends Activity {
	private Button loginButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LoginLogout service = new LoginLogout();
		service.preAuthCheck();
		
		setupViews();
	}
	
	private void setupViews() {
		setContentView(R.layout.login);
//		loginButton.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				
//			}
//		});
	}
}
