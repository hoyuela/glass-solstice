/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.services.atm;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Geometry response used in the GetLocationFromAddressServiceCall.
 * 
 * Example Response:
 * "geometry": {
                "bounds": {
                    "northeast": {
                        "lat": 42.3062738,
                        "lng": -87.974051
                    },
                    "southwest": {
                        "lat": 42.226515,
                        "lng": -88.12215979999999
                    }
                },
                "location": {
                    "lat": 42.2785596,
                    "lng": -88.0314174
                },
                "location_type": "APPROXIMATE",
                "viewport": {
                    "northeast": {
                        "lat": 42.3062738,
                        "lng": -87.974051
                    },
                    "southwest": {
                        "lat": 42.226515,
                        "lng": -88.12215979999999
                    }
                }
            }
 * 
 * @author jthornton
 *
 */
public class GeometryDetail implements Serializable{

	private static final long serialVersionUID = 2595161853060066041L;

	/**Bounds of the route*/
	@JsonProperty("bounds")
	public BoundsDetail bounds;

	/**Location of the address*/
	@JsonProperty("location")
	public LatLngDetail endLocation;

	/**Location type*/
	@JsonProperty("location_type")
	public String locationType;

	/**viewport of the route*/
	@JsonProperty("viewport")
	public BoundsDetail viewPort;

}
