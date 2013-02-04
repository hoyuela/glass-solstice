package com.discover.mobile.bank.deposit;

import com.discover.mobile.R;
import com.discover.mobile.section.FragmentComponentInfo;
import com.discover.mobile.section.GroupComponentInfo;
import com.discover.mobile.section.home.HomeSummaryFragment;

public final class BankDepositChecksSectionInfo extends GroupComponentInfo {
	
	public BankDepositChecksSectionInfo() {
		super(R.string.section_title_deposit_checks,
				new FragmentComponentInfo(R.string.sub_section_title_deposit_a_check, HomeSummaryFragment.class));
	}
	
}
