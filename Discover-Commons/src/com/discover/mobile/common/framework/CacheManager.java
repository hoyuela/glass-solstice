package com.discover.mobile.common.framework;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Class used to maintain session information for a user logged in. This class
 * follows a singleton design pattern allowing only one instance of this class
 * to ever exists. It's data members are set by NetworkServiceCall<> objects
 * upon receiving a successful response. As an example, the customerInfo object
 * is updated by the CustomerServiceCall class.
 * 
 * NOTE:  this cachemanager holds a single value per networkServiceCall class, 
 * as the key s the class name itself. 
 * 
 * @author ekaram
 * 
 */
public  class CacheManager {
	
	/**
	 * Singleton instance of this class
	 */
	private final static CacheManager cacheManager = new CacheManager();
	
	private HashMap<Class,Serializable> cacheData = new HashMap<Class,Serializable>();
	
	/**
	 * Default constructor made private to allow a single instance
	 */
	protected CacheManager() {

	}
	
	/**
	 * 
	 * @param o
	 */
	public void updateCache(Serializable o){
		cacheData.put(o.getClass(), o);
	}
	
	/**
	 * 
	 * @param c
	 * @return
	 */
	public Object getObjectFromCache(Class c){ 
		return cacheData.get(c);
	}

	/**
	 * 
	 * @return Returns reference to single instance of CacheManager
	 */
	public static CacheManager instance() {
		return cacheManager;
	}


	/**
	 * Used to clear all cached data during the session of a user logged into
	 * Bank
	 */
	public void clearSession() {
		cacheData.clear();
	}
	
	
	/**
	 * 
	 * @param c
	 * @return
	 */
	public Object removeObjectFromCache(Class c){ 
		return cacheData.remove(c);
	}
	
}
