package com.discover.mobile.common.net.json.bank;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This is used for storing Address information provided in a JSON response to a 
 * Bank web-service API invocation. 
 * 
 * The CustomerServiceCall class uses this object to store the
 * Addresss information provided in the JSON response to the Customer Service API 
 * /api/customers/current. The following is an example of the JSON:
 * 
 * {
 *		"id" : "1",
 *		"name" : "Andrew Duckett",
 *		"email" : "andrewduckett@discover.com", "addresses" : [ {
 *			"type" : "work",
 *			"streetAddress" : "2600 Lake Cook Road", "locality" : "Riverwoods",
 *			"region" : "Illinois",
 *			"postalCode" : "60015"
 *		} ],
 *		"phoneNumbers" : [ {
 *			"type" : "work",
 *			"number" : "224.405.5446" 
 *		} ],
 *		"links" : { 
 *			"accounts" : {
 *				"ref" : "https://www.discoverbank.com/api/accounts",
 *				"allowed" : [ "GET" ] },
 *			"payees" : {
 *				"ref" : "https://www.discoverbank.com/api/payees", "allowed" :[ "GET", 	"POST" ]
 *			},
 *			"payments" : {
 *				"ref" : "https://www.discoverbank.com/api/payments",
 *				"allowed" : [ "GET", "POST" ] 
 *			},
 *			"self" : {
 *				"ref" : "https://www.discoverbank.com/api/customers/1", "allowed" : [ "GET" ]
 *			},
 *			"transfers" : {
 *				"ref" : "https://www.discoverbank.com/api/transfers",
 *				"allowed" : [ "GET", "POST" ]
 *			}
 *		}
 *	}
 * 
 * @author henryoyuela
 *
 */
public class Address implements Serializable {
	/**
	 * Auto-generated serial UID which is used to serialize and de-serialize Address objects
	 */
	private static final long serialVersionUID = -3833181121877379811L;

	/**
	 * Contains the type of address information i.e. work, home etc
	 */
	@JsonProperty("type")
	public String type;

	@JsonProperty("streetAddress")
	public String streetAddress;
	
	@JsonProperty("locality")
	public String locality;
	
	@JsonProperty("region")
	public String region;
	
	@JsonProperty("postalCode")
	public String postalCode;
}
