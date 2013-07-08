package com.discover.mobile.bank.services.transfer;

import java.util.Locale;

import com.discover.mobile.common.utils.StringUtility;
/**
 * An enum used when making queries to the server that are in need of some kind of sorting.
 * Can be used as a parameter in a query.
 * 
 * @author scottseward
 *
 */
public enum OrderBy implements QueryParam{
	Amount("amount"),
	Date("date"),
	Frequency("frequency"),
	FromAccount("from_account"),
	ToAccount("to_account");
	
	/**The value that is used as a parameter in a query.*/
	private String queryValue = StringUtility.EMPTY;
	
	private OrderBy(final String paramValue) {
		queryValue = paramValue.toUpperCase(Locale.US);
	}

	/**
	 * 
	 * @return a parameter that can be used in a query.
	 */
	@Override
	public String getFormattedQueryParam() {
		return queryValue;
	}
}
