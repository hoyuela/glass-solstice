package com.discover.mobile.bank.services.account.activity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Date object holding information about the activity date.
 * 
 * API Call: /api/accounts/{id}/activity
 * 
 * JSON Object Example:
 * 
 * "dates": {
 *        "deliverBy": {
 *            "date": "2012-10-06T00:00:00Z",
 *            "formattedDate": "10/06/2012"
 *       }
 *    }
 * 
 * @author jthornton
 *
 */
public class ActivityDateDetail implements Serializable{

	/**Unique identifier for the class*/
	private static final long serialVersionUID = 3450893104602755105L;

	/**String holding the unformatted date*/
	@JsonProperty("date")
	public String date;

	/**String holding the date classification*/
	@JsonProperty("dateClassifier")
	public String classifier;

	/**String holding the formatted date*/
	@JsonProperty("formattedDate")
	public String formattedDate;

}
