package com.discover.mobile.common.net;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;

public final class ServiceCallSessionManager {
	
	private static final CookieManager cookieManager = createAndSetupCookieManager();
	
	private static CookieManager createAndSetupCookieManager() {
		final CookieManager manager = new CookieManager();
		CookieHandler.setDefault(manager);
		return manager;
	}
	
	public static void prepareWithSecurityToken(final HttpURLConnection conn) {
		final String token = getSecurityToken();
		if(isNullOrEmpty(token))
			return;
		
		setTokenHeader(conn, token);
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
	
	public static void destroySession() {
		// TODO
	}
	
	private ServiceCallSessionManager() {
		throw new UnsupportedOperationException("This class is non-instantiable");
	}
	
}
