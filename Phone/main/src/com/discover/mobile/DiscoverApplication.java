package com.discover.mobile;

import android.app.Application;

import com.google.analytics.tracking.android.EasyTracker;

public class DiscoverApplication extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		EasyTracker.getInstance().setContext(this);
	}
	
}
