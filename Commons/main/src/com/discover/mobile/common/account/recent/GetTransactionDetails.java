package com.discover.mobile.common.account.recent;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetTransactionDetails implements Serializable{

	private static final long serialVersionUID = -7550914356143841837L;
	
	@JsonProperty("canSeePending")
	public boolean showPending;
	
	@JsonProperty("loadMoreLink")
	public String loadMoreLink;
	
	@JsonProperty("outageModeVal")
	public String outageMode;

	@JsonProperty("pendingReturnCode")
	public Integer pendingReturnCode;

	@JsonProperty("pendingTransactions")
	public List<TransactionDetail> pending;

	@JsonProperty("postedTransactions")
	public List<TransactionDetail> posted;

}
