package com.discover.mobile.bank.paybills;

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

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.help.HelpMenuListFactory;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.services.customer.Eligibility;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.help.HelpWidget;

/**
 * The terms and conditions page for Pay Bills or Mange Payees.
 * 
 * This page presents the terms and conditions of the Bill Payment service to the user.
 * The content is loaded from a URL into a web view and allows the user to review and accept
 * the terms and conditions, or cancel and go back if they please.
 * 
 * @author scottseward
 *
 */
public class BankPayTerms extends BaseFragment{
	/**We need an api call that is available in API11+ so this is defined to check against version numbers*/
	private static final int API_ELEVEN = 11;

	/**The default title text that will be used if for some reason one is not passed in the Bundle */
	private int titleText = R.string.pay_a_bill_title;

	/**The String resource that is used for the title in the Action bar*/
	private int titleStringResource;

	/**The ProgressBar that is shown while the web view loads its content */
	private ProgressBar loadingSpinner;

	/**The button that the user needs to press to accept the terms and conditions */
	private Button acceptButton;

	/**The web view that displays the content for the terms of service to the user */
	private WebView termsWebView;

	/**
	 * Get the title text that was passed in by the previous Fragment.
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Bundle argBundle = getArguments();

		if(argBundle != null){
			titleStringResource  = argBundle.getInt(BankExtraKeys.TITLE_TEXT);
		}else if (savedInstanceState != null){
			titleStringResource  = savedInstanceState.getInt(BankExtraKeys.TITLE_TEXT);
		}

		//If there was a valid resource retrieved from the Bundle, use it.
		if(titleStringResource != 0) {
			titleText = titleStringResource;
		}
	}

	/**
	 * Get all of the interface elements that we need to access.
	 * @param mainView
	 */
	private void loadResources(final View mainView) {
		/**Help icon setup*/
		final HelpWidget help = (HelpWidget) mainView.findViewById(R.id.help);
		help.showHelpItems(HelpMenuListFactory.instance().getPayBillsHelpItems());

		termsWebView = (WebView)mainView.findViewById(R.id.agreement_web_view);
		acceptButton = (Button)mainView.findViewById(R.id.accept_button);
		loadingSpinner = (ProgressBar)mainView.findViewById(R.id.progress_bar);
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
		termsWebView.loadUrl(BankUrlManager.getBaseUrl());
		termsWebView.setBackgroundColor(Color.TRANSPARENT);
		termsWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(final WebView view, final String url) {
				super.onPageFinished(view, url);
				loadingSpinner.setVisibility(View.GONE);
				termsWebView.setVisibility(View.VISIBLE);
				loadingSpinner.clearAnimation();
				acceptButton.setEnabled(true);
			}
		});

		//Disable hardware accelerated scrolling for the web view if the current API is 11 or higher.
		//this allows the background of the web view to be transparent and not buggy on API 11+ devices.
		if(Build.VERSION.SDK_INT >= API_ELEVEN) {
			termsWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
		}
	}

	/**
	 * Set the onClickListener for the accept button to submit the accept
	 * service call.
	 */
	private void setupAcceptButton() {
		acceptButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				final Eligibility eligibility = BankUser.instance().getCustomerInfo().getPaymentsEligibility();
				BankServiceCallFactory.createAcceptTermsRequest(eligibility).submit();				
			}
		});
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
		setupAcceptButton();

		return mainView;
	}

	/**
	 * Set the title in the action bar.
	 */
	@Override
	public int getActionBarTitle() {
		return titleText;
	}

	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.PAY_BILLS_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.PAY_BILLS_SECTION;
	}
}