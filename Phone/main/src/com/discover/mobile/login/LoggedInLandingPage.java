package com.discover.mobile.login;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.discover.mobile.R;

public class LoggedInLandingPage extends Activity{
	
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setupViews();
		
		final ListView listView = (ListView)findViewById(R.id.optionsTable);
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>( );
		listView.setAdapter(null);
	}
	
	private void setupViews() {
		setContentView(R.layout.logged_in_landing);
	}
	
	
}
