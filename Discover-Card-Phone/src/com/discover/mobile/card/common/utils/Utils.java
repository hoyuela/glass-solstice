package com.discover.mobile.card.common.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

/**
 * This class is Util class and will contain Utility functions
 * @author cts
 *
 */
public class Utils {
	
	static ProgressDialog progressBar;

	/**
	 * 
	 * Simple utility method to check whether the internet connection is
	 * available or not.
	 * 
	 * @param context
	 *            - Application context
	 * @return return either true/false based on availability of the network
	 */
	
	private static final String ID_PREFIX = "%&(()!12["; //$NON-NLS-1$
	
	/**
	 * prevent the object creation 
	 */
	private Utils()
	{
		throw new UnsupportedOperationException("This class is non-instantiable");
	}
	
	/**
	 * get the String resource associated with particular id
	 * @param currentContext 
	 * @param id for which string value is to be returned
	 * @return String value
	 */
	public static  String getStringResource(final Context currentContext, int id) {		
		String value = currentContext.getString(id);			
	      return value;
	
	}
	
	/**
	 * This method returns the Progress Dialog
	 * @param context Activity Context
	 * @param strTitle Progress Dialog Title
	 * @param strMessage Progress Dialog Message
	 * @return Progress Dialog
	 */
	public static ProgressDialog getProgressDialog(Context context, String strTitle, String strMessage)
	{
		ProgressDialog progressDialog;
		progressDialog = ProgressDialog.show(context, strTitle, strMessage);
		return progressDialog;
	}
	
	/**
	 * Hashing for the device identifiers
	 * @param toHash String vlaue
	 * @return Hashed String
	 * @throws NoSuchAlgorithmException
	 */
	public  static String getSha256Hash(final String toHash) throws NoSuchAlgorithmException {
		final String safeToHash = toHash == null ? ID_PREFIX : ID_PREFIX + toHash;

		final MessageDigest digester = MessageDigest.getInstance("SHA-256");
		final byte[] preHash = safeToHash.getBytes(); // TODO consider
														// specifying charset

		// Reset happens automatically after digester.digest() but we don't know
		// its state beforehand so call reset()
		digester.reset();
		final byte[] postHash = digester.digest(preHash);

		return convertToHex(postHash);
	}

	/**
	 * This method converts byte data to hex
	 * @param data
	 * @return String
	 */
	private static String convertToHex(final byte[] data) {
		return String.format("%0" + data.length * 2 + 'x', new BigInteger(1, data));
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
	
	/**
	 * Set view invisible
	 * @param v view
	 */
	public final static void setViewGone(View v) {
		if(v != null)
			v.setVisibility(View.GONE);
	}
	
	/**
	 * Set the view visible
	 * @param v view
	 */
	public final static void setViewVisible(View v) {
		if(v != null)
			v.setVisibility(View.VISIBLE);
	}
	
	public static void showSpinner(Context context)
	{
		progressBar = new ProgressDialog(context);
		progressBar.setCancelable(false);
		progressBar.setMessage("Loading...");
		progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressBar.setTitle("Discover");
		progressBar.show();
	}
	
	public static void hideSpinner()
	{
		if(progressBar.isShowing())
		progressBar.dismiss();
	}

}
