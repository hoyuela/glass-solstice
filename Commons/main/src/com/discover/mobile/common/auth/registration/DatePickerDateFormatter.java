package com.discover.mobile.common.auth.registration;

public class DatePickerDateFormatter {

	/**
	 * Returns a formatted date for a given month, and year, provided by a date picker. Returns it in the format
	 * of mm/yy
	 * 
	 * @param monthOfYear the month from a date picker (expected to be ranged from 0-11)
	 * @param year the year selected from a day picker
	 * @return a formatted String of mm/yy
	 */
	public static String getFormattedDate(final int monthOfYear, final int year) {
		return getFormattedMonth(monthOfYear + 1) + "/" + getFormattedYear(year);
	}
	
	/**
	 * Convienience method. Performs getFormattedMonth with specified day.
	 * 
	 * @param day a selected day from a date picker.
	 * @return a String representation of the day as dd.
	 */
	public static String getFormattedDay(final int day) {
		return getFormattedMonth(day);
	}
	/**
	 * Returns a string representation of the given month value. If its less than 10 a zero will be appended to
	 * the front of it.
	 * 
	 * @return a string represention of the date.
	 */
	public static String getFormattedMonth(final int month) {
		String monthString;
		if(month < 10) {
			monthString = "0" + month;
		}else {
			monthString = String.valueOf(month);
		}
	
		return monthString;
	}
	

	/**
	 * Returns a string representation of the last two digits of a given number. i.e. if year is 2001 it will return
	 * 01
	 * @param year an integer to get the last two numbers of.
	 * @return a String representation of the last two digits.
	 */
	public static String getFormattedYear(final int year) {
		String yearString = String.valueOf(year);
		int yearLength = yearString.length();
		
		if(yearLength > 2)
			yearString = yearString.substring(yearLength - 2, yearLength);
		
		return yearString;
	}
	
	/**
	 * Returns a formatted date for a given month, day, and year, provided by a date picker. Returns it in the format
	 * of mm/dd/yy
	 * 
	 * @param monthOfYear the month from a date picker (expected to be ranged from 0-11)
	 * @param dayOfMonth the day selected from a day picker
	 * @param year the year selected from a day picker
	 * @return a formatted String of mm/dd/yy
	 */
	public static String getFormattedDate(final int monthOfYear, final int dayOfMonth, final int year) {
		return getFormattedMonth(monthOfYear + 1) + "/" + getFormattedDay(dayOfMonth) + "/" + getFormattedYear(year);
	}
	
}
