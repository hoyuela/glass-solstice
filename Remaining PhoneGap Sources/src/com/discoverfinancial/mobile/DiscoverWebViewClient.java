package com.discoverfinancial.mobile;

import org.apache.cordova.CordovaWebViewClient;
import org.apache.cordova.DroidGap;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.util.Log;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;

/**
 * 
 * This client is the wrapper for displaying all of the local content
 * 
 */
public class DiscoverWebViewClient extends CordovaWebViewClient
{
 
	private static final String TAG = "DiscoverWebViewClient";
    
    // Variables
	protected DroidGap discoverCtx;	
	protected static String[] allowedPrefixes = null;
	private boolean dontreload = false;
	
	// Convenience Methods
	protected boolean isAllowed(String urlStr) {
		if (!urlStr.startsWith("http://") && !urlStr.startsWith("https://") )
		{
			return true;
		}
		else 
		{
			for (String prefix : allowedPrefixes) {
				if (urlStr.startsWith(prefix)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param urlStr
	 */
	protected void startUrlInChrome(final String urlStr) {
		Uri uri = Uri.parse(urlStr);
		try {
			Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uri);
			discoverCtx.startActivity(launchBrowser);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param urlStr
	 */
	protected void alertUserToOpenUrlInChrome(final String urlStr) {	
		AlertDialog.Builder builder = new AlertDialog.Builder(discoverCtx);
		builder.setMessage("Do you want to open this link in an extenal web browser?")
		.setCancelable(false)
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
				startUrlInChrome(urlStr);
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * Constructor 
	 * 
	 * @param ctx
	 */
	 public DiscoverWebViewClient(DroidGap ctx) {
		 super(ctx);
		 this.discoverCtx = ctx;
		 String[] prefixes = ctx.getResources().getStringArray(R.array.allowed_prefixes);
		 if(prefixes != null && prefixes.length > 0){
			 allowedPrefixes = prefixes;
			 Log.d(TAG,"DiscoverWebViewClient(): total number of allowedPrefixes="+allowedPrefixes.length);
		 }else{
			 allowedPrefixes = new String[0];
			 Log.w(TAG,"DiscoverWebViewClient(): No allowed prefixes were set!");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.cordova.CordovaWebViewClient#shouldOverrideUrlLoading(android.webkit.WebView, java.lang.String)
	 */
	public boolean shouldOverrideUrlLoading(WebView view, String url){
		boolean shouldOverrideUrlLoading = true;
		if(url.startsWith("dcrd")) {
			// move from server to local
			String split[] = url.split(":");
			url = "file:///android_asset/www/" + split[1]; //index.html";
			shouldOverrideUrlLoading = super.shouldOverrideUrlLoading(view, url);
			this.discoverCtx.clearHistory();
		} else if(isAllowed(url)) {
			shouldOverrideUrlLoading = super.shouldOverrideUrlLoading(view, url);
		} else { 
			alertUserToOpenUrlInChrome(url);
		}
		return shouldOverrideUrlLoading;
	}
    
	/*
	 * (non-Javadoc)
	 * @see org.apache.cordova.CordovaWebViewClient#onReceivedHttpAuthRequest(android.webkit.WebView, android.webkit.HttpAuthHandler, java.lang.String, java.lang.String)
	 */
	public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm){
		super.onReceivedHttpAuthRequest(view, handler, host, realm);
	}

//	public String[] jQuerySpinnerUrlList = new String[] {"https://www.discoverbank.com/m", 
//											 "https://www.discoverbank.com/bankac/loginreg/login"};
	
	/*
	 * (non-Javadoc)
	 * @see org.apache.cordova.CordovaWebViewClient#onPageStarted(android.webkit.WebView, java.lang.String, android.graphics.Bitmap)
	 */
	public void onPageStarted(WebView view, String url, Bitmap favicon){
		super.onPageStarted(view, url, favicon);
		if(!isAllowed(url) && !dontreload) {
			alertUserToOpenUrlInChrome(url);
			view.loadUrl(DiscoverMobileActivity.INITIAL_URL);
			view.clearHistory();
			view.clearView();
		    view.clearFormData();
			dontreload = true;
		}else {
			dontreload = false;
		}
	}
	/*
	 * (non-Javadoc)
	 * @see org.apache.cordova.CordovaWebViewClient#onReceivedError(android.webkit.WebView, int, java.lang.String, java.lang.String)
	 */
	public void onReceivedError(final WebView view, int errorCode, String description, String failingUrl){
		Log.v(TAG, "errorCode: " + errorCode);
		view.post(new Runnable() {
			public void run() {
				view.setVisibility(View.GONE); // do not show "web page not available" with url
			}
		});
		DiscoverMobileActivity actualCtx = (DiscoverMobileActivity) discoverCtx;
		try {
			actualCtx.spinnerOff();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			actualCtx.showFailOrRetryMsg(failingUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * if the app starts up without loading credentials
	 * 
	 * (non-Javadoc)
	 * @see org.apache.cordova.CordovaWebViewClient#onReceivedSslError(android.webkit.WebView, android.webkit.SslErrorHandler, android.net.http.SslError)
	 */
	public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
		super.onReceivedSslError(view, handler, error);		
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.cordova.CordovaWebViewClient#doUpdateVisitedHistory(android.webkit.WebView, java.lang.String, boolean)
	 */
	public void doUpdateVisitedHistory(WebView view, String url, boolean isReload){
		super.doUpdateVisitedHistory(view, url, isReload);
	}
}