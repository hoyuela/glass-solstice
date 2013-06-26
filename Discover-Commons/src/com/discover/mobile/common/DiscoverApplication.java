package com.discover.mobile.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;

public class DiscoverApplication extends Application {
	
	/**The cache object that maintains the most recent user's choice to use current location*/
	private static LocationPreferenceCache locationPreference = new LocationPreferenceCache();
	
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
	 * 
	 * @return a reference to the current locationPreference object for this instance of the application. Used for
	 * caching user settings for the ATM Locator, use my location modal.
	 */
	public static final LocationPreferenceCache getLocationPreference() {
		if(locationPreference == null) {
			locationPreference = new LocationPreferenceCache();
		}
		
		return locationPreference;
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
