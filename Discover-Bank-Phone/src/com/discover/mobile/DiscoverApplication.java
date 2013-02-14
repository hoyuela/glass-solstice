package com.discover.mobile;

import org.acra.annotation.ReportsCrashes;

import android.app.Application;

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

	//	private boolean isApplicationProcess(final String processName) {
	//		final Context context = getApplicationContext();
	//		final ActivityManager actMgr = (ActivityManager) context
	//				.getSystemService(Context.ACTIVITY_SERVICE);
	//		final List<RunningAppProcessInfo> appList = actMgr.getRunningAppProcesses();
	//		for (final RunningAppProcessInfo info : appList) {
	//			if (info.pid == android.os.Process.myPid()
	//					&& processName.equals(info.processName)) {
	//				return true;
	//			}
	//		}
	//		return false;
	//	}
}
