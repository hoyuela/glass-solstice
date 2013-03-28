package com.discover.mobile.common;

import java.util.HashMap;
import java.util.List;

import org.acra.annotation.ReportsCrashes;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;

@ReportsCrashes(formKey = "dDAzM3VJakhEcHpvV2dsZlpJcXZqOGc6MQ")
public class DiscoverApplication extends Application {

	HashMap<String, Object> globalData=new HashMap<String, Object>();
	List<String>cookie;
	
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
	
	private boolean isApplicationProcess(final String processName) {
		final Context context = getApplicationContext();
		final ActivityManager actMgr = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		final List<RunningAppProcessInfo> appList = actMgr.getRunningAppProcesses();
		for (final RunningAppProcessInfo info : appList) {
			if (info.pid == android.os.Process.myPid()
					&& processName.equals(info.processName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Set the data in the hashmap
	 * @param className class name where data is located
	 * @param data actual data returned from webservice
	 */
	public void setData(final String className,final Object data)
	{
		globalData.put(className, data);
	}
    
	/**
	 * Thsi method return the data stored in hashmap
	 * @return data Actual data
	 */
	public HashMap<String,Object> getData()
	{
		return globalData;
		
	}
	
	/**
	 * This function clear whole cache.
	 */
	public void clearCache()
	{
		if(globalData != null)
		{
			globalData.clear();
		}
	}
	
	/**
	 * This will delete cache object by sending key
	 * @param key
	 */
	public void deleteCacheObject(final String key)
	{
		if(globalData != null)
		{
			globalData.remove(key);
		}
	}
	
	public void setCookie(final List<String> cookie)
	{
		this.cookie=cookie;
	}
	
	public List<String>getCookie()
	{
		return cookie;
	}
}
