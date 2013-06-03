package com.discover.mobile.card.fastcheck;

import java.io.Serializable;

public class FastcheckToken implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5560117743257948558L;
	public String deviceToken;

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}
	
	public FastcheckToken(String aToken) {
		deviceToken = aToken;
	}
}
