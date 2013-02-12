package com.discover.mobile.card.services.account.summary;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Late payment warning modal detail object.  This will hold information that will eventually be 
 * placed into a string.
 * @author jthornton
 *
 */
public class LatePaymentWarningDetail implements Serializable{

	/*Unique modifier*/
	private static final long serialVersionUID = -5809931834014640472L;
	
	/**String holding the outage mode*/
	@JsonProperty("outageModeVal")
	public String outageMode;
	
	/**Title to be swapped into the string*/
	@JsonProperty("aprTitle")
	public String aprTitle;
	
	/**IsAfterPcrd5 variable*/
	@JsonProperty("isAfterPcrd5")
	public boolean isAfterPcrd5;
	
	/**
	 * String holding the penalty APR code.  Used to match to the late payment warning text detail
	 */
	@JsonProperty("penaltyWarningAPRCode")
	public String penaltyAPRCode;
	
	/**Amount to be placed in as the late fee amount*/
	@JsonProperty("lateFeeWarningAmount")
	public String lateFee;
	
	/**penaltyVariableFixedInd variable*/
	@JsonProperty("penaltyVariableFixedInd")
	public String penaltyVariableFixedInd;
	
	/**Cash warning APR code*/
	@JsonProperty("penaltyWarningCashAPR")
	public String cashAPR;
	
	/**Rate to be shown with the merchant APR*/
	@JsonProperty("penaltyWarningMerchantAPR")
	public String merchantAPR;
}
