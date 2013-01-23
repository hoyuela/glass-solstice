package com.discover.mobile.common.customer.bank;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.discover.mobile.common.net.json.bank.Address;
import com.discover.mobile.common.net.json.bank.Name;
import com.discover.mobile.common.net.json.bank.PhoneNumber;
import com.discover.mobile.common.net.json.bank.ReceivedUrl;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This is used for storing Customer detailed information provided in a JSON response to a 
 * Bank web-service API invocation. 
 * 
 * The CustomerServiceCall class uses this object to store the
 * Customer information provided in the JSON response to the Customer Service API 
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
public class Customer implements Serializable {
	/**
	 * Auto-generated serial UID which is used to serialize and de-serialize PhoneNumber objects
	 */
	private static final long serialVersionUID = -544315055134223597L;
    /**
     * Contains the customer identifier number
     */
	@JsonProperty("id")
	public String id;
	
	@JsonProperty("name")
	public Name name;
	
	@JsonProperty("email")
	public String email;
	
	@JsonProperty("addresses")
	public List<Address> addresses;
	
	@JsonProperty("phoneNumbers")
	public List<PhoneNumber> phoneNumbers;
	/**
	 * Contains Bank web-service API Resource links
	 */
	@JsonProperty("links")
	public Map<String, ReceivedUrl> links = new HashMap<String, ReceivedUrl>();
}
