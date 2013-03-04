package com.discover.mobile.bank.ui.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.discover.mobile.bank.R;
import com.discover.mobile.common.BaseFragment;

/**
 * The Generic terms and conditions page for al
 * 
 * 
 * The content is loaded from a URL into a web view and allows the user to review and accept
 * the terms and conditions, or cancel and go back if they please.
 * 
 * @author scottseward, hoyuela
 *
 */
public abstract class TermsConditionsFragment extends BaseFragment implements OnClickListener{

	/**We need an api call that is avaialable in API11+ so this is defined to check against version numbers*/
	final int apiEleven = 11;

	/**The String resource that is used for the title in the Action bar*/
	int titleStringResource;

	/**The ProgressBar that is shown while the web view loads its content */
	protected ProgressBar loadingSpinner;

	/**The button that the user needs to press to accept the terms and conditions */
	protected Button acceptButton;

	/**The web view that displays the content for the terms of service to the user */
	protected WebView termsWebView;

	/**TextView that displays the title of the page within the fragment*/
	protected TextView pageTitle;

	/**
	 * Get the title text that was passed in by the previous Fragment.
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	/**
	 * Get all of the interface elements that we need to access.
	 * @param mainView
	 */
	private void loadResources(final View mainView) {
		termsWebView = (WebView)mainView.findViewById(R.id.agreement_web_view);
		acceptButton = (Button)mainView.findViewById(R.id.accept_button);
		loadingSpinner = (ProgressBar)mainView.findViewById(R.id.progress_bar);
		pageTitle = (TextView)mainView.findViewById(R.id.select_payee_title);
	}

	/**
	 * Setup the web view to load the terms and conditions URL,
	 * set the background of it to be transparent so we can see the normal app background
	 * behind the terms text,
	 * Then set the web view's WebViewClient to hide the loading spinner
	 * upon completing loading of the terms content.
	 */
	@SuppressLint("NewApi")
	private void setupWebView() {
		termsWebView.loadUrl(this.getTermsUrl());
		termsWebView.setBackgroundColor(Color.TRANSPARENT);
		termsWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(final WebView view, final String url) {
				super.onPageFinished(view, url);
				loadingSpinner.setVisibility(View.GONE);
				termsWebView.setVisibility(View.VISIBLE);
				loadingSpinner.clearAnimation();
			}
		});

		//Disable hardware accelerated scrolling for the web view if the current API is 11 or higher.
		//this allows the background of the web view to be transparent and not buggy on API 11+ devices.
		if(Build.VERSION.SDK_INT >= apiEleven) {
			termsWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
		}
	}


	/**
	 * Inflates the view and loads needed resources from the layout.
	 * Also sets up the web view and starts loading the content.
	 * @param inflater - inflater to inflate the layout
	 * @param container - container holding the group
	 * @param savedInstanceState - state of the fragment
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {

		final View mainView = inflater.inflate(R.layout.payment_terms_and_conditions, null);
		loadResources(mainView);
		setupWebView();

		/***Set the title of the page*/
		pageTitle.setText(this.getPageTitle());
		
		/**Set click listener for accept button*/
		acceptButton.setOnClickListener(this);
		
		return mainView;
	}
	
	/**
	 * Click Handler for all buttons in this fragment calls the respective callback depending
	 * on which button was clicked.
	 */
	@Override
	public void onClick(final View v) {
		if( v == acceptButton) {
			this.onAcceptClicked();
		}
	}
	
	/**
	 * Returns the URL to use for Terms and Conditions.
	 */
	public abstract String getTermsUrl();
	
	/**
	 * Method signature for Accept Button Click Handler to be implemented by sub-classes
	 */
	public abstract void onAcceptClicked();
	
	/**
	 * Method signature for retrieving the title displayed to the user within the Fragment. 
	 */
	public abstract int getPageTitle();
}