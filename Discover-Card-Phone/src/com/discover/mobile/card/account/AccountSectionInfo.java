package com.discover.mobile.card.account;

import com.discover.mobile.card.R;
import com.discover.mobile.card.account.recent.AccountRecentActivityFragment;
import com.discover.mobile.card.account.summary.AccountSummaryFragment;
import com.discover.mobile.common.nav.section.FragmentComponentInfo;
import com.discover.mobile.common.nav.section.GroupComponentInfo;

public final class AccountSectionInfo extends GroupComponentInfo {

	public AccountSectionInfo() {
		super(R.string.section_title_account,
				new FragmentComponentInfo(R.string.sub_section_title_account_summary, AccountSummaryFragment.class),
				new FragmentComponentInfo(R.string.sub_section_title_recent_activity, AccountRecentActivityFragment.class),
				new FragmentComponentInfo(R.string.sub_section_title_search_transaction,
						AccountSearchTransactionFragment.class),
						new FragmentComponentInfo(R.string.sub_section_title_statements, AccountStatementsFragment.class));
	}

}
