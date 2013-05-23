package com.discover.mobile.bank.deposit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.services.customer.Eligibility;
import com.discover.mobile.bank.ui.fragments.TermsConditionsFragment;

/**
 * Fragment class for displaying the Terms & Conditions for Check Deposit. It is a sub-class 
 * of TermsConditionsFragment.
 * 
 * @author henryoyuela
 *
 */
public class BankDepositTermsFragment extends TermsConditionsFragment {

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = super.onCreateView(inflater, container, savedInstanceState);

		return view;
	}

	/**
	 * Returns the URL to be used for downloading the HTML file that displays the terms & conditions for check deposit.
	 */
	@Override
	public String getTermsUrl() {
		return BankUser.instance().getCustomerInfo().getDepositsEligibility().getTermsUrl();
	}

	/**
	 * Event Handler for when a user taps the Accept Button on the Fragment page.
	 */
	@Override
	public void onAcceptClicked() {
		final Eligibility eligibility = BankUser.instance().getCustomerInfo().getDepositsEligibility();
		BankServiceCallFactory.createAcceptTermsRequest(eligibility).submit();	
	}

	/**
	 * Method used to retrieve the title to display in the status bar of the Fragment Activity hosting this fragment.
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.bank_deposit_title;
	}

	/**
	 * Method used to retrieve menu group this fragment class is associated with.
	 */
	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.DEPOSIT_CHECK_GROUP;
	}

	/**
	 * Method used to retreive the menu section this fragment class is associated with.
	 */
	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.DEPOSIT_NOW_SECTION;
	}

	/**
	 * Returns a resource identifier for the string that is displayed as the title on the fragment layout.
	 */
	@Override
	public int getPageTitle() {		
		return R.string.bank_deposit_page_title;
	}

}
