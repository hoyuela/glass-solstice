//package com.discover.mobile.bank.framework;
//
//import com.discover.mobile.bank.login.LoginActivity;
//import com.discover.mobile.bank.services.auth.BankLoginDetails;
//import com.discover.mobile.common.facade.FacadeFactory;
//import com.discover.mobile.common.framework.Conductor;
//import com.discover.mobile.common.framework.ServiceCallFactory;
//
//public class BankLoginServices extends Conductor {
//
//	public BankLoginServices(ServiceCallFactory pServiceCallFactory) {
//		super(pServiceCallFactory);
//	}
//
//	private static BankLoginDetails loginDetails;
//	
//	/**
//	 * Authorizes a Bank user against the service. If successful, the user will
//	 * be logged-in and taken to the Bank landing page of the application.
//	 * 
//	 * @param credentials
//	 */
//	public static void authorizeLogin(final BankLoginDetails credentials) {
//		loginDetails = credentials;
//		BankServiceCallFactory.createLoginCall(credentials).submit();
//	}
//
//	/**
//	 * Authorizes an SSO User against Bank using a BankSSOPayload, which is
//	 * obtained from a Card service.
//	 * 
//	 * @param bankSSOPayload
//	 *            payload with which the user is authorized.
//	 */
//	public static void authWithBankPayload(final String bankSSOPayload) {
//		// TODO log user in against token/sso using this payload.
////		BankServiceCallFactory.createSSOLoginCall(bankSSOPayload).submit;
//		
//		loginDetails = null;
//	}
//
//	/**
//	 * Authorizes an SSO User against Card using a CardSSOPayload, which in some
//	 * cases is obtained from a call to {@code BankLoginServices.authorizeLogin()}.
//	 *
//	 * @param activity
//	 * @param tokenValue
//	 * @param hashedTokenValue
//	 */
//	public static void authWithCardPayload(LoginActivity activity, String tokenValue, String hashedTokenValue) {
//		FacadeFactory.getCardLoginFacade().loginWithPayload(activity,
//				tokenValue, hashedTokenValue);
//	}
//
//	/**
//	 * Authorizes an SSO User against Bank when no BankSSOPayload is available.
//	 * This is due to an A/L/U error returned from a Card service.
//	 */
//	public static void authDueToALUStatus() {
//		if(loginDetails != null) {
//			BankServiceCallFactory.createLoginCall(loginDetails, true).submit();
//			loginDetails = null;
//		}
//	}
//	
//	@Override
//	public Class lookupCacheRequiredForDestination(Class c) {
//		return null;
//	}
//}