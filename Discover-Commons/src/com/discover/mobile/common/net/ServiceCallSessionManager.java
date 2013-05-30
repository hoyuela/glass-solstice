package com.discover.mobile.common.net;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;

import com.discover.mobile.common.AccountType;
import com.discover.mobile.common.Globals;

/**
 * ServiceCallSessionManager is used to manage the token for a session with a Card or Bank service.
 * The token is provided by the respective service in the response to an authentication request. Each
 * service has a different web-service API for authenticating and have different mechanism for providing
 * and using the token. The Card service uses a token provided via a cookie and uses the Android
 * CookieManager class to fetch the token. The Bank service API receives it token in the JSON response
 * to its authentication web-service API (see CreateBankLoginCall). 
 * 
 * @author henryoyuela
 *
 */
public final class ServiceCallSessionManager {
	
	private static final CookieManager cookieManager = createAndSetupCookieManager();
	
	/**
	 * Constant to hold the key value for the eds cookie
	 * The eds cookie holds a 6 digit number alternative to a user id
	 */
	private static final String EDS_COOKIE = "dfsedskey";
	
	/**
	 * Initializes an instance of the  Android CookieManager
	 * 
	 * @return
	 */
	private static CookieManager createAndSetupCookieManager() {
		final CookieManager manager = new CookieManager();
		CookieHandler.setDefault(manager);

		return manager;
	}
	
	/**
	 * Used to clear all tokens including both CARD and BANK
	 */
	static void clearSession() {
		// TODO determine if this is really thread-safe (and safe if
		// we have another long-running, concurrent network call)
		cookieManager.getCookieStore().removeAll();
		SessionTokenManager.clearToken();
	}
	
	/**
	 * Called by a NetworkServiceCall prior to sending a request that requires
	 * the token to be part of its HTTP headers.
	 * 
	 * @param conn Reference to the HttpURLConnection used by the NetworkServiceCall<> 
	 * 				to invoke a web-service API
	 * @return True if token is set successfully, false otherwise.
	 */
	static boolean prepareWithSecurityToken(final HttpURLConnection conn) {
		final String token = getSecurityToken();
		if(isNullOrEmpty(token)){
			return false;
		}
		
		setTokenHeader(conn, token);
		return true;
	}
	
	/**
	 * Returns the token previously cached after a successfull authentication with the web-service server.
	 * 
	 * @return Token value in a String Object
	 */
	private static String getSecurityToken() {
		String token = null;
		
		if( AccountType.BANK_ACCOUNT == Globals.getCurrentAccount() ) {
			token = SessionTokenManager.getToken();
		} else {
			// CookieManager is assumed to bring its own thread safety
			for(final HttpCookie cookie : cookieManager.getCookieStore().getCookies()) {			
				if("sectoken".equalsIgnoreCase(cookie.getName())){
					token = cookie.getValue();
				}
			}
		}
		
		return token;
	}
	
	
	
	/**
	 * EDS Key - unique alternative for the user id
	 * 
	 * @return  Returns null if not available
	 */
	public static String getEDSKey() {
		
		String edsKey = null;
		
		// CookieManager is assumed to bring its own thread safety
		for(final HttpCookie cookie : cookieManager.getCookieStore().getCookies()) {	
			if(EDS_COOKIE.equalsIgnoreCase(cookie.getName())){
				edsKey = cookie.getValue();
				break;
			}
		}
		
		
		return edsKey;
	}
	/**
	 * Used to include the currently cached token in the HTTP header of an HttpURLConnection. 
	 * For Card web-service request the token is stored in the X-Sec-Token HTTP Header and for Bank 
	 * web-service request it is stored in the Authorization HTTP header.
	 * 
	 * @param conn Reference to an HttpURLConnection which will be used to send an HTTP request
	 * @param token Reference to a token value to be stored within the HTTP Headers. 
	 */
	private static void setTokenHeader(final HttpURLConnection conn, final String token) {
		if( AccountType.BANK_ACCOUNT == Globals.getCurrentAccount() ) {
			conn.addRequestProperty(HttpHeaders.Authorization, token);
		} else {
			conn.addRequestProperty(HttpHeaders.XSecToken, token);
		}
	}
	
	/**
	 * Not Used as it follows the singleton design pattern
	 */
	private ServiceCallSessionManager() {
		throw new UnsupportedOperationException("This class is non-instantiable");
	}
	
	//Implemented by Cognizant for Provide Feedback functionality 
    /**
     * Returns the V1st Cookie previously cached after a successful PreAuth call.
     * 
     * @return V1st Cookie value in a String Object
     */
    public static String getV1stCookie() {
        String token = null;
        
        // CookieManager is assumed to bring its own thread safety
        for(final HttpCookie cookie : cookieManager.getCookieStore().getCookies()) {            
            if("v1st".equalsIgnoreCase(cookie.getName())){
                token = cookie.getValue();
            }
        }
        
        return token;
    }
}
