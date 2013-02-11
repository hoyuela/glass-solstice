/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.common.delegates;



/**
 * A factory used to get an appropriate delegate for child project code.
 * 
 * 
 * @author ekaram
 *
 */
public class DelegateFactory {
	
	public static AsyncCallbackDelegate getAsyncCallbackDelegate(){
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public static LogoutDelegate getLogoutDelegate(){
		//FIXME
		return null;
	}
	
	public static StrongAuthDelegate getStrongAuthDelegate(){
		return null;
	}
	
	public static LoginDelegate getLoginDelegate(){
		return null;
	}
	
	public static CustomerServiceDelegate getCustomerServiceDelegate(){
		return null;
	}
}
