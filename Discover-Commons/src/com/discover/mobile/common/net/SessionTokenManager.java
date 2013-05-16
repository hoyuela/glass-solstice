package com.discover.mobile.common.net;

/**
 * Class used to manage the token provided in the response to
 * a NetworkServiceCall<> to the Bank web-serivce API found at /api/auth/token. 
 * After the token is extracted from the JSON response it is stored 
 * in this class which follows a Singleton design pattern. The class
 * used to invoke the web-service API is CreateBankLoginCall.
 * 
 * Any Bank API called subsequently, after fetching a token, will required 
 * to use the token in its HTTP Authorization header. In order to this
 * the NetworkServiceCall<> used to invoke the web-service api will have to 
 * set its params requiresSessionForRequest to true. The NetworkServiceCall<>
 * will then know to fetch the token and add it to its HTTP Authorization header
 * prior to sending the HTTP request to invoke the web-service api.
 * 
 * Example of the authorization header schema, where after BankBasic is the
 * token value:
 * 
 * Authorization: BankBasic QWxhZGRpbjpvcGVuIHNlc2FtZQ== 
 * 
 * @author henryoyuela
 *
 */
public final class SessionTokenManager {
	private static String mToken = null;
	
	/**
	 * Follows a singleton design pattern. Therefore this constructor is not used and made private
	 */
	private SessionTokenManager() {
		throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
	}
	
	/**
	 * Used to set the token used for invoking Bank web-service API's via an instance of
	 * NetworkServiceCall<>. This API is meant to be used only by CreateBankLoginCall
	 * 
	 * @param token Token value received from a response to the web-service API /api/auth/token.
	 */
	public static void setToken(String token) {
		mToken = token;
	}
	
	/**
	 * 
	 * @return Returns the token cached by CreateBankLoginCall, after receiving a successful response 
	 * 		   from the Bank web-service API /api/auth/token.
	 */
	public static String getToken() {
		return mToken;
	}
	
	/**
	 * Clears the Bank token to avoid future use in any Bank related NetworkServiceCall<>
	 */
	public static void clearToken() {
		mToken = null;
	}
	
}
