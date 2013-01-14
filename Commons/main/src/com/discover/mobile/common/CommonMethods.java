package com.discover.mobile.common;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

public final class CommonMethods {
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
	 * @param label - A TextView to set visible and change the text of.
	 * @param text - The String to present.
	 */
	public final static void showLabelWithText(final TextView label, final String text) {
		label.setText(text);
		setViewVisible(label);
	}
	
	/**
	 * Launches the android native phone dialer with a given telephone number, and awaits user's
	 * action to initiate the call.
	 * 
	 * @param number - a String representation of a phone number to dial.
	 * @param callingContext - When calling this method, pass it the context/activity that called this method.
	 */
	public final static void dialNumber(final String number, final Context callingContext) {
		Intent dialNumber = new Intent(Intent.ACTION_DIAL);
		
		dialNumber.setData(Uri.parse("tel:" + number));

		callingContext.startActivity(dialNumber);
	}
	
	private CommonMethods(){
		throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
	}
}
