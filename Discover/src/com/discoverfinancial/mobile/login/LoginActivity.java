package com.discoverfinancial.mobile.login;

import com.discoverfinancial.mobile.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LoginActivity extends Activity {
	private Button loginButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
