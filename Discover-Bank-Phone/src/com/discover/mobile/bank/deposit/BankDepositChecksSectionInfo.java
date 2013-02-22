package com.discover.mobile.bank.deposit;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.account.BankAccountSummaryFragment;
import com.discover.mobile.common.nav.section.FragmentComponentInfo;
import com.discover.mobile.common.nav.section.GroupComponentInfo;

public final class BankDepositChecksSectionInfo extends GroupComponentInfo {
	
	public BankDepositChecksSectionInfo() {
		super(R.string.section_title_deposit_checks,
				new FragmentComponentInfo(R.string.sub_section_title_deposit_a_check, BankAccountSummaryFragment.class));
	}
	
}
