package com.discover.mobile.common.push.manage;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Object containing the users preferences
 * @author jthornton
 *
 */
public class PushRemindersEnrollResultsDetail {

	/**Boolean set if the server is in outage mode*/
	@JsonProperty("cardProductGroupOutageMode")
	public boolean inOutageMode;
	
	/**List of code to be shown to the user*/
	@JsonProperty("prefTypeCodesToDisplay")
	public List<String> codesToDisplay;
	
	/**List of preferences the user has set*/
	@JsonProperty("preferences")
	public List<PreferencesDetail> preferences;
	
	/**Optin message*/
	@JsonProperty("optinMsgInd")
	public boolean optinMsgInd;
	
	/**bala category defined amount*/
	@JsonProperty("balaDefAmt")
	public int balanceDefAmt;
	
	/**bala category max amount*/
	@JsonProperty("balaMaxAmt")
	public int balanaceMaxAmt;
	
	/**bala category min amount*/
	@JsonProperty("balaMinAmt")
	public int balanaceMinAmt;
	
	/**crlt category defined amount*/
	@JsonProperty("crltDefAmt")
	public int crltDefAmt;
	
	/**crlt category max amount*/
	@JsonProperty("crltMaxAmt")
	public int crltMaxAmt;
	
	/**mlrw defined amount*/
	@JsonProperty("mlrwDefAmt")
	public int mlrwDefAmt;
	
	/**mlrw category min amount*/
	@JsonProperty("mlrwMinAmt")
	public int mlrwMinAmt;
	
	/**mrrw category defined amount*/
	@JsonProperty("mrrwDefAmt")
	public int mrrwDefAmt;
	
	/**mrrw category max amount*/
	@JsonProperty("mrrwMaxAmt")
	public int mrrwMaxAmt;
	
	/**mrrw category min amount*/
	@JsonProperty("mrrwMinAmt")
	public int mrrwMinAmt;
	
	/**tamt category defined amount*/
	@JsonProperty("tamtDefAmt")
	public int tamtDefAmt;
	
	/**tamt category max amount*/
	@JsonProperty("tamtMaxAmt")
	public int tamtMaxAmt;
	
	/**tamt category min amount*/
	@JsonProperty("tamtMinAmt")
	public int tamtMinAmt;
	
	/**Carrier of the users phone*/
	@JsonProperty("carrier")
	public String carrier;
	
	/**Phone number that gets text alerts*/
	@JsonProperty("phoneNumber")
	public String phoneNumber;
	
	/**Amount options to be displayed in the crlt spinner*/
	@JsonProperty("crltAmtOptions")
	public List<Integer> crltAmtOptions;
	
}
