package com.discover.mobile.bank.services;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.json.ReceivedUrl;
import com.discover.mobile.bank.services.payment.PaymentQueryType;
import com.discover.mobile.bank.services.transfer.OrderBy;
import com.discover.mobile.bank.services.transfer.SortDirection;
import com.discover.mobile.bank.services.transfer.TransferType;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.utils.StringUtility;
import com.google.common.base.Strings;

/**
 * This class is used for adding and getting URL's for bank services. Any bank
 * URL's should go into the card Url Manager class
 * 
 * @author ajleeds
 * 
 */
public final class BankUrlManager  {

	private static String baseURL = DiscoverActivityManager.getString(R.string.bank_base_url);
	private static final String DISCOVER_STRIPPED_URL = DiscoverActivityManager.getString(R.string.bank_stripped_url);

	// 600 secs = 10 min
	public static final double MAX_IDLE_TIME = 600; 

	private static Map<String, ReceivedUrl> links = new HashMap<String, ReceivedUrl>();

	/**
	 * Keys for the urls in order to retrieve from the map
	 */
	public static final String ACCOUNT_URL_KEY = "accounts";
	public static final String TRANSFER_URL_KEY = "transfers";
	public static final String EXTERNAL_TRANSFER_ACCOUNTS_URL_KEY = "transferAccounts";
	public static final String PING_URL_KEY = "ping";
	public static final String PAYEES_URL_KEY = "payees";
	public static final String PAYMENTS_URL_KEY = "payments";
	public static final String DEPOSITS_URL_KEY = "deposits";
	public static final String ACCEPT_PAY_BILLS_TERMS_KEY = "billpayterms";
	public static final String BANK_HOLIDAYS_URL_KEY = "bank-holidays";
	public static final String CUSTOMER_URL_KEY = "customer";
	public static final String PRIVACY_POLICY_KEY = "privacy-policy";
	public static final String TERMS_OF_USE = "terms-of-use";

	/**
	 * String used to construct a URL used for deleting. Example /api/auth/token?_method=DELETE
	 */
	public static final String DELETE_METHOD = "?_method=DELETE";
	/**
	 * String used to construct a URL used for updating an item. Example /api/auth/token?_method=PUT
	 */
	public static final String PUT_METHOD = "?_method=PUT";


	/**
	 * String used to construct a URL for specifying deleting a once time transfer
	 */
	public static final String CANCEL_SCHEDULED_TRANSFER = "?type=CS";

	/**
	 * String used to construct a URL for specifying deleting all remaining transfer
	 */
	public static final String CANCEL_ALL_REMAINING_TRANSERS = "?type=CAR";

	/**
	 * String used to construct a URL for specifying deleting the next transfer
	 */
	public static final String CANCEL_NEXT_TRANSFER = "?type=CNR";

	/**
	 * This is a utility class and should not have a public or default constructor.
	 */
	private BankUrlManager() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Sets the base URL used for all NetworkServiceCall<> objects used for Bank Service API
	 * 
	 * @param value
	 */
	public static void setBaseUrl(final String value ) {
		baseURL = value;
	}

	/**
	 * @return the baseUrl
	 */
	public static String getBaseUrl() {
		return baseURL;
	}

	/**
	 * @return the authenticateCurrentCustomerUrl
	 */
	public static String getAuthenticateCurrentCustomerUrl() {
		return DiscoverActivityManager.getString(R.string.api_customers_current);
	}

	/**
	 * @return the customerServiceUrl
	 */
	public static String getCustomerServiceUrl() {
		/** Attempt to fetch URL from hash table*/
		String url = getUrl(CUSTOMER_URL_KEY);

		/**Use Default hard coded URL if not found*/
		if( Strings.isNullOrEmpty(url) || StringUtility.SLASH.equals(url)) {
			url = DiscoverActivityManager.getString(R.string.api_customers_current);
		}

		return url;
	}

	/**
	 * @return The URL link to be used for getting bank holidays from the server
	 */
	public static String getBankHolidaysUrl() {
		return getUrl(BANK_HOLIDAYS_URL_KEY);
	}

	/**
	 * @return The URL link to be used for getting Privacy & Terms from the server
	 */
	public static String getPrivacyTermsUrl() {
		return resolveTermsKeyToURL(PRIVACY_POLICY_KEY);
	}

	/**
	 * @return The URL link to be used for getting Terms of Use from the server
	 */
	public static String getTermsOfUse() {
		return resolveTermsKeyToURL(TERMS_OF_USE);
	}

