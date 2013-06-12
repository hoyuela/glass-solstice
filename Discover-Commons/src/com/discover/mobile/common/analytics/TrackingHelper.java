package com.discover.mobile.common.analytics;

import java.util.Hashtable;
import java.util.Map;

import android.app.Activity;

import com.adobe.adms.measurement.ADMS_Measurement;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.R;
import com.discover.mobile.common.net.ServiceCallSessionManager;

/**
 * A helper class for adobe site catalyst analytics
 * 
 * @author ekaram
 *
 */
public final class TrackingHelper {

	private static final String CARD_TRACKING_RSID = DiscoverActivityManager.getString(R.string.card_tracking_rsid);

	private static final String BANK_TRACKING_RSID = DiscoverActivityManager.getString(R.string.bank_tracking_rsid);

	private static final String TRACKING_SERVER = DiscoverActivityManager.getString(R.string.tracking_server);

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
			measurement.setDebugLogging(false);
		}
	}

	/**
	 * Sample method provided by adobe; goes unused with our implementation
	 */
	public static void stopActivity() {
		measurement.stopActivity();
	}

	private static final String CONTEXT_EDS_PROP = DiscoverActivityManager.getString(R.string.context_eds_prop);
	private static final String CONTEXT_EDS_VAR = DiscoverActivityManager.getString(R.string.context_eds_var);
	private static final String CONTEXT_USER_PROP = DiscoverActivityManager.getString(R.string.context_user_prop);
	private static final String CONTEXT_USER_VAR = DiscoverActivityManager.getString(R.string.context_user_var);
	private static final String CONTEXT_VSTR_ID_PROP = DiscoverActivityManager.getString(R.string.context_vstr_id_prop); 
	private static final String CONTEXT_VSTR_ID_VAR = DiscoverActivityManager.getString(R.string.context_vstr_id_var); 
	private static final String CONTEXT_PAGE_NAME = DiscoverActivityManager.getString(R.string.context_page_name);
	private static final String CONTEXT_RSID = DiscoverActivityManager.getString(R.string.context_rsid);	
	private static final String CUSTOMER = DiscoverActivityManager.getString(R.string.customer);
	private static final String PROSPECT = DiscoverActivityManager.getString(R.string.prospect);
	private static final String CONTEXT_APP_NAME = DiscoverActivityManager.getString(R.string.context_app_name);
	private static final String APP_NAME =  DiscoverActivityManager.getString(R.string.discover_app_name);

	/**Bank Application Name*/
	private static final String BANK_APP_NAME = DiscoverActivityManager.getString(R.string.bank_app_name);

	/**SSO detailed tag*/
	public static final String SSO_TAG = DiscoverActivityManager.getString(R.string.sso_tag);

	/**Account types Detailed tag*/
	public static final String ACCOUNT_TAG = DiscoverActivityManager.getString(R.string.account_tag);

	/**Single Sign on Value*/
	public static final String SINGLE_SIGN_ON_VALUE = DiscoverActivityManager.getString(R.string.single_sign_on_value);

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
		contextData.put(CONTEXT_APP_NAME, appName);  

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