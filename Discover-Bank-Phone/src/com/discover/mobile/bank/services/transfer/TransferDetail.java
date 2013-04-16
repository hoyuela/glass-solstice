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

	private static final long serialVersionUID = 3220773738601798470L;

	@JsonProperty("id")
	public String id;

	@JsonProperty("fromAccount")
	public Account fromAccount;

	@JsonProperty("toAccount")
	public Account toAccount;

	@JsonProperty("amount")
	public Money amount;

	@JsonProperty("sendDate")
	public String sendDate;

	@JsonProperty("deliverBy")
	public String deliverBy;

	@JsonProperty("frequency")
	public String frequency;

	@JsonProperty("durationType")
	public String durationType;

	@JsonProperty("durationValue")
	public String durationValue;
		
}
