package com.discover.mobile.card.services.account.recent;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The date range object. Contains the statement code and the display date for the period
 * @author jthornton
 *
 */
public class RecentActivityPeriodDetail implements Serializable{

	/**Unique identifier for the object*/
	private static final long serialVersionUID = -3745478544230050226L;
	
	/**Statement date code, used to filter the transactions on the server side*/
	@JsonProperty("stmtDate")
	public String date;
	
	/**Date to display to the user when the user is selecting from a range of dates*/
	@JsonProperty("displayDate")
	public String displayDate;

}
