package com.discover.mobile.bank.services.json;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This is used for storing the links objects returned from the JSON
 * 
 * @author ajleeds
 * 
 */
public class ReceivedUrl implements Serializable {

	/**
	 * Auto-generated serial UID which is used to serialize and de-serialize ReceivedUrl objects
	 */
	private static final long serialVersionUID = 1506089185579913594L;

	/**
	 * Contains a resource link to a web-service API
	 */
	@JsonProperty("ref")
	public String url;

	/**
	 * Contains what methods are supported by the web-service API specified in url [GET, POST]
	 */
	@JsonProperty("allowed")
	public List<?> method;

}
