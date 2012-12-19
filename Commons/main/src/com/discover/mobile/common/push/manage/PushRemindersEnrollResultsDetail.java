package com.discover.mobile.common.push.manage;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PushRemindersEnrollResultsDetail {

	@JsonProperty("cardProductGroupOutageMode")
	public boolean inOutageMode;
	
	@JsonProperty("prefTypeCodesToDisplay")
	public List<String> codesToDisplay;
	
	@JsonProperty("preferences")
	public List<PreferencesDetail> preferences;
	
	@JsonProperty("optinMsgInd")
	public boolean optinMsgInd;
	
	@JsonProperty("balaDefAmt")
	public int balanceDefAmt;
	
	@JsonProperty("balaMaxAmt")
	public int balanaceMaxAmt;
	
	@JsonProperty("balaMinAmt")
	public int balanaceMinAmt;
	
	@JsonProperty("crltDefAmt")
	public int crltDefAmt;
	
	@JsonProperty("crltMaxAmt")
	public int crltMaxAmt;
	
	@JsonProperty("mlrwDefAmt")
	public int mlrwDefAmt;
	
	@JsonProperty("mlrwMinAmt")
	public int mlrwMinAmt;
	
	@JsonProperty("mrrwDefAmt")
	public int mrrwDefAmt;
	
	@JsonProperty("mrrwMaxAmt")
	public int mrrwMaxAmt;
	
	@JsonProperty("mrrwMinAmt")
	public int mrrwMinAmt;
	
	@JsonProperty("tamtDefAmt")
	public int tamtDefAmt;
	
	@JsonProperty("tamtMaxAmt")
	public int tamtMaxAmt;
	
	@JsonProperty("tamtMinAmt")
	public int tamtMinAmt;
	
	@JsonProperty("carrier")
	public String carrier;
	
	@JsonProperty("phoneNumber")
	public String phoneNumber;
	
	@JsonProperty("crltAmtOptions")
	public List<Integer> crltAmtOptions;
	
}
