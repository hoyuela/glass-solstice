package com.discover.mobile.bank.services.account;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * 
 *  This class is used for storing the Account Number for an account, if any, specified 
 *  as a composite object of an ending and formatted string.
 *  
 *  This information is provided as part of every Account in a JSON response to a 
 *  Bank Accounts service API invocation via GetCustomerAccountsServerCall.
 *  
 * "accountNumber" : {
 *	"ending" : "1111",
 *	"formatted" : "*****1111"
 * }
 * 
 * @author henryoyuela
 *
 */
public class AccountNumber implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6847509395708829350L;
	
	/**
	 * The last four digits of the account number (e.g. 'ending in 1111).
	 */
	@JsonProperty("ending")
	public String ending;
	
	/**
	 * The account number in a formatted string (e.g. *****1111)
	 */
	@JsonProperty("formatted")
	public String formatted;
	
	
	@JsonProperty("unmaskedAccountNumber")
	public String unmaskedAccountNumber;

}
