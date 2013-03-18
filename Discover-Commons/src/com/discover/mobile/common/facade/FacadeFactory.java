/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.common.facade;

import java.util.HashMap;

import com.discover.mobile.common.AccountType;
import com.discover.mobile.common.framework.Conductor;



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
		return (LogoutFacade) getImplClass("com.discover.mobile.common.facade.LogoutFacadeImpl");
	}

	/**
	 * The BANK logout facade
	 * @return
	 */
	public static BankLogoutFacade getBankLogoutFacade(){
		return (BankLogoutFacade) getImplClass("com.discover.mobile.bank.facade.BankLogoutFacadeImpl");
	}

	/**
	 * The card logout facade
	 * @return
	 */
	public static CardLogoutFacade getCardLogoutFacade(){
		return (CardLogoutFacade) getImplClass("com.discover.mobile.card.facade.CardLogoutFacadeImpl");
	}

	/**
	 * The login facade 
	 * @return
	 */
	public static LoginActivityFacade getLoginFacade(){
		return (LoginActivityFacade) getImplClass("com.discover.mobile.bank.facade.LoginActivityFacadeImpl");
	}
	/**
	 * Customer service resides in bank code, but is shared
	 * @return
	 */
	public static CustomerServiceFacade getCustomerServiceFacade(){
		return (CustomerServiceFacade) getImplClass("com.discover.mobile.bank.facade.CustomerServiceFacadeImpl");
	}
	
	/**
	 * Customer service resides in bank code, but is shared
	 * @return
	 */
	public static PushFacade getPushFacade(){
		return (PushFacade) getImplClass("com.discover.mobile.card.facade.PushFacadeImpl");
	}
	
	
	/**
	 * Common card navigation 
	 * @return
	 */
	public static CardFacade getCardFacade(){
		return (CardFacade) getImplClass("com.discover.mobile.card.facade.CardFacadeImpl");
	}
	
	/**
	 * Common card navigation 
	 * @return
	 */
	public static CardLoginFacade getCardLoginFacade(){
		return (CardLoginFacade) getImplClass("com.discover.mobile.card.facade.CardLoginFacadeImpl");
	}
	
	/**
	 * Common card navigation 
	 * @return
	 */
	public static BankLoginFacade getBankLoginFacade(){
		return (BankLoginFacade) getImplClass("com.discover.mobile.bank.facade.BankLoginFacadeImpl");
	}
	
	
	/**
	 * Returns the conductor facade
	 * @param accountType
	 * @return
	 */
	public static Conductor getConductorFacade(AccountType accountType){ 
		if ( accountType == AccountType.CARD_ACCOUNT ){ 
			return (Conductor) getImplClass("com.discover.mobile.card.facade.CardConductorFacadeImpl");
		}else{ 
			return (Conductor) getImplClass("com.discover.mobile.card.facade.BankConductorFacadeImpl");
		}
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
