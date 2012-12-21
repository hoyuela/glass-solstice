package com.discover.mobile.common.push.manage;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostPreferencesDetail implements Serializable{
	
	private static final long serialVersionUID = -5686366915038750507L;

	public static final String OVERRIDE_YES = "T";
	
	public static final String OVERRIDE_NO = "F";
	
	public static final String DEFAULT_OS = "Android";
	
	public static final String DEFAULT_VERSION = "4.0";
	
	public static final String ACCEPT = "Y";
	
	public static final String PENDING = "P";

	@JsonProperty("preferences")
	private List<PreferencesDetail> prefs;
	
	@JsonProperty("vid")
	private String vid;
	
	@JsonProperty("deviceOS")
	private String os;
	
	@JsonProperty("osVersion")
	private String osVersion;
	
	@JsonProperty("deviceID")
	private String deviceID;
	
	@JsonProperty("regStatus")
	private String regStatus;
	
	@JsonProperty("accntOverrideInd")
	private String accntOverrideInd;
	
	@JsonProperty("phoneNumber")
	private String phoneNumber;
	
	@JsonProperty("carrier")
	private String carrier;
	
	public List<PreferencesDetail> getPrefs() {
		return prefs;
	}

	public void setPrefs(List<PreferencesDetail> prefs) {
		this.prefs = prefs;
	}

	public String getVid() {
		return vid;
	}

	public void setVid(String vid) {
		this.vid = vid;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public String getRegStatus() {
		return regStatus;
	}

	public void setRegStatus(String regStatus) {
		this.regStatus = regStatus;
	}

	public String getAccntOverrideInd() {
		return accntOverrideInd;
	}

	public void setAccntOverrideInd(String accntOverrideInd) {
		this.accntOverrideInd = accntOverrideInd;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}
}
