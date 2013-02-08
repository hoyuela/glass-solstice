package com.discover.mobile.common.urlmanager;

import java.util.Map;

import com.discover.mobile.common.net.json.bank.ReceivedUrl;

/**
 * This class is used for adding and getting URL's for bank services. Any bank
 * URL's should go into the card Url Manager class
 * 
 * @author ajleeds
 * 
 */
public class BankUrlManager {
	private static final String BASE_URL = "https://beta.discoverbank.com";
	//	private static final String BASE_URL = "http://192.168.2.177:8008"; //Henry's Laptop
	//	private static final String BASE_URL = "http://solsticebeta.com/Discover/DiscoverBank";
	//	private static final String BASE_URL = "http://192.168.1.94:8008";
	//	private static final String BASE_URL = "http://solsticebeta.com/Discover/Users/Henry/DiscoverBank";
	//private static final String BASE_URL = "http://192.168.1.70:8009"; //Jon's Laptop
	//private static final String BASE_URL = "http://solsticebeta.com/Discover/Users/Jon/DiscoverBank"; //Jon Mock Service

	public static final double MAX_IDLE_TIME = 900; //900 = 15 min
	public static final String EMPTY = "";
	private static final String AUTHENTICATE_CURRENT_CUSTOMER_URL = "/api/customers/current";
	private static final String GET_TOKEN_URL = "/api/auth/token";
	private static final String STRONG_AUTH_URL = "/api/auth/strongauth";
	private static final String CUSTOMER_SERVICE_URL = "/api/customers/current";
	private static Map<String, ReceivedUrl> links;

	/**
	 * Keys for the urls in order to retrieve from the map
	 */
	public static final String ACCOUNT_URL_KEY = "accounts";
	public static final String TRANSFER_URL_KEY = "transfers";
	public static final String PING_URL_KEY = "ping";
	public static final String PAYEES_URL_KEY = "payees";
	public static final String LOGOUT_URL_KEY = "logout";
	public static final String PAYMENTS_URL_KEY = "payments";


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
	 * @return the customerServiceUrl
	 */
	public static String getCustomerServiceUrl() {
		return CUSTOMER_SERVICE_URL;
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
	 * This is used in order to retrieve a url from the map.
	 * 
	 * @param key - The key for the URL that needs to be retrieved
	 */
	public static String getUrl(final String key) {
		final String url = links.get(key).url;
		if(null != url){
			/**
			 * Note this is hard coded now, but this will be removed in the future
			 */
			return url.replaceAll("http://beta.discoverbank.com", "");
		}else{
			return EMPTY;
		}
	}

	/**
	 * Add new links to the links map or creates a new map if one doesn't
	 * already exist.
	 * 
	 * @param newLinks Map of links returned from the JSON
	 */
	public static void setNewLinks(final Map<String, ReceivedUrl> newLinks) {
		if (newLinks == null) {
			throw new IllegalArgumentException("newLinks cannot be null.");
		}

		links.putAll(newLinks);
	}
}
