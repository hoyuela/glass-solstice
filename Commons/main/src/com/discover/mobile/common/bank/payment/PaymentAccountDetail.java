package com.discover.mobile.common.bank.payment;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.discover.mobile.common.net.json.bank.ReceivedUrl;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * JSON object holding the information about the payment account
 * 
 * JSON Example:
 * 
 * paymentAccount": {
 *        "ending": "1111",
 *        "id": 1,
 *        "name": "Discover Cashback Checking",
 *        "nickname": "My Rewards Checking",
 *        "type": "CHECKING",
 *        "balance": 123456,
 *        "interestRate": {
 *            "numerator": 6,
 *            "denominator": 100,
 *            "formatted": "0.06%"
 *        },
 *        "interestEarnedLastStatement": 123,
 *        "interestYearToDate": 4321,
 *        "openDate": "2007-04-06T16: 14: 24.134455Z",
 *        "status": "OPEN",
 *        "links": {
 *            "self": {
 *                "ref": "https://www.discoverbank.com/api/accounts/1",
 *                "allowed": ["GET"]
 *            }
 *        }
 *    },
 * 
 * @author jthornton
 *
 */
public class PaymentAccountDetail implements Serializable{

	/**Unique identifier for the object*/
	private static final long serialVersionUID = 5814960770930924047L;

	/**Last four digits of the account*/
	@JsonProperty("ending")
	public String ending;

	/**Id of the account*/
	@JsonProperty("id")
	public int id;

	/**Name of the account*/
	@JsonProperty("name")
	public String name;

	/**Nickname of the account*/
	@JsonProperty("nickname")
	public String nickName;

	/**Type of account*/
	@JsonProperty("type")
	public String type;

	/**Balance of the account*/
	@JsonProperty("balance")
	public int balance;

	/**Interest rate of the account*/
	@JsonProperty("interestRate")
	public InterestRateDetail interest;

	/**Interest earned for the account*/
	@JsonProperty("interestEarnedLastStatement")
	public int interestEarned;

	/**Interest earned to date*/
	@JsonProperty("interestYearToDate")
	public int interestToDate;

	/**Date the account was opened*/
	@JsonProperty("openDate")
	public int openDate;

	/**Status of the account*/
	@JsonProperty("status")
	public String status;

	/**
	 * Contains Bank web-service API Resource links
	 */
	@JsonProperty("links")
	public Map<String, ReceivedUrl> links = new HashMap<String, ReceivedUrl>();
}
