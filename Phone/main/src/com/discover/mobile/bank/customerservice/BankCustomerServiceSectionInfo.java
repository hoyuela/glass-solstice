package com.discover.mobile.bank.customerservice;

import com.discover.mobile.bank.R;
import com.discover.mobile.card.home.HomeSummaryFragment;
import com.discover.mobile.common.nav.section.FragmentComponentInfo;
import com.discover.mobile.common.nav.section.GroupComponentInfo;

public final class BankCustomerServiceSectionInfo extends GroupComponentInfo {
	
	public BankCustomerServiceSectionInfo() {
		super(R.string.section_title_customer_service,
				new FragmentComponentInfo(R.string.sub_section_title_contact_us, HomeSummaryFragment.class),
				new FragmentComponentInfo(R.string.sub_section_title_faq, HomeSummaryFragment.class),
				new FragmentComponentInfo(R.string.sub_section_title_secure_message,
						HomeSummaryFragment.class),
				new FragmentComponentInfo(R.string.sub_section_title_user_profile,
				HomeSummaryFragment.class));
	}
	
}
