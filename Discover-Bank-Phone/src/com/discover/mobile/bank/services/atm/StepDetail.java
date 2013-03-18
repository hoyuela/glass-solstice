/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.services.atm;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Details about the specific step
 * @author jthornton
 *
 */
public class StepDetail implements Serializable{

	/**Generated id*/
	private static final long serialVersionUID = -801947263703639730L;

	/**Distance of the step*/
	@JsonProperty("distance")
	public TextValueDetail distance;

	/**Duration of the step*/
	@JsonProperty("duration")
	public TextValueDetail duration;

	/**Ending location of the step*/
	@JsonProperty("end_location")
	public LatLngDetail endLocation;

	/**Html instructions of the step*/
	@JsonProperty("html_instructions")
	public String html;	

	/**Poly line for the overlay*/
	@JsonProperty("polyline")
	public PointDetail polyLine;

	/**Starting location of the step*/
	@JsonProperty("start_location")
	public LatLngDetail startLocation;

	/**Travel mode*/
	@JsonProperty("travel_mode")
	public String mode;

}
