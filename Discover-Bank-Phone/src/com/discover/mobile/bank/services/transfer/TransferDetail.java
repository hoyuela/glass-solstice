package com.discover.mobile.bank.services.transfer;

import java.io.Serializable;

import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.json.Money;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TransferDetail implements Serializable {

	/**Variable sent to the server if the transfer is to continue until cancelled*/
	public static final String UNTIL_CANCELLED = "continue_until_cancelled";

	/**Variable sent to the server if the transfer is to continue until a date*/
	public static final String UNTIL_DATE = "continue_until_a_set_end_date";

	/**Variable sent to the server if the transfer is to continue until a number of transactions have been made*/
	public static final String UNTIL_COUNT = "continue_until_a_set_number_of_transfers_have_been_made";

	/**Variable sent to the server if the transfer is to continue until a dollar amount*/
	public static final String UNTIL_AMOUNT = "continue_until_a_set_dollar_amount_has_been_transferred";

	/**Variable used to signify a one time transfer*/
	public static final String ONE_TIME_TRANSFER = "one_time_transfer";
	
	/**
	 * These values are used with the intention of identifying inline error responses from the server.
	 */
	public static final String AMOUNT = "amount.value";
	public static final String FROM_ACCOUNT = "fromAccount";
	public static final String ID = "id";
	public static final String TO_ACCOUNT = "toAccount";
	public static final String SEND_DATE = "sendDate";
	public static final String DELIVER_BY_DATE = "deliverBy";
	public static final String FREQUENCY = "frequency";
	public static final String DURATION_TYPE = "durationType";
	public static final String DURATION_VALUE = "durationValue";
	
	private static final long serialVersionUID = 3220773738601798470L;

	@JsonProperty(ID)
	public String id;

	@JsonProperty(FROM_ACCOUNT)
	public Account fromAccount;

	@JsonProperty(TO_ACCOUNT)
	public Account toAccount;

	@JsonProperty("amount")
	public Money amount;

	@JsonProperty(SEND_DATE)
	public String sendDate;

	@JsonProperty(DELIVER_BY_DATE)
	public String deliverBy;

	@JsonProperty(FREQUENCY)
	public String frequency;

	@JsonProperty(DURATION_TYPE)
	public String durationType;

	@JsonProperty(DURATION_VALUE)
	public String durationValue;
		
}
