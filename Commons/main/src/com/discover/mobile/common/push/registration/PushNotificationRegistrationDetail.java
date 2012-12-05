package com.discover.mobile.common.push.registration;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

public class PushNotificationRegistrationDetail {
	
	private static final String ANDROID_OS = "Android"; //$NON-NLS-1$

	private String vid;
	
	private String deviceOS;
	
	private String osVersion;
	
	private String deviceID;
	
	private String regStatus;
	
	public PushNotificationRegistrationDetail(Context context){
		setOsVersion(ANDROID_OS);
		setOsVersion(Build.VERSION.RELEASE);
		deviceID = getDeviceId(context);
	}
	
	public String getVid() {
		return vid;
	}

	public void setVid(String vid) {
		this.vid = vid;
	}

	public String getDeviceOS() {
		return deviceOS;
	}

	public void setDeviceOS(String deviceOS) {
		this.deviceOS = deviceOS;
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

	public static String getAndroidOs() {
		return ANDROID_OS;
	}

	private String getDeviceId(Context context){
		TelephonyManager tManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		return tManager.getDeviceId();
	}

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}
}

