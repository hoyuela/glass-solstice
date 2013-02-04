package com.discover.mobile.bank.customerservice;

import com.discover.mobile.R;
import com.discover.mobile.section.FragmentComponentInfo;
import com.discover.mobile.section.GroupComponentInfo;
import com.discover.mobile.section.home.HomeSummaryFragment;

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
