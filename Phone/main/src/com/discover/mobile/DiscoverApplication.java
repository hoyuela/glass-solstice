package com.discover.mobile;

import android.app.Application;

public class DiscoverApplication extends Application {
	
	static {
		System.setProperty("guice.custom.loader", "false");
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
//		EasyTracker.getInstance().setContext(this);
	}
	
}
