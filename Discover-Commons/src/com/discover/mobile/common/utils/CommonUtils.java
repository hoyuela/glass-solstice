package com.discover.mobile.common.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
	 * Sets a given EditText's input to all lowercase characters. Useful when
	 * restricting the input of a field.
	 * 
	 * @param input
	 * @param field
	 */
	public static void setInputToLowerCase(final CharSequence input,
			final EditText field) {
		final String inputString = input.toString();
		final String lowerCaseInput = inputString.toLowerCase(Locale
				.getDefault());

		if (!inputString.equals(lowerCaseInput)) {
			field.setText(lowerCaseInput);
			field.setSelection(lowerCaseInput.length());
		}
	}
	
	/**
	 * Insert a space after every 4 characters in a String. Warning!
	 * Recursion!!! This method takes the first four characters of some string,
	 * adds a space to the end of those 4 characters and then appends it to the
	 * rest of the input string, minus those 4 beginning characters and the
	 * space.
	 */
	public final static String getStringWithSpacesEvery4Characters(
			final String stringWithoutSpaces) {
		if (stringWithoutSpaces != null && stringWithoutSpaces.length() > 3)
			return stringWithoutSpaces.substring(0, 4)
					+ " "
					+ getStringWithSpacesEvery4Characters(stringWithoutSpaces
							.substring(4));
		else
			return stringWithoutSpaces;
	}
	

	/**
	 * Search through a String and remove any spaces
	 */
	public final static String getSpacelessString(final String stringWithSpaces) {
		String stringWithNoSpaces = stringWithSpaces;

		if (stringWithSpaces != null) {
			stringWithNoSpaces = stringWithSpaces.replace(" ", "");
		}

		return stringWithNoSpaces;
	}
	
	/**
	 * Launches the android native phone dialer with a given telephone number,
	 * and awaits user's action to initiate the call.
	 * 
	 * @param number
	 *            - a String representation of a phone number to dial.
	 * @param callingContext
	 *            - When calling this method, pass it the context/activity that
	 *            called this method.
	 */
	public final static void dialNumber(final String number,
			final Context callingContext) {
		if (number != null && callingContext != null) {
			Intent dialNumber = new Intent(Intent.ACTION_DIAL);

			dialNumber.setData(Uri.parse("tel:" + number));

			callingContext.startActivity(dialNumber);
		} return;

	}

	public final static void setViewGone(View v) {
		if(v != null)
			v.setVisibility(View.GONE);
	}

	public final static void setViewVisible(View v) {
		if(v != null)
			v.setVisibility(View.VISIBLE);
	}

	public final static void setViewInvisible(final View v) {
		if(v != null)
			v.setVisibility(View.INVISIBLE);
	}

	/**
	 * Set a text label visible and assign its text value to the given string.
	 * 
	 * @param label
	 *            - A TextView to set visible and change the text of.
	 * @param text
	 *            - The String to present.
	 */
	public final static void showLabelWithText(final TextView label,
			final String text) {
		label.setText(text);
		setViewVisible(label);
	}
	
	/**
	 * Set a text label visible and assign its text value to the given string
	 * resource.
	 * 
	 * @param label
	 *            - A TextView to set visible and change the text of.
	 * @param text
	 *            - The String resource to resolve and present.
	 * @param context
	 *            - the context that is using this method.
	 */
	public final static void showLabelWithStringResource(final TextView label,
			final int text, final Activity callingActivity) {
		label.setText(callingActivity.getResources().getString(text));
		setViewVisible(label);
	}

	
}
