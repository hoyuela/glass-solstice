package com.discover.mobile.bank.atm;

import com.discover.mobile.R;
import com.discover.mobile.section.FragmentComponentInfo;
import com.discover.mobile.section.GroupComponentInfo;
import com.discover.mobile.section.home.HomeSummaryFragment;

public final class BankAtmLocatorInfo extends GroupComponentInfo {
	
	public BankAtmLocatorInfo() {
		super(R.string.section_title_atm_locator,
				new FragmentComponentInfo(R.string.sub_section_title_find_nearby, HomeSummaryFragment.class),
				new FragmentComponentInfo(R.string.sub_section_title_search_location, HomeSummaryFragment.class));
	}
	
}
