package com.discover.mobile.login;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import android.os.Bundle;
import android.widget.ListView;

import com.discover.mobile.R;

@ContentView(R.layout.logged_in_landing)
public class LoggedInLandingPage extends RoboActivity {
	
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		final ListView listView = (ListView)findViewById(R.id.optionsTable);
		listView.setAdapter(null);
	}
	
}
