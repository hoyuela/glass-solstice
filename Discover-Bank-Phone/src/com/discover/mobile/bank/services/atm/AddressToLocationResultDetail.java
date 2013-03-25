/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.services.atm;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Detail object that contains information about the address location returned from the server
 * 
 * Eample Object
 * 
 * "address_components": [
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
            ]
 * 
 * 
 * @author jthornton
 *
 */
public class AddressToLocationResultDetail implements Serializable{

	/**Unique id of the object*/
	private static final long serialVersionUID = 3973606664437131363L;

	/**List of addresses returned from the server*/
	@JsonProperty("address_components")
	public List<AddressComponentDetail> addressComponent;

	/**Formatted address of the component*/
	@JsonProperty("formatted_address")
	public String formatted;

	/**Geometry object*/
	@JsonProperty("geometry")
	public GeometryDetail geometry;
}
