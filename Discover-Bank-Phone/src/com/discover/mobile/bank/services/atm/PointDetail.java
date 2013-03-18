/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.services.atm;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Point detail containing a string for the points that should be placed on the map
 * @author jthornton
 *
 */
public class PointDetail implements Serializable{

	/**Generated id*/
	private static final long serialVersionUID = 202274830686727437L;

	/**Point detail containing a string for the points that should be placed on the map*/
	@JsonProperty("points")
	public String points;
}
