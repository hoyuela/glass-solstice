package com.discover.mobile.bank.services.payment;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * JSON object that hold information about the delivery date of the payment.
 * 
 * Example:
 * 
 *  "dates": {
 *        "deliverBy": {
 *            "date": "2012-10-06T00:00:00Z",
 *           "formattedDate": "10/06/2012"
 *       }
 *   },
 * @author jthornton
 *
 */
public class PaymentDateDetail implements Serializable{

	/**Unique id of the object*/
	private static final long serialVersionUID = -7317708580258431225L;

	/**String holding the date*/
	@JsonProperty("date")
	public String date;

	/**String holding the formatted date*/
	@JsonProperty("formattedDate")
	public String formattedDate;
}
