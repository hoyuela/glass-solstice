package com.discover.mobile.bank.services.transfer;

/**
 * An interface that can be used for enumerated types to implement that are
 * intended to be used as query parameters for service calls.
 * An implemented getFormatedQueryParam method should return a String that
 * is ready to be inserted into a http query parameter.
 * 
 * @author scottseward
 *
 */
public interface QueryParam {
	String getFormattedQueryParam();
}
