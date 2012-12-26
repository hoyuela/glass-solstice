package com.discover.mobile.common.push.manage;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Main preference object sent to Discover's server for the posting of push settings
 * @author jthornton
 *
 */
public class PostPreferencesDetail implements Serializable{
	
	/**Unique serial identifier*/
	private static final long serialVersionUID = -5686366915038750507L;

	/**Static string representing that the user wants to override the current user's settings*/
	public static final String OVERRIDE_YES = "T";

	/**Static string representing that the user does not want to override the current user's settings*/
	public static final String OVERRIDE_NO = "F";
	
	/**Default operating system*/
	public static final String DEFAULT_OS = "Android";
	
	/**Default android operating system version*/
	public static final String DEFAULT_VERSION = "4.0";
	
	/**String representing the user accepted the T&C*/
	public static final String ACCEPT = "Y";

	/**String representing the user acceptance of the T&C is pending*/
	public static final String PENDING = "P";
	
	public static final String DECLINE = "N";

	/**String representing the user declined the T&C*/
	public static final String DECLINE = "N";

	/**List of preferences that the user wants set*/
	@JsonProperty("preferences")
	public List<PostPrefDetail> prefs;
	
	/**Vendor id of the device*/
	@JsonProperty("vid")
	public String vid;
	
	/**Operating system of the device*/
	@JsonProperty("deviceOS")
	public String os;
	
	/**Version of the operating system of the device*/
	@JsonProperty("osVersion")
	public String osVersion;
	
	/**Unique id of the device*/
	@JsonProperty("deviceID")
	public String deviceID;
	
	/**Registration status of the user*/
	@JsonProperty("regStatus")
	public String regStatus;
	
	/**Value representing if the user wants to override a user's settings*/
	@JsonProperty("accntOverrideInd")
	public String accntOverrideInd;
	
	/**Phone number to send text alerts to*/
	@JsonProperty("phoneNumber")
	public String phoneNumber;
	
	/**Carrier of the phone number to send alerts to*/
	@JsonProperty("carrier")
	public String carrier;
}
