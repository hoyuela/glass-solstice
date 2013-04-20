package com.discover.mobile.card.common.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.discover.mobile.card.phonegap.plugins.ResourceDownloader;

public class Utilss {

	protected static final String LOG_TAG = "Utils";

	private static final String TYPE_PDF = "application/pdf";
	private static final String TITLE_NO_DL = "No SD Card";
	private static final String MSG_NO_DL = "An SD card is required to Download PDFs.";
	private static final String TITLE_NO_PDF = "No PDF Viewer";
	private static final String MSG_NO_PDF = "A PDF Viewer was not found to view the file.";
	private static final String TITLE_ERROR = "Error";
	private static final String MSG_ERROR = "There was a problem downloading the file, please try again later.";

	/** Potential directories to save PDF file is none of the defaults exist */
	private static final ArrayList<String> DIR_LIST;
	static {
		DIR_LIST = new ArrayList<String>();
		DIR_LIST.add("/mnt/emmc/download");
		DIR_LIST.add("/mnt/emmc/downloads");
		DIR_LIST.add("/mnt/emmc/Download");
		DIR_LIST.add("/mnt/emmc/Downloads");
	}

	public static PDFObject downloadPDF(String url) {
		String directory = null;
		File downloadDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		if (downloadDir != null && downloadDir.exists()
				&& downloadDir.isDirectory() && downloadDir.canWrite()) {
			directory = downloadDir.toString();
			Log.d(LOG_TAG, "onPageStarted() using downloadDir=" + directory);
		} else if (Environment.getExternalStorageDirectory().canWrite()) {
			directory = Environment.getExternalStorageDirectory().toString();
			Log.d(LOG_TAG, "onPageStarted() using externalStorageDirectory="
					+ directory);
		} else {
			for (String dir : DIR_LIST) {
				File intStorage = new File(dir);
				if (intStorage.exists() && intStorage.isDirectory()
						&& intStorage.canWrite()) {
					directory = dir;
					Log.d(LOG_TAG, "onPageStarted() using dir=" + dir);
					break;
				}
			}
			if (directory == null) {
				// Can't find a directory to save the file.
				Log.w(LOG_TAG,
						"onPageStarted() can't find a directory to download");
				// showOkAlert(view.getContext(), TITLE_NO_DL, MSG_NO_DL);
				// return true;
				return new PDFObject(null, false, TITLE_NO_DL, MSG_NO_DL);
			}
		}
		HashMap<String, String> headers = null;
		String domain = url.substring(0, url.indexOf(".com") + 4);
		CookieSyncManager.getInstance().sync();
		// Get the cookie from cookie jar.
		String cookie = CookieManager.getInstance().getCookie(domain);
		if (cookie != null) {
			headers = new HashMap<String, String>();
			headers.put("Cookie", cookie);
			Log.d(LOG_TAG, "onPageStarted() Cookie=" + cookie);
		} else {
			Log.d(LOG_TAG, "onPageStarted() No Cookies");
		}

		Log.d(LOG_TAG, "onPageStarted. before executeDownload, directory="
				+ directory);

		boolean success = false;
		File file = null;
		try {
			file = ResourceDownloader.getInstance().executeDownload(url,
					ResourceDownloader.GET, null, headers, directory, ".pdf",
					TYPE_PDF);
			success = true;
		} catch (Exception e1) {
			Log.e(LOG_TAG, "onPageStarted() problem downloading file. message:"
					+ e1.getMessage());
			return new PDFObject(null, false, TITLE_ERROR, MSG_ERROR); 
		}
		if (success && file != null && file.exists()) {
			Uri path = Uri.fromFile(file);
			Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
			pdfIntent.setDataAndType(path, TYPE_PDF);
			pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			try {

				return new PDFObject(file, true);
			} catch (Exception e) {
				// Toast.makeText(c.getApplicationContext(), MSG_NO_PDF,
				// Toast.LENGTH_LONG).show();
				Log.w(LOG_TAG,
						"onPageStarted() Problem with launching PDF Viewer.", e);
				return new PDFObject(null, false, TITLE_NO_PDF, MSG_NO_PDF);
				// showOkAlert(view.getContext(), TITLE_NO_PDF, MSG_NO_PDF);
			}
		} else {
			Log.w(LOG_TAG, "onPageStarted() Problem downloading/saving file.");
			return new PDFObject(null, false, TITLE_ERROR, MSG_ERROR);
			// showOkAlert(view.getContext(), TITLE_ERROR, MSG_ERROR);
		}
	}

	/**
	 * 
	 * @param context
	 * @param title
	 * @param message
	 */
	public static void showOkAlert(Context context, String title, String message) {
		// We can't download the file anywhere.
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		alertDialog.setIcon(android.R.drawable.stat_notify_error);
		alertDialog.show();
	}
	
	/**
	 * Tests to see if there is a network connection (wifi or mobile network)
	 * @param cm ConnectivityManager object from activity.
	 * @return true if network connection exists, false if not.
	 */
	public static boolean isNetworkConnection(ConnectivityManager cm) {
		boolean isConnectedMobile = false;
		boolean isConnectedWifi = false;
	    if (cm == null) return true;
	    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
	    for (NetworkInfo ni : netInfo) {
	        if ("WIFI".equalsIgnoreCase(ni.getTypeName()))
	            if (ni.isConnected())
	            	isConnectedWifi = true;
	        if ("mobile".equalsIgnoreCase(ni.getTypeName()))
	            if (ni.isConnected())
	            	isConnectedMobile = true;
	    }    
	    
	    Log.d(LOG_TAG, "Wifi On: " + isConnectedWifi);
	    Log.d(LOG_TAG, "Mobile On: " + isConnectedMobile);
	    return isConnectedWifi || isConnectedMobile;
	}
}
