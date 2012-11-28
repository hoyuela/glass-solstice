package com.discover.mobile.common.push.registration;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PushRegistrationStatusDetail implements Serializable{
	
	private static final long serialVersionUID = -2209678827958715689L;
	
	public String httpCode;
	public String resultMsg;
	
	@JsonProperty("resultCode")
	public VidStatus vidStatus;
	
	public static enum VidStatus {

		ASSOCIATED('T'),
		NOT_ASSOCIATED('O'),
		MISSING('F');
		
		public final char RESULT_CODE;
		
		private VidStatus(char resultCode) {
			RESULT_CODE = resultCode;
		}
		
		@JsonCreator
		public static VidStatus fromJsonResultCode(String resultCode) {
			char resultCodeChar = resultCode.charAt(0);
			for(VidStatus status : values()) {
				if(status.RESULT_CODE == resultCodeChar)
					return status;
			}
			throw new RuntimeException("Unkown vendor id status returned. Result code: " + resultCode); //$NON-NLS-1$
		}
	}
}
