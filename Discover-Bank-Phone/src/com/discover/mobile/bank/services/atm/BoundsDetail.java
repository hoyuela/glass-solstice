/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.services.atm;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * "bounds": {
 *                "northeast": {
 *                   "lat": 41.87811000000001,
 *                    "lng": -87.62979000000001
 *                },
 *                "southwest": {
 *                    "lat": 34.05236,
 *                    "lng": -118.24356
 *                }
 *            }
 * @author jthornton
 *
 */

public class BoundsDetail implements Serializable{

	/**Generated id*/
	private static final long serialVersionUID = 167258888984722849L;

	/**North east bound*/
	@JsonProperty("northeast")
	public LatLngDetail northEast;

	/**SouthWest bound*/
	@JsonProperty("southwest")
	public LatLngDetail southWest;
}
