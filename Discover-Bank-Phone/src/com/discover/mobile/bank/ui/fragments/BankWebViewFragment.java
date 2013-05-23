/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.ui.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.R;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.utils.CommonUtils;

/**
 * Simple web view fragment.  The fragment contains a web view and a title.  To get it to load
 * pass in the title of the fragment with the key and the url that the web view should load
 * as the arguments in the bundle.
 * 
 * @author jthornton
 *
 */
public class BankWebViewFragment extends BaseFragment{
	public static final String KEY_TITLE = "text-title";
	public static final String KEY_URL = "text-url";

	/**We need an api call that is avaialable in API11+ so this is defined to check against version numbers*/
	private static final int API_ELEVEN = 11;

	/**The ProgressBar that is shown while the web view loads its content */
	private ProgressBar loadingSpinner;

	/**The web view that displays the content for the terms of service to the user */
	private WebView webView;

	/**TextView that displays the title of the page within the fragment*/
	private TextView pageTitle;

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {

		final View view = inflater.inflate(R.layout.bank_webview_fragment, null);
		webView = (WebView)view.findViewById(R.id.web_view);
		loadingSpinner = (ProgressBar)view.findViewById(R.id.progress_bar);
		pageTitle = (TextView)view.findViewById(R.id.header_title);

		pageTitle.setText(this.getArguments().getString(KEY_TITLE));

		if (savedInstanceState != null) {
			webView.restoreState(savedInstanceState);
			setupWebView(false);
		} else {
			setupWebView(true);
		}
		
		//Disable hardware acceleration for the UI so that the dotted line gets drawn correctly.
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		} else {
			// Tiled background is often broken for older devices
			CommonUtils.fixBackgroundRepeat(view.findViewById(R.id.webview_layout));
		}

		return view;
	}

	/**
	 * Set up the web view and load the URL if it needs to.
	 * @param loadUrl - set to true if the application needs to load the url
	 */
	@SuppressLint("NewApi")
	private void setupWebView(final boolean loadUrl) {
		final WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setRenderPriority(RenderPriority.HIGH);

		if (loadUrl) {
			webView.loadUrl(this.getArguments().getString(KEY_URL));
		}

		webView.setBackgroundColor(Color.TRANSPARENT);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(final WebView view, final String url) {
				super.onPageFinished(view, url);
				loadingSpinner.setVisibility(View.GONE);
				webView.setVisibility(View.VISIBLE);
				webView.requestFocus(View.FOCUS_DOWN);
				loadingSpinner.clearAnimation();
			}

			@Override
			public void onReceivedError(final WebView view, final int errorCode, 
					final String description, final String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
			}
		});

		//Disable hardware accelerated scrolling for the web view if the current API is 11 or higher.
		//this allows the background of the web view to be transparent and not buggy on API 11+ devices.
		if(Build.VERSION.SDK_INT >= API_ELEVEN) {
			webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
		}
	}


	@Override
	public void onSaveInstanceState(final Bundle outState) {
		webView.saveState(outState);
	}

	@Override
	public int getActionBarTitle() {
		return R.string.bank_terms_privacy_n_terms;
	}

	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.PRIVACY_AND_TERMS_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return 0;
	}
}
