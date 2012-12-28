package com.discover.mobile.utils;

/**
 * A common place to put utilities that can and will be used across the app on both phone and tablet
 * @author jthornton
 *
 */
public class CommonUtils {

	/**
	 * Convert the simple number into a phone number string
	 * @param number - number to be changed
	 * @return the formatted phone number
	 */
	public static String toPhoneNumber(final String number){
		if(number == null){return new String();}
		return String.format("%s-%s-%s", number.substring(0, 3), number.substring(3, 6), number.substring(6, 10));
	}
	
}
