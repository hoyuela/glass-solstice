package com.discover.mobile.common.push.registration;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DeviceRegistrationDetail implements Serializable{
	
	private static final long serialVersionUID = -8160152862607049654L;

	@JsonProperty("vid")
	public String vid;
	
	@JsonProperty("deviceOS")
	public String os = "Android"; //$NON-NLS-1$
	
	@JsonProperty("osVersion")
	public String version = "4.0"; //$NON-NLS-1$
	
	@JsonProperty("regStatus")
	public String regStatus;
	
	@JsonProperty("deviceID")
	public String id;
	
	public String httpCode;
	
	public String resultMsg;

	@JsonProperty("resultCode")
	public DeviceRegStatus vidStatus;
	
	public static enum DeviceRegStatus {

		CREATED('C'),
		UPDATED('U');
		
		public final char RESULT_CODE;
		
		private DeviceRegStatus(char resultCode) {
			RESULT_CODE = resultCode;
		}
		
		@JsonCreator
		public static DeviceRegStatus fromJsonResultCode(String resultCode) {
			char resultCodeChar = resultCode.charAt(0);
			for(DeviceRegStatus status : values()) {
				if(status.RESULT_CODE == resultCodeChar)
					return status;
			}
			throw new RuntimeException("Unkown result status code for the device registration status.  Code: " + resultCode); //$NON-NLS-1$
		}
	}
}
