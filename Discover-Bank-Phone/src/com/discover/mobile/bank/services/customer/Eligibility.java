package com.discover.mobile.bank.services.customer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.discover.mobile.bank.services.BankUrlManager;
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
	 * Denotes the service
	 */
	@JsonProperty("service")
	public String service;
	
	/**
	 * Denotes if the customer is eligible for this product.
	 */
	@JsonProperty("eligible")
	public boolean eligible;
	/**
	 * Denotes if the customer is enrolled in this product. 
	 * This field is always true for products that do not require enrollment.
	 */
	@JsonProperty("enrolled")
	public boolean enrolled;
	/**
	 * Contains Bank web-service API Resource links
	 */
	@JsonProperty("links")
	public Map<String, ReceivedUrl> links = new HashMap<String, ReceivedUrl>();
	
	/**
	 * Key used to read enroll url link from the links map.
	 */
	public final static String ENROLL_KEY = "enrollment";
	/**
	 * Key used to read enrollment url link from the links map.
	 */
	public final static String ENROLLMENT_KEY = "enrollment";
	/**
	 * Key used to read terms url link from the links map.
	 */
	public final static String TERMS_KEY = "terms";
	
	/**
	 * 
	 * @return True if customer is eligible for a product, false otherwise
	 */
	public boolean isEnrolled() {
		return enrolled;
	}
	/**
	 * 
	 * @return True if customer is enrolled in a product, false otherwise.
	 */
	public boolean isEligible() {
		return eligible;
	}
	/**
	 * 
	 * @return Returns the url string for enroll stored in the eligibility object.
	 */
	public String getEnroll() {
		return BankUrlManager.getUrl(links, ENROLL_KEY);
	}
	/**
	 * 
	 * @return Returns the url string for enroll stored in the eligibility object.
	 */
	public String getEnrollmentUrl() {
		return BankUrlManager.getUrl(links, ENROLLMENT_KEY);
	}
	
	/**
	 * 
	 * @return Returns the url string for terms store in the eligibility object.
	 */
	public String getTermsUrl() {
		return BankUrlManager.getBaseUrl() +BankUrlManager.SLASH +BankUrlManager.getUrl(links, TERMS_KEY);
	}
	
	/**
	 * 
	 * @return Returns true if eligibility objects is meant for transfer services, false otherwise.
	 */
	public boolean isTransfersEligibility() {
		return ( !Strings.isNullOrEmpty(this.service) && service.equals("transfers"));
	}
	
	/**
	 * 
	 * @return  Returns true if eligibility objects is meant for deposits services, false otherwise.
	 */
	public boolean isDepositsEligibility() {
		return ( !Strings.isNullOrEmpty(this.service) && service.equals("deposits"));
	}
	
	/**
	 * 
	 * @return  Returns true if eligibility objects is meant for payment services, false otherwise.
	 */
	public boolean isPaymentsEligibility() {
		return ( !Strings.isNullOrEmpty(this.service) && service.equals("payments"));
	}
	

}
