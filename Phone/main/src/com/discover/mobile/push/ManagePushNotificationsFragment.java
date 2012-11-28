package com.discover.mobile.push;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import android.os.Bundle;

import com.discover.mobile.R;

@ContentView(R.layout.notification_manage_layout)
public class ManagePushNotificationsFragment extends RoboActivity{

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
}
