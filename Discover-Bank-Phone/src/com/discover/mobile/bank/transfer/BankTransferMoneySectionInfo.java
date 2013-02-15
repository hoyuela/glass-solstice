package com.discover.mobile.bank.transfer;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.account.BankAccountSummaryFragment;
import com.discover.mobile.common.nav.section.FragmentComponentInfo;
import com.discover.mobile.common.nav.section.GroupComponentInfo;

public final class BankTransferMoneySectionInfo extends GroupComponentInfo {

	public BankTransferMoneySectionInfo() {
		super(R.string.section_title_transfer_money, new FragmentComponentInfo(
				R.string.section_title_transfer_money,
				BankAccountSummaryFragment.class), new FragmentComponentInfo(
				R.string.sub_section_title_scheduled_transfers,
				BankAccountSummaryFragment.class), new FragmentComponentInfo(
				R.string.sub_section_title_transfer_history,
				BankAccountSummaryFragment.class), new FragmentComponentInfo(
				R.string.sub_section_title_manage_external_accounts,
				BankAccountSummaryFragment.class));
	}

}
