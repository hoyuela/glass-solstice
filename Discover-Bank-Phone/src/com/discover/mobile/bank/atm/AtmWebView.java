/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.atm;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.services.BankUrlManager;
import com.google.common.base.Strings;

/**
 * Class controlling the web view and anything that needs to be loaded into it.
 * @author jthornton
 *
 */
public class AtmWebView{

	/**Key for getting the reporting ATM boolean from the bundle*/
	private static final String REPORTING = "reporting";

	/**Lat string*/
	private String lat;

	/**Lon string*/
	private String lon;

	/**Web view*/
	private final WebView web;

	/**The ProgressBar that is shown while the web view loads its content */
	private final ProgressBar loadingSpinner;

	/**Boolean used to indicate if the webview has street view or report atm*/
	private boolean isReportingAtm;

	/**ATM ID String */
	private String atmID;

	/**
	 * 
	 * @param web - web view for the layout
	 * @param loadingSpinner - spinner for the layout
	 */
	public AtmWebView(final WebView web, final ProgressBar loadingSpinner){
		this.web = web;
		this.loadingSpinner = loadingSpinner;
	}

	/**
	 * Setup the web view to load the terms and conditions URL,
	 * set the background of it to be transparent so we can see the normal app background
	 * behind the terms text,
	 * Then set the web view's WebViewClient to hide the loading spinner
	 * upon completing loading of the terms content.
	 */
	@SuppressLint("NewApi")
	private void setupWebView(final String url) {
		web.loadUrl(url);
		final WebSettings webSettings = web.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setRenderPriority(RenderPriority.HIGH);
		web.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(final WebView view, final String url) {
				super.onPageFinished(view, url);
				loadingSpinner.setVisibility(View.GONE);
				web.setVisibility(View.VISIBLE);
				web.setBackgroundResource(R.color.transparentGray);
				web.requestFocus(View.FOCUS_DOWN);
				loadingSpinner.clearAnimation();
			}

			@Override
			public boolean shouldOverrideUrlLoading(final WebView  view, final String  url){
				if(url.contains(BankUrlManager.getBankGoogleTermsUrl()) || 
				   url.contains(BankUrlManager.getBankGoogleReportUrl())) {
					BankConductor.navigateToBrowser(R.string.atm_browser_title, R.string.atm_browser_body, url);
					return true;
				}
				return false;
			}
		});
	}

	/**
	 * Clear the web view
	 */
	public void clearWebview(){
		web.loadUrl("javascript:document.open();document.close();");
	}

	/**
	 * Load the report an ATM URL
	 */
	public void reportAtm(final String id){
		isReportingAtm = true;
		atmID = id;
		setupWebView(BankUrlManager.getBankAtmReportUrl().replace("{0}", atmID));
		web.setVisibility(View.VISIBLE);
		web.setBackgroundColor(Color.WHITE);
	}

	/**
	 * Load the data from the bundle and display the view
	 * @param bundle - bundle containging the data
	 */
	public void loadStreetView(final Bundle bundle){
		isReportingAtm = bundle.getBoolean(REPORTING, false);
		if(isReportingAtm){
			reportAtm(bundle.getString(BankExtraKeys.ATM_ID));
			return;
		}
		lat = Double.toString(bundle.getDouble(BankExtraKeys.STREET_LAT));
		lon = Double.toString(bundle.getDouble(BankExtraKeys.STREET_LON));
		web.setBackgroundResource(R.drawable.light_gray_bkgrd);
		web.setVisibility(View.VISIBLE);
		setupWebView(String.format(BankUrlManager.getStreetViewUrl(), lat, lon));
	}

	public void bundleData(final Bundle outState){
		outState.putBoolean(REPORTING, isReportingAtm);
		if(!isReportingAtm){
			if( !Strings.isNullOrEmpty(lat) && !Strings.isNullOrEmpty(lon)) {
				outState.putDouble(BankExtraKeys.STREET_LAT, Double.parseDouble(lat));
				outState.putDouble(BankExtraKeys.STREET_LON, Double.parseDouble(lon));
			}
		}else {
			outState.putString(BankExtraKeys.ATM_ID, atmID);
		}
	}

	/**
	 * Hide the street view
	 */
	public void hide(){
		web.setVisibility(View.GONE);
		loadingSpinner.setVisibility(View.GONE);
	}

	/**
	 * Show the street view
	 */
	public void show(){
		web.setVisibility(View.VISIBLE);
		loadingSpinner.setVisibility(View.VISIBLE);
	}

	/**
	 * Show the web view
	 */
	public void showWebView(){
		web.setVisibility(View.VISIBLE);
	}
}
