package com.discover.mobile;

import java.util.List;

import org.acra.annotation.ReportsCrashes;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;

@ReportsCrashes(formKey = "dDAzM3VJakhEcHpvV2dsZlpJcXZqOGc6MQ")
public class DiscoverApplication extends Application {
	
	static {
		System.setProperty("guice.custom.loader", "false");
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
//		if (isApplicationProcess(getPackageName())) {
//			try {
//				ACRA.init(this);
//				Class sMode = Class.forName("android.os.StrictMode");
//				Method enabledDefaults = sMode.getMethod("enableDefaults");
//				enabledDefaults.invoke(null);
//			} catch (Exception e) {
//				Log.v("Reflection Error", "...not supported. skipping...");
//			}
//		}
		//		EasyTracker.getInstance().setContext(this);
	}
	
	private boolean isApplicationProcess(String processName) {
		Context context = getApplicationContext();
		ActivityManager actMgr = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appList = actMgr.getRunningAppProcesses();
		for (RunningAppProcessInfo info : appList) {
			if (info.pid == android.os.Process.myPid()
					&& processName.equals(info.processName)) {
				return true;
			}
		}
		return false;
	}
}
