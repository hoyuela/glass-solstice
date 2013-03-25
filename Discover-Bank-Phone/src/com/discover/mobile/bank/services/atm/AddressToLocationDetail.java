/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.services.atm;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response Object of the GetLocationFromAddressServiceCall
 * 
 * Example Response:
 * 
 * 
    "results": [
        {
            "address_components": [
                {
                    "long_name": "60060",
                    "short_name": "60060",
                    "types": [
                        "postal_code"
                    ]
                },
                {
                    "long_name": "Mundelein",
                    "short_name": "Mundelein",
                    "types": [
                        "locality",
                        "political"
                    ]
                },
                {
                    "long_name": "Lake",
                    "short_name": "Lake",
                    "types": [
                        "administrative_area_level_2",
                        "political"
                    ]
                },
                {
                    "long_name": "Illinois",
                    "short_name": "IL",
                    "types": [
                        "administrative_area_level_1",
                        "political"
                    ]
                },
                {
                    "long_name": "United States",
                    "short_name": "US",
                    "types": [
                        "country",
                        "political"
                    ]
                }
            ],
            "formatted_address": "Mundelein, IL 60060, USA",
            "geometry": {
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
            },
            "types": [
                "postal_code"
            ]
        }
    ],
    "status": "OK"
}
 * @author jthornton
 *
 */
public class AddressToLocationDetail implements Serializable{

	/**Unique id of the object*/
	private static final long serialVersionUID = 488379329303545505L;

	/**List of results*/
	@JsonProperty("results")
	public List<AddressToLocationResultDetail> results;

	/**Status of the request*/
	@JsonProperty("status")
	public String status;
}
