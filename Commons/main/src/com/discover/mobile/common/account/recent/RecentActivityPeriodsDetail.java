package com.discover.mobile.common.account.recent;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecentActivityPeriodsDetail implements Serializable {

	private static final long serialVersionUID = 8888488954112075701L;
	
	@JsonProperty("dates")
	public List<RecentActivityPeriodDetail> dates;

}
