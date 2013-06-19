package com.discover.mobile.card.common.net.service;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.HashMap;

/**
 * This class is web service request model class.
 * 
 * @author Anuja Deshpande
 */

public final class WSRequest {
	private String strURL;
	private String strMethodType;
	private byte[] btInput;
	private HashMap<String, String> hmHeaderValues;
	private boolean isFrequentCaller;
	private String username;
	private String password;
	private int connectionTimeOut = 10000;
	private int connectionReadTimeOut = 10000;

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	

	public WSRequest() {
		hmHeaderValues = new HashMap<String, String>();
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		hmHeaderValues.clear();
		hmHeaderValues = null;
	}

	public void setCookieHander() {
		CookieHandler.setDefault(new CookieManager());
		((CookieManager) CookieHandler.getDefault())
				.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return strURL;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(final String url) {
		strURL = url;
	}

	/**
	 * @return the methodtype
	 */
	public String getMethodtype() {
		return strMethodType;
	}

	/**
	 * @param methodtype
	 *            the methodtype to set
	 */
	public void setMethodtype(final String methodtype) {
		strMethodType = methodtype;
	}

	/**
	 * @return the headerValues
	 */
	public HashMap<String, String> getHeaderValues() {
		return hmHeaderValues;
	}

	/**
	 * @param headerValues
	 *            the headerValues to set
	 */
	public void setHeaderValues(final HashMap<String, String> headerValues) {
		hmHeaderValues = headerValues;
	}

	/**
	 * @return the input
	 */
	public byte[] getInput() {
		return btInput;
	}

	/**
	 * @param input
	 *            the input to set
	 */
	public void setInput(final byte[] input) {
		btInput = input;
	}

	public int getConnectionTimeOut() {
		return connectionTimeOut;
	}

	public void setConnectionTimeOut(final int connectionTimeOut) {
		this.connectionTimeOut = connectionTimeOut;
	}

	public boolean isFrequentCaller() {
		return isFrequentCaller;
	}

	public void setFrequentCaller(final boolean isFrequentCaller) {
		this.isFrequentCaller = isFrequentCaller;
	}

	public int getConnectionReadTimeOut() {
		return connectionReadTimeOut;
	}

	public void setConnectionReadTimeOut(int connectionReadTimeOut) {
		this.connectionReadTimeOut = connectionReadTimeOut;
	}
}
