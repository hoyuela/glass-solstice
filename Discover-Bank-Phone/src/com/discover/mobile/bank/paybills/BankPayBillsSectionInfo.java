package com.discover.mobile.bank.paybills;

import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.bank.BankNavigator;
import com.discover.mobile.bank.BankServiceCallFactory;
import com.discover.mobile.bank.BankUser;
import com.discover.mobile.bank.R;
import com.discover.mobile.card.home.HomeSummaryFragment;
import com.discover.mobile.common.nav.section.ClickComponentInfo;
import com.discover.mobile.common.nav.section.FragmentComponentInfo;
import com.discover.mobile.common.nav.section.GroupComponentInfo;

public final class BankPayBillsSectionInfo extends GroupComponentInfo {

	public BankPayBillsSectionInfo() {
		super(R.string.section_title_pay_bills,
				new ClickComponentInfo(R.string.section_title_pay_bills, getPayBillsLandingClickListener()),
				new FragmentComponentInfo(R.string.sub_section_title_review_payments, HomeSummaryFragment.class),
				new FragmentComponentInfo(R.string.sub_section_title_manage_payees,
						HomeSummaryFragment.class));
	}

	/**
	 * Click listener for the pay bills title. Based on the eligibility of the user,
	 * the app will send them to another page.
	 * @return the click listener for the pay bills title
	 */
	public static OnClickListener getPayBillsLandingClickListener(){
		return new OnClickListener(){
			@Override
			public void onClick(final View v) {
				final boolean isEligible = BankUser.instance().getCustomerInfo().getPaymentsEligibility();
				final boolean isEnrolled = BankUser.instance().getCustomerInfo().getPaymentsEnrolled();

				if(!isEligible){
					BankNavigator.navigateToPayBillsLanding();
				} else if(isEligible && !isEnrolled){
					BankNavigator.navigateToPayBillsTerms(null);
				} else{
					BankServiceCallFactory.createGetPayeeServiceRequest().submit();
				}
			}
		};
	}

}
