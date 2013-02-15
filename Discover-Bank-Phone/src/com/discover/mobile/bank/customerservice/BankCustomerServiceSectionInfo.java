package com.discover.mobile.bank.customerservice;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.account.BankAccountSummaryFragment;
import com.discover.mobile.common.nav.section.FragmentComponentInfo;
import com.discover.mobile.common.nav.section.GroupComponentInfo;

public final class BankCustomerServiceSectionInfo extends GroupComponentInfo {
	
	public BankCustomerServiceSectionInfo() {
		super(R.string.section_title_customer_service,
				new FragmentComponentInfo(R.string.sub_section_title_contact_us, BankAccountSummaryFragment.class),
				new FragmentComponentInfo(R.string.sub_section_title_faq, BankAccountSummaryFragment.class),
				new FragmentComponentInfo(R.string.sub_section_title_secure_message,
						BankAccountSummaryFragment.class),
				new FragmentComponentInfo(R.string.sub_section_title_user_profile,
						BankAccountSummaryFragment.class));
	}
	
}