	/**
	 * 
	 * @param link a String representation of a URL that should end with HTML or HTM
	 * @return
	 */
	public static boolean isValidContentLink(final String link) {
		return !Strings.isNullOrEmpty(link) && looksLikeContent(link);
	}

	/**
	 * 
	 * @param testableLink a URL that should be checked if it could contain terms content.
	 * @return if the link is html/htm content.
	 */
	private static boolean looksLikeContent(final String link) {
		return link != null && (link.endsWith("html") || link.endsWith("htm"));
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	private static String resolveTermsKeyToURL(final String key) {
		final StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(getBaseUrl());

		final String testableLink = getUrl(key);
		if(isValidContentLink(testableLink)) {
			urlBuilder.append(testableLink);
		}else {
			urlBuilder.append(getFailSafeUrlForKey(key));
		}

		return urlBuilder.toString();
	}

	/**
	 * 
	 * @param key a key which has a fail safe URL defined.
	 * @return a fail safe URL. Or empty if none are found.
	 */
	private static String getFailSafeUrlForKey(final String key) {
		String url = "";

		if(TERMS_OF_USE.equalsIgnoreCase(key)) {
			url = DiscoverActivityManager.getString(R.string.terms_fail_safe_url);
		}else if(PRIVACY_POLICY_KEY.equalsIgnoreCase(key)) {
			url = DiscoverActivityManager.getString(R.string.privacy_policy_fail_safe_url);
		}

		return url;
	}

	/**
	 * @return the getTokenUrl
	 */
	public static String getGetTokenUrl() {
		return DiscoverActivityManager.getString(R.string.get_token_url);
	}

	/**
	 * @return the url for posting a token payload
	 */
	public static String getSSOTokenUrl() {
		return DiscoverActivityManager.getString(R.string.get_sso_token_url);
	}

	/**
	 * @return the strongAuthUrl
	 */
	public static String getStrongAuthUrl() {
		return DiscoverActivityManager.getString(R.string.strong_auth_url);
	}

	/**
	 * @return Returns the the string for downloading API URLs from the Bank Server
	 */
	public static String getApiUrl() {
		return DiscoverActivityManager.getString(R.string.api_url);
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
		final Map<String, ReceivedUrl> persistentLinks = new HashMap<String, ReceivedUrl>();

		final Iterator<Map.Entry<String, ReceivedUrl>> it = links.entrySet().iterator();
		while (it.hasNext()) {
			final Map.Entry<String, ReceivedUrl> pairs = it.next();

			if( pairs.getKey().equals(BANK_HOLIDAYS_URL_KEY) ||
					pairs.getKey().equals(CUSTOMER_URL_KEY) ||
					pairs.getKey().equals(PRIVACY_POLICY_KEY) ||
					pairs.getKey().equals(TERMS_OF_USE)) {

				persistentLinks.put(pairs.getKey(), pairs.getValue());

			}
		}

		links.clear();

		links.putAll(persistentLinks);
	}

	public static int getLinksCount() {
		return links.size();
	}

	public static Map<String, ReceivedUrl> getLinks() {
		return links;
	}

	/**
	 * 
	 * @return Returns the URL for opening a new user account
	 */
	public static String getOpenAccountUrl() {
		return DiscoverActivityManager.getString(R.string.open_account_url);
	}

	/**
	 * @return Returns the URL for providing feedback
	 */
	public static String getProvideFeedbackUrl() {
		return DiscoverActivityManager.getString(R.string.feedback_url);
	}

	/**
	 * @return Returns the URL for providing feedback
	 */
	public static String getCardProvideFeedbackUrl() {
		return DiscoverActivityManager.getString(R.string.card_provide_feedback_url);
	}
	
	/**
	 * @return Returns the URL for more FAQs
	 */
	public static String getCardMoreFAQsUrl() {
		return DiscoverActivityManager.getString(R.string.card_more_faq_url);
	}
	
	/**
	 * 
	 * @return Returns the URL for going to account statements
	 */
	public static String getStatementsUrl() {
		return DiscoverActivityManager.getString(R.string.bank_login_url);
	}

	public static String getManageExternalAccountsUrl() {
		return DiscoverActivityManager.getString(R.string.bank_login_url);
	}

	/**
	 * 
	 * @return - the URL for retrieving the terms and conditions content for pay bills.
	 */
	public static String getTermsAndConditionsUrl() {
		return DiscoverActivityManager.getString(R.string.terms_and_conditions_url);
	}

	/**
	 * 
	 * @return - the URL for POSTing user acceptance of the terms and conditions for pay bills.
	 */
	public static String getAcceptPayBillsTerms() {
		return DiscoverActivityManager.getString(R.string.accept_pay_bills_terms_url);
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
			return StringUtility.SLASH;
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
			return StringUtility.SLASH;
		}
	}

