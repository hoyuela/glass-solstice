package com.discover.mobile.bank.services.customer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.discover.mobile.common.net.json.bank.ReceivedUrl;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

/**
 * This class is used for mapping an Eligibility JSON object. The JSON
 * object described below is provided in a Customer JSON object when
 * calling the Bank Customer API service call /api/customers/current via
 * the CustomerServiceCall class.
 * 
 * "payments" : {
 *	"eligible" : true,
 *	"enrolled" : false,
 *	"links" : {
 *		"terms" : {
 *			"ref" : "https://www.discoverbank.com/api/payments/terms",
 *			"allowed" : [ "GET", "POST" ]
 *		}
 *	}
 *}
 * @author henryoyuela
 *
 */
public class Eligibility implements Serializable {
	/**
	 * Auto-generated serial UID which is used to serialize and de-serialize Eligibility objects
	 */
	private static final long serialVersionUID = 1316839138854303741L;
	/**
	 * Denotes if the customer is eligible for this product.
	 */
	@JsonProperty("eligible")
	public String eligible;
	/**
	 * Denotes if the customer is enrolled in this product. 
	 * This field is always true for products that do not require enrollment.
	 */
	@JsonProperty("enrolled")
	public String enrolled;
	/**
	 * Contains Bank web-service API Resource links
	 */
	@JsonProperty("links")
	public Map<String, ReceivedUrl> links = new HashMap<String, ReceivedUrl>();
	
	/**
	 * 
	 * @return True if customer is eligible for a product, false otherwise
	 */
	public boolean isEnrolled() {
		return (!Strings.isNullOrEmpty(enrolled) && enrolled.equals("true"));
	}
	/**
	 * 
	 * @return True if customer is enrolled in a product, false otherwise.
	 */
	public boolean isEligible() {
		return (!Strings.isNullOrEmpty(enrolled) && eligible.equals("true"));
	}
}
