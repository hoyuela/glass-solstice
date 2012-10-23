package com.discover.mobile.common.auth;

import java.io.InputStream;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import com.discover.mobile.common.data.CookieData;
import com.discover.mobile.common.net.AsyncCallback;
import com.discover.mobile.common.net.HttpMethod;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.StrongReferenceHandler;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;

public class AuthenticateCall extends NetworkServiceCall<Object> {
	
	private static String TAG = AuthenticateCall.class.getSimpleName();
	
	// TEMP
	private static final ServiceCallParams STANDARD_PARAMS = new ServiceCallParams() {{
		method = HttpMethod.GET;
		path = "/cardsvcs/acs/acct/v1/account";
		
		final String concatenatedCreds = "uid4545" + ": :" + "ccccc";
		final String dcrdBasicCreds = Base64.encodeToString(concatenatedCreds.getBytes(), Base64.DEFAULT);
		headers = ImmutableMap.<String,String>builder()
				.put("Authorization", "DCRDBasic " + dcrdBasicCreds).build();
	}};
	
	// TEMP
	private final Handler handler;

	public AuthenticateCall(final Context context, final AsyncCallback<Object> callback) {
		super(context, STANDARD_PARAMS);
		
		handler = new StrongReferenceHandler<Object>(callback);
	}

	@Override
	protected Handler getHandler() {
		// TEMP
		return handler;
		
		// TODO
	}

	@Override
	protected Object parseResponse(final int status, final Map<String,List<String>> headers, final InputStream body) {
		// TEMP
		final CookieManager manager = new CookieManager();
		try {
			manager.put(new URI("https://mst0.m.discovercard.com/cardsvcs/acs/acct/v1/account"), headers);
		} catch(final Exception e) {
			throw Throwables.propagate(e);
		}
		
		saveCookieInfo(manager);
		// TEMP
		return null;
		
		// TODO
	}
	
	private static void saveCookieInfo(CookieManager cookieManager) {
		for(final HttpCookie cookie : cookieManager.getCookieStore().getCookies()) {			
			if(cookie.getName().equalsIgnoreCase("sectoken")) {
				String tempFormattedToken = formatCookieData(cookie);
				CookieData.getInstance().setSecToken(tempFormattedToken);
			}
		}
	}
	
	private static String formatCookieData(HttpCookie cookie) {
		String[] separatedData = cookie.toString().split("=", 2);
		
		Log.d("tag", "Length: " + separatedData.length);
		Log.d("tag", "SecToken set: " + separatedData[1]);
		
		if(separatedData[1] != null)
			return separatedData[1];
		
		Log.e(TAG, "Cookie could not be formatted.  No token set.");
		return null;
	}
}
