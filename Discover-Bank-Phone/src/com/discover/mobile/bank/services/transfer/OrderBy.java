package com.discover.mobile.bank.services.transfer;

import java.util.Locale;

import com.discover.mobile.common.utils.StringUtility;

public enum OrderBy implements QueryParam{
	Amount("amount"),
	Date("date"),
	Frequency("frequency"),
	FromAccount("from_account"),
	ToAccount("to_account");
	
	private String queryValue = StringUtility.EMPTY;
	
	private OrderBy(final String paramValue) {
		queryValue = paramValue.toUpperCase(Locale.US);
	}

	@Override
	public String getFormattedQueryParam() {
		return queryValue;
	}
}
