package com.discover.mobile.bank.atm;

import com.discover.mobile.bank.R;
import com.discover.mobile.common.nav.section.FragmentComponentInfo;
import com.discover.mobile.common.nav.section.GroupComponentInfo;

public final class BankAtmLocatorInfo extends GroupComponentInfo {

	public BankAtmLocatorInfo() {
		super(R.string.section_title_atm_locator,
				new FragmentComponentInfo(R.string.sub_section_title_find_nearby, SearchNearbyFragment.class),
				new FragmentComponentInfo(R.string.sub_section_title_search_location, SearchByLocationFragment.class));
	}

}
