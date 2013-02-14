package com.discover.mobile.common.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * A common place to put utilities that can and will be used across the app on both phone and tablet
 * @author jthornton
 *
 */
public class CommonUtils {
	
	/**Static int for the minimum length of a phone number*/
	private static int PHONE_NUMBER_MIN = 10;

	/**
	 * Convert the simple number into a phone number string
	 * 
	 * If the number is not at least 10 digits in length or it is null this will return an empty string
	 * 
	 * @param number - number to be changed
	 * @return the formatted phone number
	 */
	public static String toPhoneNumber(final String number){
		if(number == null || number.length() < PHONE_NUMBER_MIN){return "";}
		return String.format("%s-%s-%s", number.substring(0, 3), number.substring(3, 6), number.substring(6, 10));
	}
	
	/**
	 * Return a formatted date for given month day and year integer values. The formatted date is in the form
	 * mm/dd/yy
	 * @param month an integer representation of the month
	 * @param day an integer representation of the day of the month
	 * @param year an integer representation of the year
	 * @return a string representation of the formatted month day and year as mm/dd/yy
	 */
	public static String getFormattedDate(final int month, final int day, final int year) {
		Calendar calendarValue = Calendar.getInstance();
		calendarValue.set(Calendar.YEAR, year);
		calendarValue.set(Calendar.MONTH, month);
		calendarValue.set(Calendar.DAY_OF_MONTH, day);

		return new SimpleDateFormat("MM/dd/yy", Locale.getDefault()).format(calendarValue.getTime());
	}
	
	/**
	 * Return a formatted date for given month and year integer values. The formatted date is in the form
	 * mm/yy
	 * @param month an integer representation of the month
	 * @param year an integer representation of the year
	 * @return a string representation of the formatted month day and year as mm/yy
	 */
	public static String getFormattedDate(final int month, final int year) {
		Calendar calendarValue = Calendar.getInstance();
		calendarValue.set(Calendar.YEAR, year);
		calendarValue.set(Calendar.MONTH, month);
		
		return new SimpleDateFormat("MM/yy", Locale.getDefault()).format(calendarValue.getTime());
	}
	
}
