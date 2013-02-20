package com.discover.mobile.bank.services.customer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.discover.mobile.bank.services.BankUrlManager;
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
 *    "id": "1",
 *    "name": {
 *        "givenName": "Andrew",
 *        "middleName": "M",
 *        "familyName": "Duckett",
 *        "formatted": "Andrew M Duckett"
 *    },
 *    "email": "andrewduckett@discover.com",
 *    "addresses": [
 *        {
 *            "type": "work",
 *            "streetAddress": "2600 Lake Cook Road",
 *            "locality": "Riverwoods",
 *            "region": "Illinois",
 *            "postalCode": "60015",
 *            "formatted": "2600 Lake Cook Road\nRiverwoods Illinois 60015"
 *        }
 *    ],
 *    "phoneNumbers": [
 *        {
 *            "type": "work",
 *            "number": "2244055446"
 *        }
 *    ],
 *    "eligibility": {
 *        "transfers": {
 *            "eligible": true,
 *            "enrolled": true
 *        },
 *        "payments": {
 *            "eligible": true,
 *            "enrolled": false,
 *            "links": {
 *                "terms": {
 *                    "ref": "https://www.discoverbank.com/api/payments/terms",
 *                    "allowed": [
 *                        "GET",
 *                        "POST"
 *                    ]
 *                }
 *            }
 *        },
 *        "deposits": {
 *            "eligible": false,
 *            "enrolled": false
 *        }
 *    },
 *    "links": {
 *        "accounts": {
 *            "ref": "/api/accounts",
 *            "allowed": [
 *                "GET"
 *            ]
 *        },
 *        "payees": {
 *            "ref": "/api/payees",
 *            "allowed": [
 *                "GET",
 *                "POST"
 *            ]
 *        },
 *        "payments": {
 *            "ref": "/api/payments",
 *            "allowed": [
 *                "GET",
 *                "POST"
 *             ]
 *        },
 *        "self": {
 *            "ref": "/api/customers/1",
 *            "allowed": [
 *                "GET"
 *            ]
 *        },
 *        "transfers": {
 *            "ref": "/api/transfers",
 *            "allowed": [
 *                "GET",
 *                "POST"
 *            ]
 *        }
 *    }
 *}
 * 
 * @author henryoyuela
 *
 */
public class Customer implements Serializable {
	/**
	 * Auto-generated serial UID which is used to serialize and de-serialize Customer objects
	 */
	private static final long serialVersionUID = -544315055134223597L;
	/**
	 * Contains the customer identifier number
	 */
	@JsonProperty("id")
	public String id;
	/**
	 * Name of this customer
	 */
	@JsonProperty("name")
	public Name name;
	/**
	 * Email address belonging to this customer
	 */
	@JsonProperty("email")
	public String email;
	/**
	 * Postal addresses belonging to this customer
	 */
	@JsonProperty("addresses")
	public List<Address> addresses;
	/**
	 * Phone numbers belonging to this customer
	 */
	@JsonProperty("phoneNumbers")
	public List<PhoneNumber> phoneNumbers;
	/**
	 * Contains Bank web-service API Resource links
	 */
	@JsonProperty("links")
	public Map<String, ReceivedUrl> links = new HashMap<String, ReceivedUrl>();
	/**
	 * The customer's eligibility and enrollment and various products.
	 */
	@JsonProperty("eligibility")
	public List<Eligibility> eligibilities;
//	public Map<String, Eligibility> eligibilities;

	/**
	 * 
	 * @return
	 */
	public boolean canAccessTransfers() {
		return (this.links.get(BankUrlManager.TRANSFER_URL_KEY) != null );
	}

	/**
	 * 
	 * @return True if has a link to Payees, false otherwise
	 */
	public boolean canAccessPayees() {
		return (this.links.get(BankUrlManager.PAYEES_URL_KEY) != null );
	}

	/**
	 * 
	 * @return  True if has a link to Payments, false otherwise
	 */
	public boolean canAccessPayments() {
		return (this.links.get(BankUrlManager.TRANSFER_URL_KEY) != null );
	}

	/**
	 * 
	 * @return True if the Accounts link is provided, false otherwise
	 */
	public boolean hasAccounts() {
		return (this.links.get(BankUrlManager.ACCOUNT_URL_KEY) != null );
	}

	/**
	 * 
	 * @return Returns Transfers Eligibility object that specifies a Customer's enrollment
	 * and eligibility status for transfers
	 */
	public Eligibility getTransfersEligibility() {
		return ( this.eligibilities != null )? getEligibilityValues("transfers") : null;
	}

	/**
	 * 
	 * @return Returns boolean that specifies a Customer's eligibility status for Payments
	 */
	public boolean getPaymentsEligibility() {
		return ( this.eligibilities != null )? getEligibilityValues("payments").isEligible() : false;
	}

	/**
	 * 
	 * @return Returns boolean object that specifies a Customer's enrollment status for Payments
	 */
	public boolean getPaymentsEnrolled() {
		return ( this.eligibilities != null )? getEligibilityValues("payments").isEnrolled() : false;
	}

	/**
	 * 
	 * @return Returns Transfers Eligibility object that specifies a Customer's
	 * enrollment and eligibility status for Transfers
	 */
	public Eligibility getDepositsEligibility() {
		return ( this.eligibilities != null )? getEligibilityValues("deposits") : null;
	}
	
	/**
	 * Returns the eligibility object for the given key
	 * 
	 * @param key
	 * @return
	 */
	public Eligibility getEligibilityValues(String key){
		for (int i = 0; i < eligibilities.size(); i++) {
		    String serviceName = eligibilities.get(i).service;
		    if (serviceName != null && serviceName.equals(key)){
		        return eligibilities.get(i);
		    }
		}
		return null;
	}
}
