package com.discover.mobile.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(formKey = "dDAzM3VJakhEcHpvV2dsZlpJcXZqOGc6MQ")
public class DiscoverApplication extends Application {

	Map<String, Object> globalData=new HashMap<String, Object>();
	List<String>cookie;
	
	static {
		System.setProperty("guice.custom.loader", "false");
	}

	@Override
	public void onCreate() {
		super.onCreate();
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
	public Map<String,Object> getData()
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
