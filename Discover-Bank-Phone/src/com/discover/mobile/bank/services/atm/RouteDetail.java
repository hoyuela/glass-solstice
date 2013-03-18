/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.services.atm;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Details about the route that the user should get to arrive at the ATM location
 * @author jthornton
 *
 */
public class RouteDetail implements Serializable{

	/**Generated id*/
	private static final long serialVersionUID = 254973762536483488L;

	/**Bounds of the route*/
	@JsonProperty("bounds")
	public BoundsDetail bounds;

	/**Copyrights from Google*/
	@JsonProperty("copyrights")
	public String copyrights;

	/**Legs of the route*/
	@JsonProperty("legs")
	public List<LegDetail> legs;

	/**Line to draw on the map*/
	@JsonProperty("overview_polyline")
	public PointDetail overview;

	/**Summary of the route*/
	@JsonProperty("summary")
	public String summary;

	/**Warnings on the route*/
	@JsonProperty("warnings")
	public List<String> warnings;

	/**Order of the wapoints if there are any*/
	@JsonProperty("waypoint_order")
	public List<String> order;
}
