package com.discover.mobile.common.account.recent;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Details about transactions
 * @author jthornton
 *
 */
public class TransactionDetail implements Serializable {

	/**Unique identifier*/
	private static final long serialVersionUID = 3597259705149565786L;
	
	/**Amount of the transaction*/
	@JsonProperty("txnAmt")
	public String amount;
	
	/**Date of the transaction*/
	@JsonProperty("txnDate")
	public String date;
	
	/**Transaction description*/
	@JsonProperty("txnDesc")
	public String description;
	
	/**Transaction posted date*/
	@JsonProperty("txnPostDt")
	public String postedDate;
	
	/**Transaction category*/
	@JsonProperty("txnCtg")
	public String category;
	
	/**Transaction merchant id*/
	@JsonProperty("merchantId")
	public String merchantId;
}
