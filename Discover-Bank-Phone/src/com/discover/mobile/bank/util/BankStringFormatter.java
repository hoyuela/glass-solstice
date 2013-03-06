package com.discover.mobile.bank.util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

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
