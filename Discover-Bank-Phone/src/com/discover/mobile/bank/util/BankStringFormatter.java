package com.discover.mobile.bank.util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.Activity;
import android.util.Log;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.account.activity.ActivityDetail;
import com.discover.mobile.common.DiscoverActivityManager;
import com.google.common.base.Strings;

public class BankStringFormatter {
	public final static String EMPTY_DOLLAR = "$0.00";

	/**String representing a negative*/
	public static final String NEGATIVE = "-";

	/**String representing a dollar sign*/
	public static final String DOLLAR = "$";

	/**
	 * Convert the string amount to a dollar amount
	 * @param dollar - dollar amount
	 * @return the dollar amount in string form
	 */
	public static String convertToDollars(final String dollar){
		if(null != dollar){
			try {
				final double amount = Double.parseDouble(dollar);
				String value = NumberFormat.getCurrencyInstance(Locale.US).format(amount);

				if( value.startsWith("(") ) {
					value = "-" + value.substring(1, value.length()-1);
				}

				return value;
			} catch(final Exception ex) {
				return EMPTY_DOLLAR;
			}
		} else{
			return EMPTY_DOLLAR;
		}
	}
	
	/**
	 * Convert the date from the format dd/MM/yyyy to dd/MM/yy
	 * @param date - date to be converted
	 * @return the converted date
	 */
	public static String convertDate(final String date){
		final SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		final SimpleDateFormat tableFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

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
		double amount = (double)cents/100;
		final StringBuilder formattedString = new StringBuilder();

		//If negative, make positive
		if(amount < 0){
			amount *= -1;
			formattedString.append("-");
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
			formattedCurrency.append("-");
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
			final String endingIn = currentActivity.getResources().getString(R.string.account_ending_in);
			accountEndingInBuilder.append(endingIn);
			accountEndingInBuilder.append(" ");
			if(!Strings.isNullOrEmpty(accountNumber))
				accountEndingInBuilder.append(accountNumber);
			else {
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
		
		final SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
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
		if(!Strings.isNullOrEmpty(value)){
			final StringBuilder strBuilder = new StringBuilder();
			strBuilder.append("(");
			strBuilder.append(value.substring(0, 3));
			strBuilder.append("-");
			strBuilder.append(value.substring(3));
			strBuilder.append(")");
			return strBuilder.toString();
		} else{
			return "";
		}
	}
}
