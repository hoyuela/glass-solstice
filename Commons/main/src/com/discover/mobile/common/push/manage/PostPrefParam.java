package com.discover.mobile.common.push.manage;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Special set of params posted with each preference
 * @author jthornton
 *
 */
public class PostPrefParam implements Serializable{

	/**Unique serial identifier*/
	private static final long serialVersionUID = 7751622603759673195L;

	/**Static string representing the amount code*/
	public static final String AMOUNT_CODE = "AMT";

	/**Code to associate this param with*/
	@JsonProperty("parmCode")
	public String code;
	
	/**Value of this param*/
	@JsonProperty("parmValue")
	public String value;
}
