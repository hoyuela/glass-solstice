package com.discover.mobile.bank.services.transfer;

import java.util.Locale;

import com.discover.mobile.common.utils.StringUtility;

public enum TransferType implements QueryParam {
	Scheduled("scheduled"),
	Completed("completed"),
	Cancelled("cancelled");
	
	private String queryValue = StringUtility.EMPTY;
	
	private TransferType(final String paramValue) {
		queryValue = paramValue.toUpperCase(Locale.US);
	}

	@Override
	public String getFormattedQueryParam() {
		return queryValue;
	}
	
}
