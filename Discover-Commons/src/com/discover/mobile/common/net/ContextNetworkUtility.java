package com.discover.mobile.common.net;

import static com.discover.mobile.common.ThreadUtility.isMainThread;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 
 */
final class ContextNetworkUtility {
	
	// This should only ever be mutated on the main thread
	@SuppressLint("UseSparseArrays")
	private static HashMap<Integer, String> keyValuePairs = new HashMap<Integer,String>();
	
	/**
	 * Loads resource only one time  
	 * 
	 * @param currentContext
	 * @param id
	 * @return
	 */
	static synchronized String getStringResource(final Context currentContext, int id) {
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
	
	private ContextNetworkUtility() {
		throw new UnsupportedOperationException("This class is non-instantiable");
	}
	
}
