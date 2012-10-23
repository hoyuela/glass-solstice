package com.discover.mobile.common.net;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;

import com.discover.mobile.commons.R;

final class ContextNetworkUtility {
	
	// This should only ever be mutated on the main thread
	private static String baseUrl;
	
	static String getBaseUrl(final Context currentContext) {
		checkState(isMainThread(), "getBaseUrl() must be called from the main thread");
		
		if(baseUrl == null) {
			baseUrl = currentContext.getString(R.string.base_url);
			checkNotNull(baseUrl, "baseUrl was null when retrieving from resources");
		}
		
		return baseUrl;
	}
	
	static boolean isActiveNetworkConnected(final Context context) {
		final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	}
	
	private static boolean isMainThread() {
		return Thread.currentThread() == Looper.getMainLooper().getThread();
	}
	
	private ContextNetworkUtility() {
		throw new UnsupportedOperationException("This class is non-instantiable");
	}
	
}
