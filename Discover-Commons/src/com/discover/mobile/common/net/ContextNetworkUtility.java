package com.discover.mobile.common.net;

import static com.discover.mobile.common.ThreadUtility.isMainThread;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import com.google.common.base.Strings;

/**
 * 
 */
final public class ContextNetworkUtility {
	
	// This should only ever be mutated on the main thread
	@SuppressLint("UseSparseArrays")
	private static Map<Integer, String> keyValuePairs = new HashMap<Integer,String>();
	
	/**
	 * Loads resource only one time  
	 * 
	 * @param currentContext
	 * @param id
	 * @return
	 */
	static synchronized String getStringResource(final Context currentContext, final int id) {
		checkState(isMainThread(), "getBaseUrl() must be called from the main thread");
		String value = null;
		if( !keyValuePairs.containsKey(id) ) {
			value = currentContext.getString(id);
			keyValuePairs.put(id, value);
			checkNotNull(value, "String resource was null when retrieving from resources:" + id);
		}else{ 
			value = keyValuePairs.get(id);
		}
		return value;
		
	}
	
	static boolean isActiveNetworkConnected(final Context context) {
		final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	}
	
	/**
	 * Method used to fetch a unique identifier for the device. Returns
	 * telephony device id, if not available returns the ANDROID_ID. Note, the
	 * ANDROID_ID is not available
	 * 
	 * @param context
	 * @return
	 */
	public static String getUUID(final Context context) {
		String identifier = "";

		final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		if (telephonyManager != null)
			telephonyManager.getDeviceId();

		if (Strings.isNullOrEmpty(identifier) || identifier.length() == 0)
			identifier = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);

		return identifier;
	}

	private ContextNetworkUtility() {
		throw new UnsupportedOperationException("This class is non-instantiable");
	}
	
}
