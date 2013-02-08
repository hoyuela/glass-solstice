package com.discover.mobile.bank.account;

import java.text.NumberFormat;
import java.util.Locale;

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
}
