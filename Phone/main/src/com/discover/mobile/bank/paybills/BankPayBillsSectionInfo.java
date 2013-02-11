package com.discover.mobile.bank.paybills;

import com.discover.mobile.bank.R;
import com.discover.mobile.card.home.HomeSummaryFragment;
import com.discover.mobile.common.nav.section.FragmentComponentInfo;
import com.discover.mobile.common.nav.section.GroupComponentInfo;

public final class BankPayBillsSectionInfo extends GroupComponentInfo {

	public BankPayBillsSectionInfo() {
		super(R.string.section_title_pay_bills,
				new FragmentComponentInfo(R.string.section_title_pay_bills, BankSchedulePaymentLandingPage.class),
				new FragmentComponentInfo(R.string.sub_section_title_review_payments, HomeSummaryFragment.class),
				new FragmentComponentInfo(R.string.sub_section_title_manage_payees,
						HomeSummaryFragment.class));
	}

}
