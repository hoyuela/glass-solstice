/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.common.delegates;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;



/**
 * A factory used to get an appropriate delegate for child project code.
 * 
 * @author ekaram
 *
 */
public class DelegateFactory {
	
	private static HashMap<String, Object> singletons = new HashMap<String, Object>();
	
	public static LogoutDelegate getLogoutDelegate(){
		
		return (LogoutDelegate) getImplClass("com.discover.mobile.bank.delegates.LogoutDelegateImpl");
	}
	
	public static StrongAuthDelegate getStrongAuthDelegate(){
		return (StrongAuthDelegate) getImplClass("com.discover.mobile.bank.delegates.StrongAuthDelegateImpl");
	}
	
	public static LoginDelegate getLoginDelegate(){
		return (LoginDelegate) getImplClass("com.discover.mobile.bank.delegates.LoginDelegateImpl");
	}
	
	public static CustomerServiceDelegate getCustomerServiceDelegate(){
		return (CustomerServiceDelegate) getImplClass("com.discover.mobile.bank.delegates.CustomerServiceDelegateImpl");
	}
	
	
	/**
	 * Loads the impl class, expecting to find it in the classloader
	 * 
	 * @param fullyQualifiedClassName
	 * @return
	 */
	private static synchronized Object getImplClass(String fullyQualifiedClassName){
		Object delegate = singletons.get(fullyQualifiedClassName);
		if ( delegate == null ) { 
			try {
				
				delegate = Class.forName(fullyQualifiedClassName).getConstructors()[0].newInstance(null);
				
				singletons.put(fullyQualifiedClassName, delegate);
			} catch (Exception e) {
				throw new RuntimeException("DELEGATE BOOTSTRAP FAILED: Unable to find delegate impl class:" + fullyQualifiedClassName);
			}
		}
		return delegate;
	}
	
	
}
