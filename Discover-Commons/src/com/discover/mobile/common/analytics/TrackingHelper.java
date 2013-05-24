package com.discover.mobile.common.analytics;

import java.util.Hashtable;
import java.util.Map;

import android.app.Activity;

import com.adobe.adms.measurement.ADMS_Measurement;
import com.discover.mobile.common.net.ServiceCallSessionManager;

/**
 * A helper class for adobe site catalyst analytics
 * 
 * @author ekaram
 *
 */
public final class TrackingHelper {

	private static final String CARD_TRACKING_RSID = "discovercardmobileprod"; //$NON-NLS-1$

	private static final String BANK_TRACKING_RSID = "discoverbankmobileprod"; //$NON-NLS-1$

	private static final String TRACKING_SERVER = "smetrics.discover.com"; //$NON-NLS-1$

	private static ADMS_Measurement measurement;

	/**
	 * Sample method provided by adobe; goes unused with our implementation
	 * 
	 * @param activity
	 */
	public static void startActivity(final Activity activity) {
		configureAppMeasurement(activity);
		measurement.startActivity(activity);
	}

	/**
	 * Initializes measurement for site catalyst
	 * 
	 * @param activity
	 */
	private static void configureAppMeasurement(final Activity activity) {
		if (measurement == null){
			measurement = ADMS_Measurement.sharedInstance(activity);
			measurement.setSSL(true);  // comment out if using Bloodhound
			measurement.setDebugLogging(true);
		}
	}

	/**
	 * Sample method provided by adobe; goes unused with our implementation
	 */
	public static void stopActivity() {
		measurement.stopActivity();
	}

	private static final String CONTEXT_EDS_PROP = "my.prop15";
	private static final String CONTEXT_EDS_VAR = "my.eVar15";
	private static final String CONTEXT_USER_PROP = "my.prop6";
	private static final String CONTEXT_USER_VAR = "my.eVar6";
	private static final String CONTEXT_VSTR_ID_PROP = "my.prop 12"; 
	private static final String CONTEXT_VSTR_ID_VAR = "my.eVar 12"; 
	private static final String CONTEXT_PAGE_NAME = "my.eVar22";
	private static final String CONTEXT_RSID = "my.prop26";	
	private static final String CUSTOMER = "Customer";
	private static final String PROSPECT = "Prospect";
	private static final String CONTEXT_APP_NAME = "my.eVar56";
	private static final String APP_NAME =  "DiscoverCard:Native:Android";

	/**Bank Application Name*/
	private static final String BANK_APP_NAME = "DiscoverBank:Native:Android";

	/**SSO detailed tag*/
	public static final String SSO_TAG = "s.eVar43";

	/**Account types Detailed tag*/
	public static final String ACCOUNT_TAG = "s.eVar41";

	/**Single Sign on Value*/
	public static final String SINGLE_SIGN_ON_VALUE = "<Single Sign On>";

	/**
	 * Track a bank page
	 * @param pageName - string value of the page that should be represented
	 */
	public static void trackBankPage(final String pageName){
		trackPageView(pageName, BANK_APP_NAME, null, BANK_TRACKING_RSID);
	}

	/**
	 * Track a page with extra data
	 */
	public static void trackBankPage(final String pageName, final Map<String, Object> extras){
		trackPageView(pageName, BANK_APP_NAME, extras, BANK_TRACKING_RSID);
	}

	/**
	 * Track the card pages
	 * @param pageName - string value of the page to track
	 */
	public static void trackPageView(final String pageName){
		trackPageView(pageName, APP_NAME, null, CARD_TRACKING_RSID);
	}

	/**
	 * Used to track each page view in the app
	 * 
	 * @param pageName - supply the page name according to the specification from Discover
	 */
	private static void trackPageView(final String pageName, final String appName, 
			final Map<String, Object> extras, final String rsid) {
		if(measurement == null){return;}
		measurement.clearVars();
		final Hashtable<String, Object> contextData = new Hashtable<String, Object>();
		contextData.put(CONTEXT_APP_NAME, appName);  //$NON-NLS-1$//$NON-NLS-2$

		if ( ServiceCallSessionManager.getEDSKey() != null ){ 
			contextData.put(CONTEXT_EDS_PROP, ServiceCallSessionManager.getEDSKey());
			contextData.put(CONTEXT_EDS_VAR, ServiceCallSessionManager.getEDSKey());
			contextData.put(CONTEXT_USER_PROP,CUSTOMER);
			contextData.put(CONTEXT_USER_VAR,CUSTOMER);
		}else{ 
			contextData.put(CONTEXT_USER_PROP,PROSPECT);
			contextData.put(CONTEXT_USER_VAR,PROSPECT);
		}

		//Bank specific variables
		if(BANK_APP_NAME.equals(appName) && ServiceCallSessionManager.getEDSKey() != null){
			contextData.put(CONTEXT_VSTR_ID_PROP,CUSTOMER);
			contextData.put(CONTEXT_VSTR_ID_VAR,CUSTOMER);
		}else if(BANK_APP_NAME.equals(appName)){
			contextData.put(CONTEXT_VSTR_ID_PROP,PROSPECT);
			contextData.put(CONTEXT_VSTR_ID_VAR,PROSPECT);
		}

		if(null != extras){
			contextData.putAll(extras);
		}

		contextData.put(CONTEXT_PAGE_NAME, pageName);
		contextData.put(CONTEXT_RSID, rsid);


		measurement.configureMeasurement(rsid, TRACKING_SERVER);
		measurement.setAppState(pageName);
		measurement.track(contextData);
	}

	/**
	 * A private constructor to enforce static use of this class
	 */
	private TrackingHelper() {
		throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
	}

}