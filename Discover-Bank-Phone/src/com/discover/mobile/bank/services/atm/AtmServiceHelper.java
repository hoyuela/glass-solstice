/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.services.atm;

import android.location.Location;

/**
 * Helper object for the Get ATM details service calls.  When the user puts all
 * of the details in the object it will create the correct query string.  Example
 * of a query string is: 
 * ?latitude=41.927087&longitude=-87.650711&maxresults=2&distance=1&surchargeFree=
 * @author jthornton
 *
 */
public class AtmServiceHelper {

	/**
	 * Keys to create the query string
	 */
	private static final String QUERY_START = "?";
	private static final String DIVIDER = "&";
	private static final String LAT = "latitude=";
	private static final String LON = "longitude=";
	private static final String MAX_RESULTS = "maxresults=";
	private static final String DISTANCE = "distance=";
	private static final String SURCHARGE = "surchargeFree=";
	private static final String SURCHARGE_FREE = "Y";

	/**Maximum number of results the service should return*/
	private int maxResults = DEFAULT_RESULT_NUMBER;

	/**Distance in miles for the search around the location*/
	private int distance = DEFAULT_DISTANCE;

	/**Boolean letting the application know if the ATM should not have a surcharge*/
	private boolean isSurchargeFree = false;

	/**Location to search for atms around*/
	private final Location location;

	/**Default mile distance from the user*/
	private static final int DEFAULT_DISTANCE = 25;

	/**Default number of atms to return*/
	private static final int DEFAULT_RESULT_NUMBER = 30;

	/**
	 * Constructor for the helper
	 * @param location - Location to search for atms around
	 */
	public AtmServiceHelper(final Location location){
		this.location = location;
	}

	/**
	 * Build the query string
	 * @return the query string
	 */
	public String getQueryString(){
		final StringBuilder builder = new StringBuilder();
		builder.append(QUERY_START);
		builder.append(LAT + location.getLatitude() + DIVIDER);
		builder.append(LON + location.getLongitude() + DIVIDER);
		builder.append(MAX_RESULTS + maxResults + DIVIDER);
		builder.append(DISTANCE + distance + DIVIDER);
		builder.append(SURCHARGE);
		if(isSurchargeFree){
			builder.append(SURCHARGE_FREE);
		}
		return builder.toString();
	}

	/**
	 * @return the maxResults
	 */
	public int getMaxResults() {
		return maxResults;
	}

	/**
	 * @param maxResults the maxResults to set
	 */
	public void setMaxResults(final int maxResults) {
		this.maxResults = maxResults;
	}

	/**
	 * @return the distance
	 */
	public int getDistance() {
		return distance;
	}

	/**
	 * @param distance the distance to set
	 */
	public void setDistance(final int distance) {
		this.distance = distance;
	}

	/**
	 * @return the isSurchargeFree
	 */
	public boolean isSurchargeFree() {
		return isSurchargeFree;
	}

	/**
	 * @param isSurchargeFree the isSurchargeFree to set
	 */
	public void setSurchargeFree(final boolean isSurchargeFree) {
		this.isSurchargeFree = isSurchargeFree;
	}

}
