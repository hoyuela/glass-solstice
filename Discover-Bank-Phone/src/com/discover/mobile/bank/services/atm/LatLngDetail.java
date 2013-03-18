/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.services.atm;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * {
 * 		"lat": 34.05236,
 *      "lng": -118.24356
 * }
 * @author jthornton
 *
 */
public class LatLngDetail implements Serializable{

	/**Generated id*/
	private static final long serialVersionUID = 7560119863202302962L;

	/**Latitude*/
	@JsonProperty("lat")
	public double lat;

	/**Longitude*/
	@JsonProperty("lng")
	public double lon;
}
