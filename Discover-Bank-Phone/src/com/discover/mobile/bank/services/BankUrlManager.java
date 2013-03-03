package com.discover.mobile.bank.services;

import java.util.HashMap;
import java.util.Map;

import com.discover.mobile.bank.services.payment.PaymentQueryType;
import com.discover.mobile.common.net.json.bank.ReceivedUrl;
import com.google.common.base.Strings;

/**
 * This class is used for adding and getting URL's for bank services. Any bank
 * URL's should go into the card Url Manager class
 * 
 * @author ajleeds
 * 
 */
public class BankUrlManager  {

	private static String BASE_URL = "https://asys.discoverbank.com";
	private static String DISCOVER_STRIPPED_URL = "http://asys.discoverbank.com";
	//private static String BASE_URL = "https://beta.discoverbank.com";
	//private static String DISCOVER_STRIPPED_URL = "http://beta.discoverbank.com";
	//	private static String BASE_URL = "http://solsticebeta.com/Discover/Users/Jon/DiscoverBank"; //Jon Mock Service
	//  private static String BASE_URL = "http://solsticebeta.com/Discover/Users/Erin/";
	//	private static final String BASE_URL = "http://192.168.2.177:8008"; //Henry's Laptop
	//	private static final String BASE_URL = "http://solsticebeta.com/Discover/DiscoverBank";
	//	private static final String BASE_URL = "http://192.168.1.94:8008";
	//	private static String BASE_URL = "http://solsticebeta.com/Discover/Users/Henry/DiscoverBank";
//	private static String BASE_URL = "http://192.168.1.110:8009"; //Jon's Laptop
	//	private static String BASE_URL = "http://solsticebeta.com/Discover/Users/Scott/DiscoverBank"; //Scott's Mock Service
	//	private static String BASE_URL = "http://192.168.2.173:8008";

	public static final double MAX_IDLE_TIME = 900; //900 = 15 min
	public static final String EMPTY = "";
	public static final String SLASH = "/";
	private static final String AUTHENTICATE_CURRENT_CUSTOMER_URL = "/api/customers/current";
	private static final String GET_TOKEN_URL = "/api/auth/token";
	private static final String STRONG_AUTH_URL = "/api/auth/strongauth";
	private static final String CUSTOMER_SERVICE_URL = "/api/customers/current";
	private static final String STATEMENTS_URL = "https://www.discoverbank.com/bankac/loginreg/login ";
	private static final String OPEN_ACCOUNT_URL = "https://www.discover.com/online-banking/savings.html"; 
	private static final String TERMS_AND_CONDITIONS_URL = "/api/content/payments/terms.js";
	private static final String ACCEPT_PAY_BILLS_TERMS_URL = "/api/payments/terms";

	private static Map<String, ReceivedUrl> links = new HashMap<String, ReceivedUrl>();

	/**
	 * Keys for the urls in order to retrieve from the map
	 */
	public static final String ACCOUNT_URL_KEY = "accounts";
	public static final String TRANSFER_URL_KEY = "transfers";
	public static final String PING_URL_KEY = "ping";
	public static final String PAYEES_URL_KEY = "payees";
	public static final String PAYMENTS_URL_KEY = "payments";
	public static final String DEPOSITS_URL_KEY = "deposits";
	public static final String ACCEPT_PAY_BILLS_TERMS_KEY = "billpayterms";

	/**
	 * String used to construct a URL used for deleting. Example /api/auth/token?_method=DELETE
	 */
	public static final String DELETE_METHOD = "?method=DELETE";
	/**
	 * String used to construct a URL used for updating an item. Example /api/auth/token?_method=PUT
	 */
	public static final String PUT_METHOD = "?method=PUT";
	
	/**
	 * Sets the base URL used for all NetworkServiceCall<> objects used for Bank Service API
	 * 
	 * @param value
	 */
	public static void setBaseUrl(final String value ) {
		BASE_URL = value;
	}

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
		return getUrl(links, key);
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

	/**
	 * Method used to clear links cached after a Customer Download.
	 */
	public static void clearLinks() {
		links.clear();
	}

	/**
	 * 
	 * @return Returns the URL for opening a new user account
	 */
	public static String getOpenAccountUrl() {
		return OPEN_ACCOUNT_URL;
	}

	/**
	 * 
	 * @return Returns the URL for going to account statements
	 */
	public static String getStatementsUrl() {
		return STATEMENTS_URL;
	}

	/**
	 * 
	 * @return - the URL for retrieving the terms and conditions content for pay bills.
	 */
	public static String getTermsAndConditionsUrl() {
		return TERMS_AND_CONDITIONS_URL;
	}

	/**
	 * 
	 * @return - the URL for POSTing user acceptance of the terms and conditions for pay bills.
	 */
	public static String getAcceptPayBillsTerms() {
		return ACCEPT_PAY_BILLS_TERMS_URL;
	}

	/**
	 * Utility method used to remove base url from the link
	 * @param link Link with BASE_URL example: http://beta.discoverbank.com/api/token 
	 * 		  where http://beta.discoverbank.com is the base url.
	 * @return Returns Relative path in a url, example  http://beta.discoverbank.com/api/token  would return /api/token
	 */
	public static String getRelativePath(final String link) {
		if( !Strings.isNullOrEmpty(link) ) {
			/**
			 * Note this is hard coded now, but this will be removed in the future
			 */
			return link.replaceAll(DISCOVER_STRIPPED_URL, "");
		}else{
			/**Return a "/" here to trigger an error response from server, since url is not available**/
			return SLASH;
		}
	}

	/**
	 * Utility method used to fetch a link from a hashmap and remove the base url from it.
	 * 
	 * @param urls Hashmap with links
	 * @param key Key used to read a link from the hashmap provided via urls
	 * @return Returns URL link that was stored in hashmap
	 */
	public static String getUrl( final Map<String, ReceivedUrl> urls, final String key) {
		if( urls != null && urls.containsKey(key)  ) {
			final String url = urls.get(key).url;
			return getRelativePath(url);
		} else {
			/**Return a "/" here to trigger an error response from server, since url is not available**/
			return SLASH;
		}
	}

	/**
	 * Method used to construct a query URL string using the Payments URL provided in Customer Download.
	 * 
	 * @param query Value can be eitherSCHEDULED, CANCELLED, COMPLETED, or ALL. Static Strings are found in GetPaymentsServiceCall.
	 * @return Returns a URL to use when using GetPaymentsServiceCall to send a request.
	 */
	public static String generateGetPaymentsUrl(final PaymentQueryType query) {
		return BankUrlManager.getUrl(BankUrlManager.PAYMENTS_URL_KEY) +"?status=" +query;
	}

}
