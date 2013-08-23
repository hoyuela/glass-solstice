package com.discover.mobile.card.fastcheck;

import java.io.Serializable;

public class FastcheckToken implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5560117743257948558L;
	public String deviceToken;
	public boolean getCardImage;

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}
	
	public FastcheckToken(String aToken) {
		deviceToken = aToken;
		getCardImage=true;
	}

	public boolean isGetCardImage() {
		return getCardImage;
	}

	public void setGetCardImage(boolean getCardImage) {
		this.getCardImage = getCardImage;
	}
}
