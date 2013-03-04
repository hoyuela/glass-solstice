package com.discover.mobile.common.net.json.bank;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This is used for storing Name information provided in a JSON response to a 
 * Bank web-service API invocation. 
 * 
 * The CustomerServiceCall class uses this object to store the
 * Name information provided in the JSON response to the Customer Service API 
 * /api/customers/current. The following is an example of the JSON:
 * 
  * {
 *		"id":"0000443951",
 *		"name":{
 *			"givenName":"AUDREY",
 *			"middleName":"",
 *			"familyName": "RICHARDSON",
 *			"formatted":"AUDREY  RICHARDSON"
 *		},
 *		"email" : "andrewduckett@discover.com", 
 *		"addresses":[{
 *                "type":"HOME",
 *                "streetAddress":"19949 PLEASANT MEADOW LANE",
 *                "locality":"PURCELLVILLE",
 *                "region":"VA",
 *                "postalCode":"20132",
 *                "formatted":"19949 PLEASANT MEADOW LANE\nPURCELLVILLE VA 20132"
 *       }],
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
public class Name implements Serializable {
	
	/**
	 * Auto-generated serial UID which is used to serialize and de-serialize Name object
	 */
	private static final long serialVersionUID = 6648021748646124016L;

	@JsonProperty("givenName")
	public String type;

	 @JsonProperty("middleName")
	 public String middleName;
	 
	 @JsonProperty("familyName")
	 public String familyName;
	 
	 @JsonProperty("formatted")
	 public String formatted;

}
