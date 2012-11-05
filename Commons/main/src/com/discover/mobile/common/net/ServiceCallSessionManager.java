package com.discover.mobile.common.net;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;

final class ServiceCallSessionManager {
	
	private static final CookieManager cookieManager = createAndSetupCookieManager();
	
	private static CookieManager createAndSetupCookieManager() {
		final CookieManager manager = new CookieManager();
		CookieHandler.setDefault(manager);
		return manager;
	}
	
	static void clearSession() {
		// TODO determine if this is really thread-safe (and safe if
		// we have another long-running, concurrent network call)
		cookieManager.getCookieStore().removeAll();
	}
	
	static boolean prepareWithSecurityToken(final HttpURLConnection conn) {
		final String token = getSecurityToken();
		if(isNullOrEmpty(token))
			return false;
		
		setTokenHeader(conn, token);
		return true;
	}
	
	private static String getSecurityToken() {
		// CookieManager is assumed to bring its own thread safety
		for(final HttpCookie cookie : cookieManager.getCookieStore().getCookies()) {			
			if("sectoken".equalsIgnoreCase(cookie.getName()))
				return cookie.getValue();
		}
		
		return null;
	}
	
	private static void setTokenHeader(final HttpURLConnection conn, final String token) {
		conn.addRequestProperty("X-Sec-Token", token);
	}
	
	private ServiceCallSessionManager() {
		throw new UnsupportedOperationException("This class is non-instantiable");
	}
	
}