	/**
	 * Method used to construct a query URL string using the Payments URL provided in Customer Download.
	 * 
	 * @param query Value can be eitherSCHEDULED, CANCELLED, COMPLETED, or ALL. Static Strings are found
	 * in GetPaymentsServiceCall.
	 * @return Returns a URL to use when using GetPaymentsServiceCall to send a request.
	 */
	public static String generateGetPaymentsUrl(final PaymentQueryType query) {
		return BankUrlManager.getUrl(BankUrlManager.PAYMENTS_URL_KEY) +"?status=" +query;
	}
	
	
	/**
	 * Gets the scheduled, descending, date sorted transfers.
	 * 
	 * @return a query url
	 */
	public static String generateGetTransfersUrl() {
		return generateGetTransfersUrl(TransferType.Scheduled, OrderBy.Date, SortDirection.Descending);
	}
	
	/**
	 * 
	 * @param type
	 * @return a query url
	 */
	public static String generateGetTransfersUrl(final TransferType type) {
		String getTransfersUrl = StringUtility.EMPTY;
		if(type == TransferType.Scheduled) {
			getTransfersUrl = generateGetTransfersUrl(type, OrderBy.Date, SortDirection.Ascending);
		}else {
			getTransfersUrl = generateGetTransfersUrl(type, OrderBy.Date, SortDirection.Descending);
		}
		return getTransfersUrl;
	}
	
	/**
	 * 
	 * @param type
	 * @param order
	 * @param direction
	 * @return a query url
	 */
	public static String generateGetTransfersUrl(final TransferType type, 
													final OrderBy order, 
													final SortDirection direction) {
		
		final StringBuilder urlBuilder = new StringBuilder();
		
		urlBuilder.append(BankUrlManager.getUrl(BankUrlManager.TRANSFER_URL_KEY));
		urlBuilder.append("?view=");
		urlBuilder.append(type.getFormattedQueryParam());
		urlBuilder.append("&orderby=");
		urlBuilder.append(order.getFormattedQueryParam());
		urlBuilder.append("&dir=");
		urlBuilder.append(direction.getFormattedQueryParam());
		return urlBuilder.toString();
	}

	/**
	 * @return the atmLocatorUrl
	 */
	public static String getAtmLocatorUrl() {
		return DiscoverActivityManager.getString(R.string.atm_locator_url);
	}

	/**
	 * @return the atmDirectionsBaseUrl
	 */
	public static String getAtmDirectionsBaseUrl() {
		return DiscoverActivityManager.getString(R.string.atm_directions_base_url);
	}

	/**
	 * @return the url to refresh a bank session.
	 */
	public static String getRefreshSessionUrl() {
		return DiscoverActivityManager.getString(R.string.refresh_url);
	}

	/**
	 * @return the atmAddressToLocationBaseUrl
	 */
	public static String getAtmAddressToLocationBaseUrl() {
		return DiscoverActivityManager.getString(R.string.atm_address_to_location_base_url);
	}

	/**
	 * @return the cardGoogleTermsUrl
	 */
	public static String getCardGoogleTermsUrl() {
		return DiscoverActivityManager.getString(R.string.card_google_terms_url);
	}
	
	/**
	 * @return the bankGoogleTermsUrl
	 */
	public static String getBankGoogleTermsUrl() {
		return DiscoverActivityManager.getString(R.string.bank_google_terms_url);
	}
	
	/**
	 * @return the bankGoogleReportUrl
	 */
	public static String getBankGoogleReportUrl() {
		return DiscoverActivityManager.getString(R.string.bank_google_report_url);
	}
	
	public static String getBankGoogleMapUrl() {
		return DiscoverActivityManager.getString(R.string.bank_google_map_url);
	}
	
	public static String getBankAtmReportUrl() {
		return DiscoverActivityManager.getString(R.string.atm_report_url);
	}

	/**
	 * 
	 * @return the street view url
	 */
	public static String getStreetViewUrl() {
		return baseURL + DiscoverActivityManager.getString(R.string.street_view_url);
	}

	/**
	 * 
	 * @return the pay bills terms and conditions url
	 */	
	public static String getPayBillsTermsUrl() {
		return baseURL + DiscoverActivityManager.getString(R.string.pay_bills_terms_url);
	}

}
