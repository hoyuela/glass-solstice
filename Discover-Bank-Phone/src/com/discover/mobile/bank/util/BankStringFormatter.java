package com.discover.mobile.bank.util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Activity;
import android.util.Log;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.account.activity.ActivityDetail;
import com.discover.mobile.common.DiscoverActivityManager;
import com.google.common.base.Strings;

public final class BankStringFormatter {
	private static final String TAG = BankStringFormatter.class.getSimpleName();
	
	private BankStringFormatter() {
		Log.e(TAG, "Cannot create instance of " + this.getClass().getSimpleName());
	}
	
	public static final String EMPTY_DOLLAR = "$0.00";
	
	/**String representing a negative*/
	public static final String NEGATIVE = "-";

	/**String representing a dollar sign*/
	public static final String DOLLAR = "$";
	
	private static final String DATE_YYYY_MM_DD = "yyyy-MM-dd";
	private static final String DATE_MM_DD_YYYY = "MM/dd/yyyy";
	
	/**
	 * Convert the date from the format dd/MM/yyyy to dd/MM/yy
	 * @param date - date to be converted
	 * @return the converted date
	 */
	public static String convertDate(final String date){
		final SimpleDateFormat serverFormat = new SimpleDateFormat(DATE_YYYY_MM_DD, Locale.US);
		final SimpleDateFormat tableFormat = new SimpleDateFormat(DATE_MM_DD_YYYY, Locale.US);

		try{
			return tableFormat.format(serverFormat.parse(date));
		} catch (final ParseException e) {
			return date;
		}
	}

	/**
	 * Convert the string amount to a dollar amount
	 * @param cents - dollar amount
	 * @return the dollar amount in string form
	 */
	public static String convertCentsToDollars(final String cents){
		if(cents.contains(DOLLAR)){return cents;}
		return convertCentsToDollars(Integer.parseInt(cents));
	}
	

	/**
	 * Convert the string amount to a dollar amount
	 * @param cents - dollar amount
	 * @return the dollar amount in string form
	 */
	public static String convertCentsToDollars(final int cents){
		final int centsPerDollar = 100;
		double amount = (double)cents/centsPerDollar;
		final StringBuilder formattedString = new StringBuilder();

		//If negative, make positive
		if(amount < 0){
			amount *= -1;
			formattedString.append(NEGATIVE);
		}

		formattedString.append(NumberFormat.getCurrencyInstance(Locale.US).format(amount));

		return formattedString.toString();
	}
	
	/**
	 * Returns a formatted String in US currency for a given floating point value represented with a String.
	 * 
	 * @param dollars A floating point value represented as a String
	 * @return a US currency for the given dollar amount.
	 */
	public static String convertStringFloatToDollars(final String dollars) {
		final StringBuilder formattedCurrency = new StringBuilder();
		double amount = Double.parseDouble(dollars);
		
		//If negative, make positive
		if(amount < 0){
			amount *= -1;
			formattedCurrency.append(NEGATIVE);
		}	
		
		formattedCurrency.append(NumberFormat.getCurrencyInstance(Locale.US).format(amount));
		return formattedCurrency.toString();
	}
	
	/**
	 * Returns a String of the format "Account Ending In n" where n is the provided account number.
	 * @param accountNumber an account number, expected to be the last 4 digits of an account number.
	 * @return a formatted string in the form "Account Ending In n"
	 */
	public static String getAccountEndingInString(final String accountNumber) {
		final String tag = BankStringFormatter.class.getSimpleName();
		final Activity currentActivity = DiscoverActivityManager.getActiveActivity();
		final StringBuilder accountEndingInBuilder = new StringBuilder("");
		if(R.string.account_ending_in != 0){
			final String endingIn = currentActivity.getResources().getString(R.string.account_ending_in_first_two_caps);
			accountEndingInBuilder.append(endingIn);
			accountEndingInBuilder.append(" ");
			if(!Strings.isNullOrEmpty(accountNumber)){
				accountEndingInBuilder.append(accountNumber);
			}else {
				accountEndingInBuilder.append("VOID");
				Log.e(tag, "Error formatting 'account ending in' String. No account number provided to method.");
			}
		}
		return accountEndingInBuilder.toString();

	}
	
