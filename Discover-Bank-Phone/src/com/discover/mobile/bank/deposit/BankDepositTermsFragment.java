package com.discover.mobile.bank.deposit;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.BankUser;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.services.customer.Eligibility;
import com.discover.mobile.bank.ui.fragments.TermsConditionsFragment;

public class BankDepositTermsFragment extends TermsConditionsFragment {

	@Override
	public String getTermsUrl() {
		return BankUser.instance().getCustomerInfo().getDepositsEligibility().getTermsUrl();
	}

	@Override
	public void onAcceptClicked() {
		final Eligibility eligibility = BankUser.instance().getCustomerInfo().getDepositsEligibility();
		BankServiceCallFactory.createAcceptTermsRequest(eligibility).submit();	
	}

	@Override
	public int getActionBarTitle() {
		return R.string.bank_deposit_title;
	}

	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.DEPOSIT_CHECK_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.DEPOSIT_NOW_SECTION;
	}

	@Override
	public int getPageTitle() {		
		return R.string.bank_deposit_title;
	}

}
