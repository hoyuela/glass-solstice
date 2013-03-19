package com.discover.mobile.common.net.error.bank;

import java.util.HashMap;
import java.util.Map;

import com.discover.mobile.common.net.error.AbstractErrorResponse;
import com.discover.mobile.common.net.error.ErrorMessageMapper;
import com.discover.mobile.common.net.json.bank.ReceivedUrl;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Used to map JSON for Bank Login response related to SSO Users.
 * 
 * 401 Not Authorized
 * WWW-Authenticate: CardAuth realm="www.discovercard.com"
 * {
 *	"value": "X5jK0jqYMvIhgkb2B4gGa2phipI="
 *	"hashedValue": "437fcc12dfdfbb3bc4f58af7a34d0556"
 *	"links" : {
 *		"self" : {
 *			"ref" : "/api/auth/token",
 *			"allowed" : [ "POST", "DELETE" ]
 *		}
 *	}
 *}
 * @author sam
 *
 */
public class BankErrorSSOResponse extends AbstractErrorResponse<BankErrorSSOResponse> {
	/**
	 * Auto-generated serial UID which is used to serialize and de-serialize BankErrorResponse objects
	 */
	private static final long serialVersionUID = -2162751436049892434L;
	/**
	 * Used for print logs from this class into Android logcat
	 */
	private static final String TAG = BankErrorSSOResponse.class.getSimpleName();	
	/**
	 * Contains value associated with sso user.
	 **/
	@JsonProperty("value")
	public String token; 
	/**
	 * Hashed value associated with sso user.
	 */
	@JsonProperty("hashedValue")
	public String hashedValue;
	/**
	 * Returns links to actionable services 
	 */
	@JsonProperty("links")
	public Map<String, ReceivedUrl> links = new HashMap<String, ReceivedUrl>();
	
	
	/**
	 * Not Used in this class
	 */
	@Override
	public ErrorMessageMapper<BankErrorSSOResponse> getMessageMapper() {
		return null;
	}
}
