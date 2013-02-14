package com.discover.mobile.bank.account;

import java.text.NumberFormat;
import java.util.Locale;

import com.google.common.base.Strings;

public class BankStringFormatter {
	public final static String EMPTY_DOLLAR = "$0.00";
	
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
