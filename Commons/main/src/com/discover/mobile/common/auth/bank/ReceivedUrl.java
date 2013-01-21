package com.discover.mobile.common.auth.bank;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This is used for storing the links objects returned from the JSON
 * 
 * @author ajleeds
 * 
 */
public class ReceivedUrl {

	@JsonProperty("ref")
	public String url;

	@JsonProperty("allowed")
	public List<?> method;

}
