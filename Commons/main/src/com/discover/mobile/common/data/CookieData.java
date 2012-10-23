package com.discover.mobile.common.data;


public final class CookieData {

	private static final CookieData instance = new CookieData();
	
	private String secToken;
	
	public static CookieData getInstance() {
		return instance;
	}

	public String getSecToken() {
		return secToken;
	}
	
	public void setSecToken(String secToken) {
		this.secToken = secToken;
	}
}
