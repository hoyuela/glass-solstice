package com.discover.mobile.section.account;

import com.discover.mobile.R;
import com.discover.mobile.section.SectionInfo;
import com.discover.mobile.section.SubSectionInfo;

public final class AccountSectionInfo extends SectionInfo {
	
	public AccountSectionInfo() {
		super(R.string.section_title_account, AccountHomeFragment.class,
				new SubSectionInfo(R.string.sub_section_title_account_summary, AccountSummaryFragment.class),
				new SubSectionInfo(R.string.sub_section_title_recent_activity, AccountRecentActivityFragment.class),
				new SubSectionInfo(R.string.sub_section_title_search_transaction,
						AccountSearchTransactionFragment.class),
				new SubSectionInfo(R.string.sub_section_title_statements, AccountStatementsFragment.class));
	}
	
}
