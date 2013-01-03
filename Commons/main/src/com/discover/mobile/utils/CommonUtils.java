package com.discover.mobile.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.view.View;

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
		if(number == null || number.length() < PHONE_NUMBER_MIN){return new String();}
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
	
	/**
	 * Returns a String representation of the passed String resource with its last character removed.
	 * Used when setting the title of a date picker dialog. The Strings that are used include an ugly colon that 
	 * doesnt make sense in a popup dialog.
	 * 
	 * @param res A String resource.
	 * @return A String representation of the passed resource with its last character removed.
	 */
	public static String removeLastChar(final String subString) {
		return subString.substring(0, subString.length() - 1);
	}
	
	/**
	 * Show a view.
	 * @param v the view you want to show.
	 */
	public static void showLabel(final View v){
		v.setVisibility(View.VISIBLE);
	}
	
	/**
	 * Hide a view.
	 * @param v the view you want to hide
	 */
	public static void hideLabel(final View v){
		v.setVisibility(View.GONE);
	}
	
}
