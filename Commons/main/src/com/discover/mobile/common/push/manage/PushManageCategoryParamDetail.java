package com.discover.mobile.common.push.manage;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Special set of params posted with each preference
 * @author jthornton
 *
 */
public class PushManageCategoryParamDetail implements Serializable{
	
	/**Static string representing the amount code*/
	public static final String AMOUNT_CODE = "AMT";

	/**Unique serial identifier*/
	private static final long serialVersionUID = 3094405120334743575L;

	/**Code to associate this param with*/
	@JsonProperty("paramCode")
	public String code;
	
	/**Value of this param*/
	@JsonProperty("paramValue")
	public String value;
}
