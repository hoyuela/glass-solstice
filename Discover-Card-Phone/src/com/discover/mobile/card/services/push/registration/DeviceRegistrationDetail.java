package com.discover.mobile.card.services.push.registration;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This is the device registration detail.  The object is posted to Discover's 
 * server when the device needs to to be registered.  It will also will contain
 * the response from the server.
 * 
 *   Enum - U means the Xtify ID aka Vid is present on the server and has been updated.
 *   Enum - C means the Xtify ID aka Vid was not on the server and has been created.
 *   
 * @author jthornton
 *
 */
public class DeviceRegistrationDetail implements Serializable{
	
	private static final long serialVersionUID = -8160152862607049654L;

	/**Vendor Id aka Xtify ID**/
	@JsonProperty("vid")
	public String vid;
	
	/**Operating system, should always be Android*/
	@JsonProperty("deviceOS")
	public String os = "Android"; //$NON-NLS-1$
	
	/**Android operating system, set a default of 4.0 in case it can't be retrieved*/
	@JsonProperty("osVersion")
	public String version = "4.0"; //$NON-NLS-1$
	
	/**Enrollment status (Accepted or declined terms)*/
	@JsonProperty("regStatus")
	public String regStatus;
	
	/**Device unique ID*/
	@JsonProperty("deviceID")
	public String id;
	
	/**Response code from the server*/
	public String httpCode;
	
	/**Messages received from the server*/
	public String resultMsg;

	@JsonProperty("resultCode")
	public DeviceRegStatus vidStatus;
	
	/**Enum representing the status of the device after the server call*/
	public static enum DeviceRegStatus {

		CREATED('C'),
		UPDATED('U');
		
		public final char RESULT_CODE;
		
		private DeviceRegStatus(final char resultCode) {
			RESULT_CODE = resultCode;
		}
		
		@JsonCreator
		public static DeviceRegStatus fromJsonResultCode(final String resultCode) {
			final char resultCodeChar = resultCode.charAt(0);
			for(DeviceRegStatus status : values()) {
				if(status.RESULT_CODE == resultCodeChar)
					return status;
			}
			throw new RuntimeException("Unkown result status code for the device reg status.  Code: " + resultCode); //$NON-NLS-1$
		}
	}
}
