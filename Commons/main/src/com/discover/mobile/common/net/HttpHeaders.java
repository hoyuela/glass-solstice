package com.discover.mobile.common.net;

/**
 * Utility class with contains a collection of header name that can found in a HTTP request or response.
 * 
 * @author henryoyuela
 *
 */
public class HttpHeaders {
	public static final String Authorization = "Authorization";
	public static final String XSecToken = "X-Sec-Token";
	public static final String Authentication = "WWW-Authenticate";
	
	
	private HttpHeaders(){
		throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
	}
}
