package com.discover.mobile.bank.services.customer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.services.json.ReceivedUrl;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
	 * The reason why the customer is not eligible. Only present if 'eligible' is false
	 */
	@JsonProperty("reasonCode")
	public String reasonCode;
	/**
	 * Flag used to determine whether this eligibility object has been syncrhornized with values from the server.
	 */
	@JsonIgnore
	public boolean updated;

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
	 * The customer does not have any accounts eligible for this service.
	 */
	public final static String NOT_ELIGIBLE_REASON = "NoEligibleAccounts";
	/**
	 * The customer has been blocked from this service.
	 */
	public final static String CUSTOMER_BLOCKED_REASON = "CustomerBlocked";
	
	/**
	 * 
	 * @return True if customer is eligible for a product, false otherwise
	 */
	public final boolean isEnrolled() {
		return enrolled;
	}
	/**
	 * 
	 * @return True if customer is enrolled in a product, false otherwise.
	 */
	public final boolean isEligible() {
		return eligible;
	}
	/**
	 * 
	 * @return Returns the url string for enroll stored in the eligibility object.
	 */
	public final String getEnroll() {
		return BankUrlManager.getUrl(links, ENROLL_KEY);
	}
	/**
	 * 
	 * @return Returns the url string for enroll stored in the eligibility object.
	 */
	public final String getEnrollmentUrl() {
		return BankUrlManager.getUrl(links, ENROLLMENT_KEY);
	}
	
	/**
	 * 
	 * @return Returns the url string for terms store in the eligibility object.
	 */
	public final String getTermsUrl() {
		final StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(BankUrlManager.getBaseUrl());
		
		final String termsLink = BankUrlManager.getUrl(links, TERMS_KEY);
		
		if(BankUrlManager.isValidContentLink(termsLink)) {
			urlBuilder.append(termsLink);
		}else {
			urlBuilder.append(getFailSafeTermsUrl());
		}

		return urlBuilder.toString();
	}
	
	/**
	 * 
	 * @return a URL corresponding to the terms and conditions for this eligibility object.
	 */
	private String getFailSafeTermsUrl() {
		final String url = "api/content/%s/terms.html";
		return String.format(url, service);
	}
	
	/**
	 * 
	 * @return Returns true if eligibility objects is meant for transfer services, false otherwise.
	 */
	public final boolean isTransfersEligibility() {
		return ( !Strings.isNullOrEmpty(this.service) && service.equals("transfers"));
	}
	
	/**
	 * 
	 * @return  Returns true if eligibility objects is meant for deposits services, false otherwise.
	 */
	public final boolean isDepositsEligibility() {
		return ( !Strings.isNullOrEmpty(this.service) && service.equals("deposits"));
	}
	
	/**
	 * 
	 * @return  Returns true if eligibility objects is meant for payment services, false otherwise.
	 */
	public final boolean isPaymentsEligibility() {
		return ( !Strings.isNullOrEmpty(this.service) && service.equals("payments"));
	}
	
	/**
	 * @return Returns true if reasonCode is equal to CUSTOMER_BLOCKED_REASON which means customer is forbidden
	 *         to use the service specified in service field, false otherwise.
	 */
	public final boolean isUserBlocked() {
		return !eligible && (!Strings.isNullOrEmpty(reasonCode) && reasonCode.equals(CUSTOMER_BLOCKED_REASON));
	}

	/**
	 * Method used to determine whether this object has been updated with the eligibility values on the server.
	 * 
	 * @return True if updated, false otherwise
	 */
	public boolean isUpdated() {
		return updated;
	}

	/**
	 * Method used to update the flag used to determine whether this object has been updated with the eligibility values
	 * on the server.
	 * 
	 * @param updated
	 *            True if it has been updated, false otherwise.
	 */
	public void setUpdated(final boolean updated) {
		this.updated = updated;
	}
}
