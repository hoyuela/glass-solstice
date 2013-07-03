package com.discover.mobile.bank.services.transfer;
/**
 * An enumerated type that represents the types of Transfers that
 * can be viewed by a user.
 */
import java.util.Locale;

import com.discover.mobile.common.utils.StringUtility;

public enum TransferType implements QueryParam {
	Scheduled("scheduled"),
	Completed("completed"),
	Cancelled("cancelled");
	
	/**The query value used when making a query for a type of transfer */
	private String queryValue = StringUtility.EMPTY;
	
	private TransferType(final String paramValue) {
		queryValue = paramValue.toUpperCase(Locale.US);
	}

	/**
	 * @return the formatted version of the query param.
	 */
	@Override
	public String getFormattedQueryParam() {
		return queryValue;
	}
	
}
