/*
 * Copyright Solstice 2013
 */
package com.discover.mobile.common.framework;

import java.io.Serializable;

import com.discover.mobile.common.net.NetworkServiceCall;

/**
 * For the child class service call factories to adhere
 * @author ekaram
 *
 */
public interface ServiceCallFactory {
	
	/**
	 * The implementing class is expected to map the cache object class to a
	 * private method which looks up the required service call for the given
	 * cache object and instantiates a callback and creates the Call object
	 * itself
	 * 
	 * @param cacheObject
	 *            - the class of the cache object we are trying to fetch
	 * @param payload
	 *            is the payload required to create the service call. this is
	 *            optional.
	 * @return A NetworkServiceCall
	 */
	public NetworkServiceCall createServiceCall(Class<Object> cacheObject, final Serializable payload);
	
	

}
