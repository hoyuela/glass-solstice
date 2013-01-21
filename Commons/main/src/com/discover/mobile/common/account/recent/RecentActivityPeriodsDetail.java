package com.discover.mobile.common.account.recent;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Details that are received from the server from the Get Activity Periods call
 * @author jthornton
 *
 */
public class RecentActivityPeriodsDetail implements Serializable {

	/**Unique identifier for the object*/
	private static final long serialVersionUID = 8888488954112075701L;
	
	/**List of periods that can the user can select to filter the transactions with*/
	@JsonProperty("dates")
	public List<RecentActivityPeriodDetail> dates;

}
