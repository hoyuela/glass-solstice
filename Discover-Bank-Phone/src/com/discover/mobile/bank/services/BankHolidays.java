package com.discover.mobile.bank.services;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class used to represent list of Bank Holidays
 * 
 * Sample of JSON response:
 * 
 * [
	    "2011-12-26T00:00:00Z",
	    "2012-01-02T00:00:00Z",
	    "2012-01-16T00:00:00Z",
	    "2012-02-20T00:00:00Z",
	    "2012-05-28T00:00:00Z",
	    "2012-07-04T00:00:00Z",
	    "2012-09-03T00:00:00Z",
	    "2012-10-08T00:00:00Z",
	    "2012-11-12T00:00:00Z",
	    "2012-11-22T00:00:00Z",
	    "2012-12-25T00:00:00Z",
	    "2013-01-01T00:00:00Z",
	    "2013-01-21T00:00:00Z",
	    "2013-02-18T00:00:00Z",
	    "2013-05-27T00:00:00Z",
	    "2013-07-04T00:00:00Z",
	    "2013-09-02T00:00:00Z",
	    "2013-10-14T00:00:00Z",
	    "2013-11-11T00:00:00Z",
	    "2013-11-28T00:00:00Z",
	    "2013-12-25T00:00:00Z",
	    "2014-01-01T00:00:00Z",
	    "2014-01-20T00:00:00Z",
	    "2014-02-17T00:00:00Z",
	    "2014-05-26T00:00:00Z",
	    "2014-07-04T00:00:00Z",
	    "2014-09-01T00:00:00Z",
	    "2014-10-13T00:00:00Z",
	    "2014-11-11T00:00:00Z",
	    "2014-11-27T00:00:00Z",
	    "2014-12-25T00:00:00Z"
	]
 * @author henryoyuela
 *
 */
public class BankHolidays implements Serializable {
	/**
	 *  Auto-generated serial UID which is used to serialize and de-serialize BankHolidays objects
	 */
	private static final long serialVersionUID = -5637404253512037694L;

	/**
	 * TAG used for printing logs into Android Logcat
	 */
	private static final String TAG = BankHolidays.class.getSimpleName();
	
	
	@JsonProperty("links")
	public List<String> holidays;
	
	/**
	 * Method used to convert an ISO8601 date formatted string into a Date object.
	 * 
	 * @param date String with an IS08601 formatted date.
	 * 
	 * @return Date object
	 */
	private Date getPaymentDate(final String date) {
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		Date ret = null;
		try {
		    ret = sdf.parse(date);
		} catch (final ParseException e) {
			if( Log.isLoggable(TAG, Log.ERROR)) {
				Log.e(TAG, "Unable to convert IS08601 date to Date object");
			}
		}
		
		return ret;
	}
	
	/**
	 * @return Returns an array of Date objects representing Bank Holidays.
	 */
	public ArrayList<Date> getDates() {
		final ArrayList<Date> dates = new ArrayList<Date>();
		
		if( holidays != null ) {
			for( final String holiday : holidays ) {
				dates.add(getPaymentDate(holiday));
			}
		}
		
		return dates;
	}
}