package com.discover.mobile.common.analytics;

import android.app.Activity;

import com.adobe.adms.measurement.ADMS_Measurement;

public class TrackingHelper {

	// params for using Bloodhound
	private static final String TRACKING_RSID = "test";
	private static final String TRACKING_SERVER = "192.168.4.235:50046";
	
//	private static final String TRACKING_RSID = "discovercardmobiledev";
//	private static final String TRACKING_SERVER = "discoverfinancial.d1.sc.omtrdc.net";
	
	private static ADMS_Measurement measurement;
	
	public static void startActivity(final Activity activity) {
		TrackingHelper.configureAppMeasurement(activity);
		TrackingHelper.measurement.startActivity(activity);
	}

	public static void stopActivity() {
		TrackingHelper.measurement.stopActivity();
	}

	public static void configureAppMeasurement(final Activity activity) {
		if (TrackingHelper.measurement == null){
			TrackingHelper.measurement = ADMS_Measurement.sharedInstance(activity);
			TrackingHelper.measurement.configureMeasurement(TRACKING_RSID, TRACKING_SERVER);
//			TrackingHelper.measurement.setSSL(true);  // comment out if using Bloodhound
		}
	}
	
	public static void trackPageView(final String pageName) {
		TrackingHelper.measurement.clearVars();
		TrackingHelper.measurement.setAppState(pageName);
		TrackingHelper.measurement.track();
	}
	
	
	// Examples of Custom Event and AppState Tracking
//	public static void trackCustomEvents (String events) {
//		Hashtable<String, Object> contextData = new Hashtable<String, Object>();
//		contextData.put("contextKey", "value");
//		measurement.trackEvents(events, contextData);
//	}
	
//	public static void trackCustomAppState (String appState) {
//		Hashtable<String, Object> contextData = new Hashtable<String, Object>();
//		contextData.put("contextKey", "value");
//		measurement.trackAppState(appState, contextData);
//	}
	

}