package com.discover.mobile.login;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.discover.mobile.R;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;

@ContentView(R.layout.logged_in_landing)
public class LoggedInLandingPage extends RoboActivity {
	
	@InjectView(R.id.optionsTable)
	ListView listView;
	
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		TrackingHelper.trackPageView(AnalyticsPage.ACCOUNT_LANDING);
		
		listView.setAdapter(null);
	}
	
	@Override
	public void onBackPressed() {
	   Intent navToMain = new Intent(this, LoginActivity.class);
	   startActivity(navToMain);
	}
	
}
