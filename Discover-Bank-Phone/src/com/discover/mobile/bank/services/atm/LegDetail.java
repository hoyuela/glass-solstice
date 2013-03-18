/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.services.atm;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Details about the specific leg of the route
 * @author jthornton
 *
 */
public class LegDetail implements Serializable{

	/**Generated id*/
	private static final long serialVersionUID = -7983658920842966847L;

	/**Distance of the leg*/
	@JsonProperty("distance")
	public TextValueDetail distance;

	/**Duration of the leg*/
	@JsonProperty("duration")
	public TextValueDetail duration;

	/**Ending address of the leg*/
	@JsonProperty("end_address")
	public String endAddress;

	/**Ending location of the address*/
	@JsonProperty("end_location")
	public LatLngDetail endLocation;

	/*8Start address of the location*/
	@JsonProperty("start_address")
	public String startAddress;

	/**Starting location*/
	@JsonProperty("start_location")
	public LatLngDetail startLocation;

	/**Steps to get there*/
	@JsonProperty("steps")
	public List<StepDetail> steps;
}
