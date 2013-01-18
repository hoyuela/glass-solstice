package com.discover.mobile.common.auth.bank;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.discover.mobile.common.net.error.AbstractErrorResponse;
import com.discover.mobile.common.net.error.ErrorMessageMapper;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author henryoyuela
 *
 */
public class BankErrorResponse extends AbstractErrorResponse<BankErrorResponse> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5484067099749190270L;

	private static final String TAG = BankErrorResponse.class.getSimpleName();	
	
	/**List of params associated with this preference*/
	@JsonProperty("errors")
	public List<BankError> errors;
	
	@JsonProperty("data")
	public Map<String, String> data = new HashMap<String, String>();
	
	public BankErrorResponse() {
		
	}
	
	@Override
	public ErrorMessageMapper<BankErrorResponse> getMessageMapper() {
		// TODO Auto-generated method stub
		return null;
	}
	
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
}
