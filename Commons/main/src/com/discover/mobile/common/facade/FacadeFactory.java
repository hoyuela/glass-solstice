/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.common.facade;

import java.util.HashMap;



/**
 * A factory used to get an appropriate facade for child project code.
 * 
 * @author ekaram
 *
 */
public class FacadeFactory {
	
	/**
	 * The private map to store the singleton objects
	 */
	private static HashMap<String, Object> singletons = new HashMap<String, Object>();
	
	/**
	 * The logout facade
	 * @return
	 */
	public static LogoutFacade getLogoutFacade(){
		
		return (LogoutFacade) getImplClass("com.discover.mobile.bank.facade.LogoutFacadeImpl");
	}
	
	/**
	 * Returns a strong auth facade.  Currently this code lives in bank.  may still need to refactor
	 * @return
	 */
	public static StrongAuthFacade getStrongAuthFacade(){
		return (StrongAuthFacade) getImplClass("com.discover.mobile.bank.facade.StrongAuthFacadeImpl");
	}
	/**
	 * The login facade 
	 * @return
	 */
	public static LoginFacade getLoginFacade(){
		return (LoginFacade) getImplClass("com.discover.mobile.bank.facade.LoginFacadeImpl");
	}
	/**
	 * Customer service resides in bank code, but is shared
	 * @return
	 */
	public static CustomerServiceFacade getCustomerServiceFacade(){
		return (CustomerServiceFacade) getImplClass("com.discover.mobile.bank.facade.CustomerServiceFacadeImpl");
	}
	
	
	/**
	 * Loads the impl class, expecting to find it in the classloader.
	 * 
	 * Expects the facade class to have a single, unparameterized constructor
	 * 
	 * @param fullyQualifiedClassName
	 * @return
	 */
	private static synchronized Object getImplClass(String fullyQualifiedClassName){
		Object facade = singletons.get(fullyQualifiedClassName);
		if ( facade == null ) { 
			try {
				
				facade = Class.forName(fullyQualifiedClassName).getConstructors()[0].newInstance(null);
				
				singletons.put(fullyQualifiedClassName, facade);
			} catch (Exception e) {
				throw new RuntimeException("FACADE BOOTSTRAP FAILED: Unable to find facade impl class:" + fullyQualifiedClassName);
			}
		}
		return facade;
	}
	
	
}
