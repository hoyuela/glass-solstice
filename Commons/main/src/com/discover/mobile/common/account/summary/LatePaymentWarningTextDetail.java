package com.discover.mobile.common.account.summary;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LatePaymentWarningTextDetail implements Serializable{

	/**Unique identifier*/
	private static final long serialVersionUID = -4446792156776593776L;
	
	public static final String B = "B";
	
	public static final String C = "C";
	
	public static final String M = "M";
	
	public static final String N = "N";

	public static final String O = "O";
	
	public static final String NO_STMT = "NO_STMT";
	
	@JsonProperty("B")
	public String b;
	
	@JsonProperty("C")
	public String c;
	
	@JsonProperty("M")
	public String m;
	
	@JsonProperty("N")
	public String n;
	
	@JsonProperty("O")
	public String o;
	
	@JsonProperty("NO_STMT")
	public String noStatement;
}
