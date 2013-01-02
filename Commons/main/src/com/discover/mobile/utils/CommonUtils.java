package com.discover.mobile.utils;

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
	
}
