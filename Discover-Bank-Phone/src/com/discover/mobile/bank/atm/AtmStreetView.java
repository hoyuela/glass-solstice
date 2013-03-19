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

public class AtmStreetView{

	/**Lat string*/
	private String lat;

	/**Lon string*/
	private String lon;

	/**Web view*/
	private final WebView web;

	/**The ProgressBar that is shown while the web view loads its content */
	private final ProgressBar loadingSpinner;

	/**Url to get the street view*/
	private static final String URL = "https://asys.discoverbank.com/api/content/atm/streetview.html?lat=%s&lng=%s";

	/**
	 * 
	 * @param web - web view for the layout
	 * @param loadingSpinner - spinner for the layout
	 */
	public AtmStreetView(final WebView web, final ProgressBar loadingSpinner){
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
		web.setBackgroundColor(Color.TRANSPARENT);
		final WebSettings webSettings = web.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setRenderPriority(RenderPriority.HIGH);
		web.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(final WebView view, final String url) {
				super.onPageFinished(view, url);
				loadingSpinner.setVisibility(View.GONE);
				web.setVisibility(View.VISIBLE);
				loadingSpinner.clearAnimation();
			}
		});
	}

	/**
	 * Load the data from the bundle and display the view
	 * @param bundle - bundle containging the data
	 */
	public void loadStreetView(final Bundle bundle){
		lat = Double.toString(bundle.getDouble(BankExtraKeys.STREET_LAT));
		lon = Double.toString(bundle.getDouble(BankExtraKeys.STREET_LON));
		setupWebView(String.format(URL, lat, lon));
	}

	public void bundleData(final Bundle outState){
		outState.putDouble(BankExtraKeys.STREET_LAT, Double.parseDouble(lat));
		outState.putDouble(BankExtraKeys.STREET_LON, Double.parseDouble(lon));
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
}
