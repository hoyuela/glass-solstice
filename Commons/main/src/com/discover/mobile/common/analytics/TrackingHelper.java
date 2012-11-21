package com.discover.mobile.common.analytics;

import android.app.Activity;

import com.adobe.adms.measurement.ADMS_Measurement;

public final class TrackingHelper {
	
	// FIXME externalize
	private static final String TRACKING_RSID = "discovercardmobiledev"; //$NON-NLS-1$
	private static final String TRACKING_SERVER = "discoverfinancial.d1.sc.omtrdc.net"; //$NON-NLS-1$
	// params for using Bloodhound
//	private static final String TRACKING_RSID = "test";
//	private static final String TRACKING_SERVER = "192.168.4.235:50046";
	
	private static ADMS_Measurement measurement;
	
	public static void startActivity(final Activity activity) {
		configureAppMeasurement(activity);
		measurement.startActivity(activity);
	}
	
	private static void configureAppMeasurement(final Activity activity) {
		if (measurement == null){
			measurement = ADMS_Measurement.sharedInstance(activity);
			measurement.configureMeasurement(TRACKING_RSID, TRACKING_SERVER);
			TrackingHelper.measurement.setSSL(true);  // comment out if using Bloodhound
		}
	}
	
	public static void stopActivity() {
		measurement.stopActivity();
	}
	
	public static void trackPageView(final String pageName) {
		
		//TODO:  THis is here to prevent a null pinter during testing
		if(measurement == null){return;}
		measurement.clearVars();
		measurement.setAppState(pageName);
		measurement.track();
	}
	
	private TrackingHelper() {
		throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
	}
	
}