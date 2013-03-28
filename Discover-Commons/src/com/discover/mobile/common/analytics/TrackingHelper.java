package com.discover.mobile.common.analytics;

import java.util.Hashtable;

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
	
	private static final String TRACKING_RSID = "discovercardmobiledev"; //$NON-NLS-1$
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
			measurement.configureMeasurement(TRACKING_RSID, TRACKING_SERVER);
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
	private static final String CONTEXT_PAGE_NAME = "my.eVar22";
	private static final String CONTEXT_RSID = "my.prop26";	
	private static final String CUSTOMER = "Customer";
	private static final String PROSPECT = "Prospect";
	private static final String CONTEXT_APP_NAME = "my.eVar56";
	private static final String APP_NAME =  "DiscoverCard:Native:Android";
	
	/**
	 * Used to track each page view in the app
	 * 
	 * @param pageName - supply the page name according to the specification from Discover
	 */
	public static void trackPageView(final String pageName) {
		if(measurement == null){return;}
		measurement.clearVars();
		final Hashtable<String, Object> contextData = new Hashtable<String, Object>();
		contextData.put(CONTEXT_APP_NAME,APP_NAME);  //$NON-NLS-1$//$NON-NLS-2$
		
		if ( ServiceCallSessionManager.getEDSKey() != null ){ 
			contextData.put(CONTEXT_EDS_PROP, ServiceCallSessionManager.getEDSKey());
			contextData.put(CONTEXT_EDS_VAR, ServiceCallSessionManager.getEDSKey());
			contextData.put(CONTEXT_USER_PROP,CUSTOMER);
			contextData.put(CONTEXT_USER_VAR,CUSTOMER);
			
		}else{ 
			contextData.put(CONTEXT_USER_PROP,PROSPECT);
			contextData.put(CONTEXT_USER_VAR,PROSPECT);
		}

		contextData.put(CONTEXT_PAGE_NAME, pageName);
		contextData.put(CONTEXT_RSID, TRACKING_RSID);
		
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