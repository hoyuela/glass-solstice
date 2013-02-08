package com.discover.mobile.bank.account;

import com.discover.mobile.R;
import com.discover.mobile.section.FragmentComponentInfo;
import com.discover.mobile.section.GroupComponentInfo;

public final class BankAccountSectionInfo extends GroupComponentInfo {

	public BankAccountSectionInfo() {
		super(R.string.section_title_account,
				new FragmentComponentInfo(R.string.sub_section_title_account_summary, BankAccountSummaryFragment.class),
				new FragmentComponentInfo(R.string.sub_section_title_statements, BankAccountSummaryFragment.class),
				new FragmentComponentInfo(R.string.sub_section_title_open_new_account,
						BankAccountSummaryFragment.class));
		super(R.string.section_title_account, 
				new FragmentComponentInfo(R.string.sub_section_title_account_summary,HomeSummaryFragment.class), 
				new FragmentComponentInfo(R.string.sub_section_title_statements,null, "http://www.google.com"),
				new FragmentComponentInfo(R.string.sub_section_title_open_new_account,null, "http://www.google.com"));
	}

}
