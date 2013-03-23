package com.discover.mobile.common.net.error.bank;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.discover.mobile.common.net.error.AbstractErrorResponse;
import com.discover.mobile.common.net.error.ErrorMessageMapper;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This is a class is used for mapping the JSON that is returned for errors on Bank service calls.
 * 
 * Example of JSON:
 * {
 *	    "errors" : [
 *	                {
 *	                "name" : "LastLoginAttempt",
 *	                "code" : "Auth.Login.LastAttempt",
 *	                "message" : "We're sorry, we still do not recognize the information you entered. For security reasons, you will be allowed only one more login attempt."
 *	                }
 *	                ],
 *	    "data" : {
 *	        "username" : "test001"
 *	    }
 *	}
 * 
 * @author henryoyuela
 *
 */
public class BankErrorResponse extends AbstractErrorResponse<BankErrorResponse> {
	/**
	 * Auto-generated serial UID which is used to serialize and de-serialize BankErrorResponse objects
	 */
	private static final long serialVersionUID = -5484067099749190270L;
	/**
	 * Used for print logs from this class into Android logcat
	 */
	private static final String TAG = BankErrorResponse.class.getSimpleName();	
	/**
	 * Contains list of errors found in a JSON Error Response to a Bank related NetworkServiceCall<>
	 **/
	@JsonProperty("errors")
	public List<BankError> errors;
	/**
	 * Contains the data found in a JSON Error Response to a Bank related NetworkServiceCall<>
	 */
	@JsonProperty("data")
	public Map<String, String> data = new HashMap<String, String>();

	
	/**
	 * Not Used in this class
	 */
	@Override
	public ErrorMessageMapper<BankErrorResponse> getMessageMapper() {
		return null;
	}
	
	/**
	 * @return Returns the error code found in the JSON error response
	 */
	public String getErrorCode() {
		String errorCode = null;
		
		if( errors != null && !errors.isEmpty() ) {
			errorCode = errors.get(0).code;	
		} else {
			if( Log.isLoggable(TAG, Log.ERROR)) {
				Log.e(TAG, "Unkown Bank Response Error");
			}
		}
		
		return errorCode;
	}
	/**
	 * @return Returns the name used to decribe the error found in the JSON error response for the first error object in the list
	 */
	public String getErrorName() {
		String errorName = null;
		
		if( errors != null && !errors.isEmpty() ) {
			errorName = errors.get(0).code;	
		} else {
			if( Log.isLoggable(TAG, Log.ERROR)) {
				Log.e(TAG, "Unkown Bank Response Error");
			}
		}
		
		return errorName;
	}
	/**
	 * @return Returns the Message found in the JSON error response for the first error object in the list
	 */
	public String getErrorMessage() {
		String errorMessage = null;
		
		if( errors != null && !errors.isEmpty() ) {
			errorMessage = errors.get(0).message;	
		} else {
			if( Log.isLoggable(TAG, Log.ERROR)) {
				Log.e(TAG, "Unkown Bank Response Error");
			}
		}
		
		return errorMessage;
	}
	/**
	 * @return Returns the Phone Number found in the JSON error response for the first error object in the list
	 */
	public String getPhoneNumber() {
		String phone = null;
		
		if( errors != null && !errors.isEmpty() ) {
			phone = errors.get(0).phone;	
		} else {
			if( Log.isLoggable(TAG, Log.ERROR)) {
				Log.e(TAG, "Unkown Bank Response Error");
			}
		}
		
		return phone;
	}
	/**
	 * @return Returns the Title found in the JSON error response for the first error object in the list
	 */
	public String getTitle() {
		String title = null;
		
		if( errors != null && !errors.isEmpty() ) {
			title = errors.get(0).title;	
		} else {
			if( Log.isLoggable(TAG, Log.ERROR)) {
				Log.e(TAG, "Unkown Bank Response Error");
			}
		}
		
		return title;
	}
	
	/**
	 * 
	 * @param key 
	 * @return Returns the string with the key
	 */
	public String getDataValue(final String key){
		
		return data.get(key);
	}
}
