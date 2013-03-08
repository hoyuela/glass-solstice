/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.services.atm;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Main detail object for the get ATM service call.  This class is used
 * to map the response to the results so that the results can be displayed
 * on the map.
 * 
 * API: /api/atmLocator/SearchGeocodedLocation.xml
 * 
 * Sample Response: 
 * 
 * "Atm"=[{ "atmIdentifier": 1144113, "address1": "2426 N RACINE", "address2":
 * undefined, "address3": undefined, "cityName": "Chicago", "postalCode": 60614,
 * "locationName": "GAS LIGHT", "atmHrAvailDesc": "Unknown", "latitude":
 * 41.926176, "longitude": "-87.658589", "restricted": "U", "braille": "U",
 * "driveUp": "U", "walkUp": "U", "surchargeFree": "N", "stateName": "IL" }]
 * 
 * 
 * @author jthornton
 */
public class ListAtmDetails implements Serializable{

	/**Unique serialized object id*/
	private static final long serialVersionUID = -1477435671338212652L;

	/**List of atms close to the user*/
	@JsonProperty("Atm")
	public List<AtmDetail> atms;
}
