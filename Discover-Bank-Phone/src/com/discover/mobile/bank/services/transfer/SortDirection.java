package com.discover.mobile.bank.services.transfer;

import java.util.Locale;

import com.discover.mobile.common.utils.StringUtility;

public enum SortDirection implements QueryParam {
	Ascending("asc"),
	Descending("desc");

	private String queryValue = StringUtility.EMPTY;
	
	private SortDirection(final String paramValue) {
		queryValue = paramValue.toUpperCase(Locale.US);
	}
	
	@Override
	public String getFormattedQueryParam() {
		return queryValue;
	}

}
