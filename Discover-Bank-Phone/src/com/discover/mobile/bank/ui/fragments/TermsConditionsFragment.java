package com.discover.mobile.bank.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.help.PrivacyTermsType;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.utils.CommonUtils;
import com.discover.mobile.common.utils.StringUtility;
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
@SuppressLint("SetJavaScriptEnabled")
public abstract class TermsConditionsFragment extends BaseFragment implements OnClickListener{

	/**We need an api call that is avaialable in API11+ so this is defined to check against version numbers*/
	private static final int API_ELEVEN = 11;
	
	/** Delay which allows the WebView to finish drawing before manually scrolling. */
	private static final int SCROLL_DELAY = 100;
	
	/** Delay which allows the WebView to finish drawing before manually scrolling. (higher delay for Baseline devices) */
	private static final int SCROLL_DELAY_BASELINE = 300;
	
	/** Holds the last known scroll amount (percent). */
	private float scroll = 0f;
	
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

	@SuppressLint("NewApi")
	private void setupWebView(final boolean loadUrl) {
		pageLoadSuccess = true;
		final WebSettings webSettings = termsWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setRenderPriority(RenderPriority.HIGH);
		
		if (loadUrl) {
			termsWebView.loadUrl(this.getTermsUrl());
		}
		
		termsWebView.setBackgroundColor(Color.TRANSPARENT);
		termsWebView.setWebViewClient(new TermsAndConditionsWebViewClient());
		
		//Disable hardware accelerated scrolling for the web view if the current API is 11 or higher.
		//this allows the background of the web view to be transparent and not buggy on API 11+ devices.
		if(Build.VERSION.SDK_INT >= API_ELEVEN) {
			termsWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
		}
	}

	/**
	 * Display the content of the web view.
	 */
	private void showTerms() {
		termsWebView.setVisibility(View.VISIBLE);
		termsWebView.requestFocus(View.FOCUS_DOWN);
		loadingSpinner.clearAnimation();
		if(pageLoadSuccess){
			acceptButton.setEnabled(true);
		}
		if (scroll > 0) {
			// Use OS version to determine how long to delay the WebView scroll restore
			final boolean isBaseline = 
					android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB;
			// Scroll to exact previous point on page (lost on orientation change)
			WebUtility.scrollAfterDelay(termsWebView, scroll, 
					isBaseline ? SCROLL_DELAY_BASELINE : SCROLL_DELAY);
		}
	}

	@Override
	public void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		
		/**Verify webview is not null*/
		if( null != termsWebView ) {
			termsWebView.saveState(outState);
			// Since we are retaining instance, no need to store in the bundle.
			scroll = WebUtility.calculateProgression(termsWebView);
		}
		
		/**
		 * Retain instance state so that this method is not 
		 * called again until the fragment is resumed again.
		 */
		this.setRetainInstance(true);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		/**This is to allow onSaveInstanceState to be called again on rotation*/
		this.setRetainInstance(false);
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
			setupWebView(false);
			
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
	
	/**
	 * This inner class defines the WebView behavior for loading, showing data, and handling link clicks.
	 * 
	 * @author scottseward
	 *
	 */
	private class TermsAndConditionsWebViewClient extends WebViewClient {
		
		@Override
		public void onPageFinished(final WebView view, final String url) {
			super.onPageFinished(view, url);
			loadingSpinner.setVisibility(View.GONE);
			showTerms();
		}

		@Override
		public void onReceivedError(final WebView view, final int errorCode, 
															final String description, final String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			pageLoadSuccess = false;
		}
		
		/**
		 * This method handles custom hyperlink actions and enables us to launch a device browser when
		 * a user presses a web link in one of our webviews, or dial a number, or send an email or
		 * even make a navigation change in our app.
		 */
		@Override
		public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
			boolean handledLink = false;
			
			if(looksLikeWebPage(url)) {
				handledLink = startIntentFor(Intent.ACTION_VIEW, getDiscoverUrl(url));
			}else if (looksLikePhoneNumber(url)) {
				handledLink = startIntentFor(Intent.ACTION_DIAL, url);
			}else if(looksLikeEmail(url)) {
				handledLink = startIntentFor(Intent.ACTION_SENDTO, url);
			}else if(looksLikeInAppNavigation(url)) {
				handledLink = appNavigateToSection(url);
			}
			
			return handledLink;
		}
		
		/**
		 * 
		 * @param url
		 * @return if the url provided can be handled by the BankConductor to navigate the app.
		 */
		private boolean appNavigateToSection(final String url) {
			boolean willNavigate = false;

			if(url != null) {
				if(url.contains("navigateToMobilePrivacyStatement")) {
					BankConductor.navigateToPrivacyTerms(PrivacyTermsType.MobilePrivacyStatement);
					willNavigate = true;
				}
			}
			
			return willNavigate;
		}
		
		/**
		 * Starts an intent to handle a clicked link.
		 * @param action the kind of Intent to launch.
		 * @param url the URL to provide data to the intent.
		 * @return if the intent was started.
		 */
		private boolean startIntentFor(final String action, final String url) {
			boolean intentStarted = false;
			
			if(action != null && url != null) {
				final Intent intent = new Intent(action, Uri.parse(url));
				TermsConditionsFragment.this.startActivity(intent);
				intentStarted = true;
			}
			
			return intentStarted;
		}
		
		/**
		 * 
		 * @param url
		 * @return if the url should be treated like an in app navigation change that the BankConductor can handle.
		 */
		private boolean looksLikeInAppNavigation(final String url) {
			boolean looksLikeNavigation = false;
		
			if(url != null) {
				looksLikeNavigation = url.startsWith(StringUtility.METHOD);
			}
			
			return looksLikeNavigation;
		}

		/**
		 * Returns a String for the URL where the HTTP or HTTPS prefix is replaced by the com.discover.mobile prefix
		 * that will allow our app to handle the intent.
		 * @param url a web URL
		 * @return a String that can be handled by the Discover Mobile app.
		 */
		private String getDiscoverUrl(final String url) {
			String result = StringUtility.EMPTY;
			if(url != null) {
				if(url.startsWith(StringUtility.HTTPS)) {
					result = url.replaceAll(StringUtility.HTTPS, StringUtility.BROWSER_SCHEME);
				}else if (url.startsWith(StringUtility.HTTP)) {
					result = url.replaceAll(StringUtility.HTTP, StringUtility.BROWSER_SCHEME);
				}
			}
			
			return result;
		}
		
		/**
		 * 
		 * @param url
		 * @return if the url should be treated as a web page.
		 */
		private boolean looksLikeWebPage(final String url) {
			boolean looksLikePage = false;
			
			if(url != null) {
				looksLikePage = url.startsWith(StringUtility.HTTPS) || url.startsWith(StringUtility.HTTP);
			}
			return looksLikePage;
		}
		
		/**
		 * 
		 * @param url
		 * @return if the url should be treated like a phone number
		 */
		private boolean looksLikePhoneNumber(final String url) {
			boolean looksLikePhoneNumber = false;
			
			if(url != null) {
				looksLikePhoneNumber = url.startsWith(StringUtility.TEL);
			}
			
			return looksLikePhoneNumber;
		}
		
		/**
		 * 
		 * @param url
		 * @return if the url should be treated as an email address.
		 */
		private boolean looksLikeEmail(final String url) {
			boolean looksLikeEmail = false;
			
			if(url != null) {
				looksLikeEmail = url.startsWith(StringUtility.MAILTO);
			}
			
			return looksLikeEmail;
		}
	
	}
}