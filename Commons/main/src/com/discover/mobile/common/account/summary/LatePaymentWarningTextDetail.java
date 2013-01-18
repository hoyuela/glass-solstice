package com.discover.mobile.common.account.summary;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Detail that will hold all of the possible text values to be displayed in the 
 * late payment warning modal
 * @author jthornton
 *
 */
public class LatePaymentWarningTextDetail implements Serializable{

	/**Unique identifier*/
	private static final long serialVersionUID = -4446792156776593776L;
	
	/**Matches the code in the late payment warning detail signifying that the b text should be used*/
	private static final String B = "B";

	/**Matches the code in the late payment warning detail signifying that the c text should be used*/
	private static final String C = "C";

	/**Matches the code in the late payment warning detail signifying that the m text should be used*/
	private static final String M = "M";

	/**Matches the code in the late payment warning detail signifying that the n text should be used*/
	private static final String N = "N";

	/**Matches the code in the late payment warning detail signifying that the o text should be used*/
	private static final String O = "O";
	
	/**Matches the code in the late payment warning detail signifying that the no statement text should be used*/
	public static final String NO_STMT = "NO_STMT";
	
	/**Key used to place in the merchant apr values*/
	public static final String PURCH_PENALTY_APR = "!~purch_penalty_apr~!";
	
	/**Key used to replace the penalty text*/
	public static final String PENALTY = "!~defOrPen~!";
	
	/**Key used to replace the late fee amount*/
	public static final String LATE_FEE = "!~late_fee~!";
	
	/**String to remove the starting section of the HTML*/
	public static final String START = "<strong>Late Payment Warning:</strong> ";
	
	/**Variable to add to the end of the string if the percentage is variable*/
	public static final String VARIABLE_END ="% variable";
	
	/**String containing the b text*/
	@JsonProperty("B")
	public String b;

	/**String containing the c text*/
	@JsonProperty("C")
	public String c;

	/**String containing the m text*/
	@JsonProperty("M")
	public String m;

	/**String containing the n text*/
	@JsonProperty("N")
	public String n;

	/**String containing the o text*/
	@JsonProperty("O")
	public String o;

	/**String containing the NO_STMT text*/
	@JsonProperty("NO_STMT")
	public String noStatement;
	
	/**
	 * Get the string that should be associated with the detail code
	 * @param code - the detail code that needs to be associated
	 * @return the string the will be shown
	 */
	public String getStringFromCode(final String code){
		if(B.equals(code)){
			return b;
		} else if(C.equals(code)){
			return c;
		} else if(M.equals(code)){
			return m;
		} else if(N.equals(code)){
			return n;
		} else if(O.equals(code)){
			return o;
		} else{
			return NO_STMT;
		}
	}
}
