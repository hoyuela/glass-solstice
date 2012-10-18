package com.discover.mobile.login;

import com.discover.mobile.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LandingActivity extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setupViews();
		setupButtons();
	}
	
	private void setupViews() {
		setContentView(R.layout.landing);
	}
	
	private void setupButtons(){
		//Setup the button that takes us to the login screen.
		Button creditCardLoginButton = (Button)findViewById(R.id.card_login_button);
		
		creditCardLoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				navigateToLogin();
			}
		});
	}
	
	private void navigateToLogin(){
		Intent loginActivity = new Intent(this, LoginActivity.class);
		this.startActivity(loginActivity);
	}
}
