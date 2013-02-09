package com.discover.mobile.bank.account;

import java.text.NumberFormat;
import java.util.Locale;

import com.google.common.base.Strings;

public class BankStringFormatter {
	/**
	 * Convert the string amount to a dollar amount
	 * @param dollar - dollar amount
	 * @return the dollar amount in string form
	 */
	public static String convertToDollars(final String dollar){
		if(null != dollar){
			final double amount = Double.parseDouble(dollar);
			return NumberFormat.getCurrencyInstance(Locale.US).format(amount);
		} else{
			return "$0.00";
		}
	}
	
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
