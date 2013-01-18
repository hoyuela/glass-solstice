package com.discover.mobile.common.account.summary;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LatePaymentWarningDetail implements Serializable{

	/*Unique modifier*/
	private static final long serialVersionUID = -5809931834014640472L;
	
	@JsonProperty("outageModeVal")
	public String outageMode;
	
	@JsonProperty("aprTitle")
	public String aprTitle;
	
	@JsonProperty("isAfterPcrd5")
	public boolean isAfterPcrd5;
	
	@JsonProperty("penaltyWarningAPRCode")
	public String penaltyAPRCode;
	
	@JsonProperty("lateFeeWarningAmount")
	public String lateFee;
	
	@JsonProperty("penaltyVariableFixedInd")
	public String penaltyVariableFixedInd;
	
	@JsonProperty("penaltyWarningCashAPR")
	public String cashAPR;
	
	@JsonProperty("penaltyWarningMerchantAPR")
	public String merchantAPR;
}
