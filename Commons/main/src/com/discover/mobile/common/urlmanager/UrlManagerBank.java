package com.discover.mobile.common.urlmanager;

/**
 * This class is used for adding and getting URL's for bank services. Any bank
 * URL's should go into the card Url Manager class
 * 
 * @author ajleeds
 * 
 */
public class UrlManagerBank {
	private static final String BASE_URL = "https://beta.discoverbank.com";
//	private static final String BASE_URL = "http://192.168.2.177:8008"; //Henry's Laptop
//	private static final String BASE_URL = "http://solsticebeta.com/Discover/DiscoverBank";
//	private static final String BASE_URL = "http://192.168.2.71:8008";
//	private static final String BASE_URL = "http://solsticebeta.com/Discover/Users/Henry/DiscoverBank";

	private static final String AUTHENTICATE_CURRENT_CUSTOMER_URL = "/api/customers/current";
	private static final String GET_TOKEN_URL = "/api/auth/token";
	private static final String STRONG_AUTH_URL = "/api/auth/strongauth";
	private static final String ACCOUNT_URL = "/api/accounts";
	private static final String EXTERNAL_ACCOUNTS_URL = "/api/accounts/external";
	private static final String CUSTOMER_SERVICE_URL = "/api/customers/current";
	public static String LOGOUT_URL = null;
	

	/**
	 * @return the baseUrl
	 */
	public static String getBaseUrl() {
		return BASE_URL;
	}

	/**
	 * @return the authenticateCurrentCustomerUrl
	 */
	public static String getAuthenticateCurrentCustomerUrl() {
		return AUTHENTICATE_CURRENT_CUSTOMER_URL;
	}

	/**
	 * @return the getTokenUrl
	 */
	public static String getGetTokenUrl() {
		return GET_TOKEN_URL;
	}

	/**
	 * @return the strongAuthUrl
	 */
	public static String getStrongAuthUrl() {
		return STRONG_AUTH_URL;
	}

	/**
	 * @return the accountUrl
	 */
	public static String getAccountUrl() {
		return ACCOUNT_URL;
	}

	/**
	 * @return the externalAccountsUrl
	 */
	public static String getExternalAccountsUrl() {
		return EXTERNAL_ACCOUNTS_URL;
	}

	/**
	 * @return the customerServiceUrl
	 */
	public static String getCustomerServiceUrl() {
		return CUSTOMER_SERVICE_URL;
	}

	/**
	 * @return the logoutUrl
	 */
	public static String getLogoutUrl() {
		return LOGOUT_URL;
	}

	/**
	 * This is used in order to set the logout URL that is returned from the
	 * service
	 * 
	 * @param url
	 */
	public static void setLogoutUrl(String url) {
		
		LOGOUT_URL = url.replaceAll("http://beta.discoverbank.com", "");
	}

}
