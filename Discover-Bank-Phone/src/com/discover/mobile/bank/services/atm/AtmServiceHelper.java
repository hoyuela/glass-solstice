/*
 * � Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.services.atm;

import android.location.Location;

import com.discover.mobile.common.utils.StringUtility;

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
	private static final String ORIGIN = "origin=";
	private static final String DESTINATION = "destination=";
	private static final String SENSOR = "sensor=false";
	private static final String ADDRESS = "address=";
	private static final String PLUS = "+";
	private static final String COMMA = ",";
	private static final String ADDRESS_TO_LOCATION_END = "&ka=&sensor=false";

	/**Maximum number of results the service should return*/
	private int maxResults = DEFAULT_RESULT_NUMBER;

	/**Distance in miles for the search around the location*/
	private int distance = DEFAULT_DISTANCE;

	/**Boolean letting the application know if the ATM should not have a surcharge*/
	private boolean surchargeFree = false;

	/**Location to search for atms around*/
	private final Location location;

	/**Default mile distance from the user*/
	private static final int DEFAULT_DISTANCE = 25;

	/**Default number of atms to return*/
	private static final int DEFAULT_RESULT_NUMBER = 30;

	/**String of the to address*/
	private final String to;

	/**String of the from address*/
	private final String from;

	/**Address to get the location of*/
	private final String address;

	/**
	 * Constructor for the helper
	 * @param location - Location to search for atms around
	 */
	public AtmServiceHelper(final Location location){
		this.location = location;
		to = null;
		from = null;
		address = null;
	}


	/**
	 * Constructor for the helper
	 * @param to - String for the address that the user is going to
	 * @param from - String for the address that the users is coming from
	 */
	public AtmServiceHelper(final String to, final String from){
		this.to = to;
		this.from = from;
		location = null;
		address = null;
	}

	/**
	 * Constructor for the helper
	 * @param address - String for the address to get the location of.=
	 */
	public AtmServiceHelper(final String address){
		to = null;
		from = null;
		location = null;
		this.address = address;
	}

	/**
	 * Build the query string
	 * @return the query string
	 */
	public String getQueryString(){
		final StringBuilder builder = new StringBuilder();
		builder.append(QUERY_START);
		builder.append(LAT);
		builder.append(location.getLatitude());
		builder.append(DIVIDER);
		builder.append(LON);
		builder.append(location.getLongitude());
		builder.append(DIVIDER);
		builder.append(MAX_RESULTS);
		builder.append(maxResults);
		builder.append(DIVIDER);
		builder.append(DISTANCE);
		builder.append(distance);
		builder.append(DIVIDER);
		builder.append(SURCHARGE);
		if(surchargeFree){
			builder.append(SURCHARGE_FREE);
		}
		return builder.toString();
	}

	/**
	 * Build the directions query string
	 * @return the directions query string
	 */
	public String getDirectionsQueryString(){
		final StringBuilder builder = new StringBuilder();
		builder.append(QUERY_START + ORIGIN);
		builder.append(from.replaceAll(StringUtility.SPACE, PLUS).replaceAll(COMMA, StringUtility.EMPTY));
		builder.append(DIVIDER+DESTINATION);
		builder.append(to.replaceAll(StringUtility.SPACE, PLUS).replaceAll(COMMA, StringUtility.EMPTY));
		builder.append(DIVIDER+SENSOR);
		return builder.toString();
	}

	public String getAddressToLocationString(){
		final StringBuilder builder = new StringBuilder();
		builder.append(QUERY_START + ADDRESS);
		builder.append(address.replaceAll(StringUtility.SPACE, StringUtility.ENCODED_SPACE));
		builder.append(ADDRESS_TO_LOCATION_END);
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
		return surchargeFree;
	}

	/**
	 * @param isSurchargeFree the isSurchargeFree to set
	 */
	public void setSurchargeFree(final boolean isSurchargeFree) {
		this.surchargeFree = isSurchargeFree;
	}


	/**
	 * @return the to
	 */
	public String getTo() {
		return to;
	}


	/**
	 * @return the from
	 */
	public String getFrom() {
		return from;
	}

}
