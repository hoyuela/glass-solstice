package com.discover.mobile.login;

import com.discover.mobile.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LoggedInLandingPage extends Activity{
	

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setupViews();
		
		ListView listView = (ListView)findViewById(R.id.optionsTable);
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>( );
		listView.setAdapter(null);
	}
	
	private void setupViews() {
		setContentView(R.layout.logged_in_landing);
	}
	
	
}
