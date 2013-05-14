package com.discover.mobile.common.utils;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * A common place to put utilities that can and will be used across the app on both phone and tablet
 * @author jthornton
 *
 */
public final class CommonUtils {
	
	/**Static int for the minimum length of a phone number*/
	private static final int PHONE_NUMBER_MIN = 10;
	/** Index of the first dash "-" in a phone number. */
	private static final int PHONE_DASH_FIRST = 3;
	/** Index of the second dash "-" in a phone number. */
	private static final int PHONE_DASH_SECOND = 6;
	
	/** Number of characters between spaces in an account number */
	private static final int ACCOUNT_BLOCK_LENGTH = 4;
	
	/** Number of cents in a dollar */
	private static final int CENTS_IN_DOLLAR = 100;

	private CommonUtils() {
		throw new AssertionError();
	}
	
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
		return String.format("%s-%s-%s", number.substring(0, PHONE_DASH_FIRST), 
				number.substring(PHONE_DASH_FIRST, PHONE_DASH_SECOND), 
				number.substring(PHONE_DASH_SECOND, PHONE_NUMBER_MIN));
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
		final Calendar calendarValue = Calendar.getInstance();
		calendarValue.set(Calendar.YEAR, year);
		calendarValue.set(Calendar.MONTH, month);
		calendarValue.set(Calendar.DAY_OF_MONTH, day);

		return new SimpleDateFormat("MM/dd/yy", Locale.getDefault()).format(calendarValue.getTime());
	}

	/**
	 * Returns a date formatted as {@code YYYY-MM-ddTHH:MM:ssZ}, (e.g.
	 * 2012-10-06T00:00:00Z). This is required for some Bank services.
	 * 
	 * @param month
	 * @param day
	 * @param year
	 * @return
	 */
	public static String getServiceFormattedISO8601Date(final int month,
			final int day, final int year) {
		final Calendar calendarValue = Calendar.getInstance();
		calendarValue.set(Calendar.YEAR, year);
		calendarValue.set(Calendar.MONTH, month);
		calendarValue.set(Calendar.DAY_OF_MONTH, day);

		final StringBuilder sb = new StringBuilder();
		sb.append(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendarValue.getTime()));
		sb.append("T00:00:00Z");
		
		return sb.toString();
	}
	
	/**
	 * Return a formatted date for given month and year integer values. The formatted date is in the form
	 * mm/yy
	 * @param month an integer representation of the month
	 * @param year an integer representation of the year
	 * @return a string representation of the formatted month day and year as mm/yy
	 */
	public static String getFormattedDate(final int month, final int year) {
		final Calendar calendarValue = Calendar.getInstance();
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
	public final static String getStringWithSpacesEvery4Characters(final String stringWithoutSpaces) {
		if (stringWithoutSpaces != null && stringWithoutSpaces.length() >= ACCOUNT_BLOCK_LENGTH) {
			return stringWithoutSpaces.substring(0, ACCOUNT_BLOCK_LENGTH)
					+ StringUtility.SPACE
					+ getStringWithSpacesEvery4Characters(stringWithoutSpaces
							.substring(ACCOUNT_BLOCK_LENGTH));
		} 

		return stringWithoutSpaces;
	}
	

	/**
	 * Search through a String and remove any spaces
	 */
	public final static String getSpacelessString(final String stringWithSpaces) {
		String stringWithNoSpaces = stringWithSpaces;

		if (stringWithSpaces != null) {
			stringWithNoSpaces = stringWithSpaces.replace(StringUtility.SPACE, StringUtility.EMPTY);
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
			final Intent dialNumber = new Intent(Intent.ACTION_DIAL);

			dialNumber.setData(Uri.parse("tel:" + number));

			callingContext.startActivity(dialNumber);
		}
		return;
	}

	public final static void setViewGone(final View v) {
		if(v != null){
			v.setVisibility(View.GONE);
		}
	}

	public final static void setViewVisible(final View v) {
		if(v != null){
			v.setVisibility(View.VISIBLE);
		}
	}

	public final static void setViewInvisible(final View v) {
		if(v != null){
			v.setVisibility(View.INVISIBLE);
		}
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

	/**
	 * This takes a String that is formatted as a form of currency (e.g.
	 * 1244.500, $1244.50, $1,244.5) and returns an int formatted as Bank
	 * services use it (e.g. 124450). 
	 * 
	 * @param amount
	 * @return Integer representation of a currency amount
	 */
	public final static int formatCurrencyStringAsBankInt(final String amount) {
		
		String newAmount = formatCurrencyAsStringWithoutSign(amount);
		newAmount = newAmount.replaceAll(StringUtility.COMMA, "");
		
		double d;
		try {
			d = Double.parseDouble(newAmount);
		} catch (final Exception e) {
			d = 0.0f;
		}
		
		return (int)(d * CENTS_IN_DOLLAR);
	}
	
	/**
	 * This takes a String that is formatted as a form of currency (e.g.
	 * 1244.500, $1244.50, $1,244.5) and returns a String formatted without '$' (e.g. 1,244.50).
	 * 
	 * @param amount
	 * @return String representation of a currency amount without '$'. Returns
	 *         "0.00" if an errors occurs.
	 */
	public final static String formatCurrencyAsStringWithoutSign(final String amount) {
		// Remove special characters before parsing
		String newAmount = amount.replaceAll("\\$", "");
		newAmount = newAmount.replaceAll(StringUtility.COMMA, "");
		
		double d;
		try {
			d = Double.parseDouble(newAmount);
		} catch (final Exception e) {
			d = 0.0f;
		}
		
		String outAmount = NumberFormat.getCurrencyInstance(Locale.US).format(d);
		outAmount = outAmount.replaceAll("\\$", "");
		
		return outAmount;
	}
	
	/**
	 * This method can be used to reset the tiling of a background image if it is not getting tiled correctly.
	 * @param view a view which contains a tiled background image.
	 */
	public final static void fixBackgroundRepeat(final View view) {
		if( view != null ) {
		    final Drawable bg = view.getBackground();
			if (bg instanceof BitmapDrawable) {
				final BitmapDrawable bmp = (BitmapDrawable) bg;
				bmp.mutate(); // make sure that we aren't sharing state anymore
				bmp.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
			}
		}
	}
}
