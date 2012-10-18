package com.discover.mobile.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.discover.mobile.R;
import com.discover.mobile.common.auth.AuthService;

public class LoginActivity extends Activity {
	private Button loginButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		AuthService service = new AuthService();
		service.preAuthCheck();
		
		setupViews();
		setupButtons();
	}
	
	private void setupViews() {
		setContentView(R.layout.login);
	}
	
	private void setupButtons(){
		Button loginButton = (Button)findViewById(R.id.login_button);
		loginButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				logIn();
			}
		});
	}
	
	private void logIn(){
		Intent logIn = new Intent(this, LoggedInLandingPage.class);
		this.startActivity(logIn);
		
	}
}
