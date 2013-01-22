package com.discover.mobile.common;

import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.discover.mobile.common.auth.AccountDetails;

public final class CommonMethods {
	private final static String TAG = CommonMethods.class.getSimpleName();

	public final static void setViewGone(View v) {
		v.setVisibility(View.GONE);
	}

	public final static void setViewVisible(View v) {
		v.setVisibility(View.VISIBLE);
	}

	public final static void setViewInvisible(final View v) {
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
			final int text, final Context context) {
		label.setText(context.getResources().getString(text));
		setViewVisible(label);
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
		} else {
			Log.e(TAG, "ERROR : Attempting to dial a null number");
		}

	}

	/**
	 * Determines whether or not the current account's card type is a Miles
	 * Escape, or not.
	 * 
	 * @param accountDetails
	 * @return true if a Miles Escape, false otherwise.
	 */
	public final static boolean isEscapeCard(AccountDetails accountDetails) {

		// No Outage
		if (!accountDetails.cardProductGroupOutageMode) {
			if (accountDetails.cardProductGroupCode
					.equalsIgnoreCase(AccountCodes.CPGC_ESCAPE)) {
				return true;
			}
			return false;
		}

		// Outage - manual discernment of type
		if (accountDetails.cardType
				.equalsIgnoreCase(AccountCodes.CARD_TYPE_PERSONAL)
				&& accountDetails.incentiveCode
						.equalsIgnoreCase(AccountCodes.INCENTIVE_CODE_ESC)
				&& accountDetails.incentiveTypeCode
						.equalsIgnoreCase(AccountCodes.INCENTIVE_TYPE_MI2)) {
			return true;
		}
		return false;
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
	 * Determines whether or not the current account's card type is a Cashback
	 * Rewards card.
	 * 
	 * @param accountDetails
	 * @return true if a Cashback, false otherwise.
	 */
	public final static boolean isCashbackCard(AccountDetails accountDetails) {

		if (accountDetails.incentiveTypeCode
				.equalsIgnoreCase(AccountCodes.INCENTIVE_TYPE_CBB)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Takes a string and adds commas ',' every three character places from the
	 * right. Used for formatting natural numbers with thousand commas.
	 * 
	 * @param str
	 * @return Formatted string with commas.
	 */
	public final static String insertCommas(String str) {
		if (str.length() < 4) {
			return str;
		}

		return insertCommas(str.substring(0, str.length() - 3)) + ","
				+ str.substring(str.length() - 3, str.length());
	}

	private CommonMethods() {
		throw new UnsupportedOperationException(
				"This class is non-instantiable"); //$NON-NLS-1$
	}
}
