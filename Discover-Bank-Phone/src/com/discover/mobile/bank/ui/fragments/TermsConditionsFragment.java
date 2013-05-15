package com.discover.mobile.bank.ui.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.R;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.utils.CommonUtils;
import com.discover.mobile.common.utils.WebUtility;

/**
 * The Generic terms and conditions fragment to be used as a base class for any  of the terms and conditions
 * pages including: Payments, Transfers and Deposits. The sub-class is required to implement the following
 * methods:
 * 
 *  //Method Used to fetch the URL for downloading the Terms and Condition from the sub-class
 *  public abstract String getTermsUrl();
 *  
 *  //Method to be implemented by sub-class for handling when the user taps Accept on the screen
 *  public abstract void onAcceptClicked();
 *  
 *  //Method to be implemented by sub-class to return the title that is shown at the top of the layout.
 *  public abstract int getPageTitle();
 * 
 * The content is loaded from a URL into a web view and allows the user to review and accept
 * the terms and conditions, or cancel and go back if they please.
 * 
 * @author scottseward, hoyuela
 *
 */
public abstract class TermsConditionsFragment extends BaseFragment implements OnClickListener{

	/**We need an api call that is avaialable in API11+ so this is defined to check against version numbers*/
	private static final int API_ELEVEN = 11;
	
	/** Key for storing exact amount of WebView scroll on orientation change. */
	private static final String SCROLL_KEY = "scroll";
	
	/** Delay which allows the WebView to finish drawing before manually scrolling. */
	private static final int SCROLL_DELAY = 100;

	/**The ProgressBar that is shown while the web view loads its content */
	private ProgressBar loadingSpinner;

	/**The button that the user needs to press to accept the terms and conditions */
	private Button acceptButton;

	/**The web view that displays the content for the terms of service to the user */
	private WebView termsWebView;

	/**TextView that displays the title of the page within the fragment*/
	private TextView pageTitle;

	/**Relative Layout that show the footer at the bottom of the page with accept button*/
	private RelativeLayout footer;

	/**Divider between the content and the footer*/
	private View divider;

	/**
	 * Get all of the interface elements that we need to access.
	 * @param mainView
	 */
	private void loadResources(final View mainView) {
		termsWebView = (WebView)mainView.findViewById(R.id.agreement_web_view);
		acceptButton = (Button)mainView.findViewById(R.id.accept_button);
		loadingSpinner = (ProgressBar)mainView.findViewById(R.id.progress_bar);
		pageTitle = (TextView)mainView.findViewById(R.id.select_payee_title);
		footer = (RelativeLayout)mainView.findViewById(R.id.footer);
		divider = mainView.findViewById(R.id.footer_divider);
	}

	/**
	 * Setup the web view to load the terms and conditions URL,
	 * set the background of it to be transparent so we can see the normal app background
	 * behind the terms text,
	 * Then set the web view's WebViewClient to hide the loading spinner
	 * upon completing loading of the terms content.
	 */
	boolean pageLoadSuccess = true;

	private void setupWebView(final boolean loadUrl) {
		setupWebView(loadUrl, 0);
	}
	
	@SuppressLint("NewApi")
	private void setupWebView(final boolean loadUrl, final float scroll) {
		pageLoadSuccess = true;
		final WebSettings webSettings = termsWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setRenderPriority(RenderPriority.HIGH);
		
		if (loadUrl) {
			termsWebView.loadUrl(this.getTermsUrl());
		}
		
		termsWebView.setBackgroundColor(Color.TRANSPARENT);
		termsWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(final WebView view, final String url) {
				super.onPageFinished(view, url);
				loadingSpinner.setVisibility(View.GONE);
				termsWebView.setVisibility(View.VISIBLE);
				termsWebView.requestFocus(View.FOCUS_DOWN);
				loadingSpinner.clearAnimation();
				if(pageLoadSuccess){
					acceptButton.setEnabled(true);
				}
				if (scroll > 0) {
					// Scroll to exact previous point on page (lost on orientation change)
					WebUtility.scrollAfterDelay(termsWebView, scroll, SCROLL_DELAY);
				}
			}

			@Override
			public void onReceivedError(final WebView view, final int errorCode, final String description, final String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				pageLoadSuccess = false;
			}
		});

		//Disable hardware accelerated scrolling for the web view if the current API is 11 or higher.
		//this allows the background of the web view to be transparent and not buggy on API 11+ devices.
		if(Build.VERSION.SDK_INT >= API_ELEVEN) {
			termsWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
		}
	}

	@Override
	public void onSaveInstanceState(final Bundle outState) {
		termsWebView.saveState(outState);
		outState.putFloat(SCROLL_KEY, WebUtility.calculateProgression(termsWebView));
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
		
		if (savedInstanceState != null) {
			termsWebView.restoreState(savedInstanceState);
			setupWebView(false, savedInstanceState.getFloat(SCROLL_KEY, 0));
		} else {
			setupWebView(true);
		}

		/***Set the title of the page*/
		pageTitle.setText(this.getPageTitle());

		/**Set click listener for accept button*/
		acceptButton.setOnClickListener(this);

		CommonUtils.fixBackgroundRepeat(mainView);
		return mainView;
	}

	/**
	 * Click Handler for all buttons in this fragment calls the respective callback depending
	 * on which button was clicked.
	 */
	@Override
	public void onClick(final View v) {
		if( v.equals(acceptButton)) {
			this.onAcceptClicked();
		}
	}

	/**
	 * Method used to show or hide footer at the bottom of the page.
	 * 
	 * @param value True to show footer, false to hide
	 */
	public void showFooter( final boolean value ) {
		if( footer != null ) {
			if( value ) {
				footer.setVisibility(View.VISIBLE);
				divider.setVisibility(View.VISIBLE);
			} else {
				footer.setVisibility(View.GONE);
				divider.setVisibility(View.GONE);
			}
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