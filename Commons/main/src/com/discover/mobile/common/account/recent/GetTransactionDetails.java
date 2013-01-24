package com.discover.mobile.common.account.recent;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Details about all received transactions
 * @author jthornton
 *
 */
public class GetTransactionDetails implements Serializable{

	/**Unique id*/
	private static final long serialVersionUID = -7550914356143841837L;
	
	/**Boolean to let the app know if it should show the pending table*/
	@JsonProperty("canSeePending")
	public boolean showPending;
	
	/**Link to load more transactions*/
	@JsonProperty("loadMoreLink")
	public String loadMoreLink;
	
	/**Outage mode value*/
	@JsonProperty("outageModeVal")
	public String outageMode;

	/**Pending return code*/
	@JsonProperty("pendingReturnCode")
	public Integer pendingReturnCode;

	/**List of pending transactions*/
	@JsonProperty("pendingTransactions")
	public List<TransactionDetail> pending;

	/**list of posted transactions*/
	@JsonProperty("postedTransactions")
	public List<TransactionDetail> posted;

}