	/**
	 * Returns a String which is a formatted date in the form of mm/dd/yy from a formatted date String passed as the 
	 * parameter.
	 * @param longDate a formatted date
	 * @return an mm/dd/yy representation of the parameter.
	 */
	public static String getFormattedDate(final String longDate) {
		String formattedDate = "";
		
		if(longDate.contains(ActivityDetail.DATE_DIVIDER)){
			formattedDate = longDate.split(ActivityDetail.DATE_DIVIDER)[0];
		}
		
		final SimpleDateFormat serverFormat = new SimpleDateFormat(DATE_YYYY_MM_DD, Locale.US);
		final SimpleDateFormat tableFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);

		try{
			formattedDate = tableFormat.format(serverFormat.parse(formattedDate));
		} catch (final ParseException e) {
			formattedDate = longDate;
		}
		
		return formattedDate;
	}

	/**
	 * Method formats the value provided to have parentheses around it. This method is used
	 * for formatting the last four digits of a bank with parentheses.  
	 * @param value Last four digits of an account number.
	 * @return
	 */
	public static String convertToAccountEnding(final String value){
		if(!Strings.isNullOrEmpty(value))
		{
			final String formattedEnding = "(..." + value + ")";
			return formattedEnding;
		} 
		else
		{
			return "";
		}
	}
	
	/**
	 * Capitalizes the first letter of a given String.
	 * @return {@link String} with the first letter converted to upper case (or null if the given String was null)
	 */
	public static String capitalize(final String string) {
		if (Strings.isNullOrEmpty(string)) {
			return string;
		}
		return string.length() > 1 ? string.substring(0, 1).toUpperCase(Locale.US) + string.substring(1) 
				: string.toUpperCase(Locale.US); 
	}
	
	/**
	 *
	 * @param formattedDate a String in the format mm/dd/yyyy
	 * @return a String in the format yyyy-MM-dd'T'HH:mm:ss.SSSZ in the eastern time zone
	 */
	public static String convertToISO8601Date(final String formattedDate, final boolean useMilliSecs ) {
		String selectedDate = "";

		if(!Strings.isNullOrEmpty(formattedDate)) {
			final String easternTime = "America/New_York";
			selectedDate = formattedDate;
			
			final SimpleDateFormat chosenDateFormat = new SimpleDateFormat(DATE_MM_DD_YYYY, Locale.getDefault());
			chosenDateFormat.setTimeZone(TimeZone.getTimeZone(easternTime));
			
			final SimpleDateFormat submissionDateFormat;
			
			if( useMilliSecs ) {
				submissionDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault() );
			} else {
				submissionDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault() );
			}
			
			submissionDateFormat.setTimeZone(TimeZone.getTimeZone(easternTime));
			
			try {							
				final Date unformattedDate = chosenDateFormat.parse(selectedDate);
				selectedDate = submissionDateFormat.format(unformattedDate);
			} catch (final ParseException e) {
				Log.e(TAG, "Could not format date : " + e);
			}
			
		}
		
		return selectedDate;
	}
	
	
	/**
	 * Formats date as MM/dd/YYYY.
	 * 
	 * @param year
	 * @param month
	 *            formatted 1-12 (i.e. not 0 for January)
	 * @param day
	 * @return formatted date
	 */
	public static String formatDate(final String year, final String month, final String day) {
		final StringBuilder sb = new StringBuilder();
		sb.append(month); 
		sb.append('/');
		sb.append(day); 
		sb.append('/');
		sb.append(year); 
		return sb.toString();
	}
	
	/**
	 * Format the day of the month
	 * @param value- value to format
	 * @return the formatted value
	 */
	public static String formatDayOfMonth(final Integer dayOfMonth){
		String valueString = dayOfMonth.toString();
		final int minDoubleDigitDay = 10;
		
		final boolean isSingleDigitDay = dayOfMonth < minDoubleDigitDay;
		
		if (isSingleDigitDay){
			valueString = "0" + valueString;
		}
		
		return valueString;
	}
	
}
