package com.discover.mobile.bank.paybills;

import android.os.Bundle;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.services.customer.Eligibility;
import com.discover.mobile.bank.ui.fragments.TermsConditionsFragment;

/**
 * The terms and conditions page for Pay Bills or Mange Payees.
 * 
 * This page presents the terms and conditions of the Bill Payment service to the user.
 * The content is loaded from a URL into a web view and allows the user to review and accept
 * the terms and conditions, or cancel and go back if they please.
 * 
 * @author scottseward, jthornton
 *
 */
public class BankPayTerms extends TermsConditionsFragment{

	/**The default title text that will be used if for some reason one is not passed in the Bundle */
	private int titleText = R.string.pay_a_bill_title;

	/**The String resource that is used for the title in the Action bar*/
	private int titleStringResource;
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

	@Override
	public String getTermsUrl() {
		return BankUser.instance().getCustomerInfo().getPaymentsEligibility().getTermsUrl();
	}

	@Override
	public void onAcceptClicked() {
		final Eligibility eligibility = BankUser.instance().getCustomerInfo().getPaymentsEligibility();
		BankServiceCallFactory.createAcceptTermsRequest(eligibility).submit();
	}

	@Override
	public int getPageTitle() {
		return titleText;
	}
}