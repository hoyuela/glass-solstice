package com.discover.mobile.bank.transfer;

import com.discover.mobile.bank.R;
import com.discover.mobile.card.home.HomeSummaryFragment;
import com.discover.mobile.common.nav.section.FragmentComponentInfo;
import com.discover.mobile.common.nav.section.GroupComponentInfo;

public final class BankTransferMoneySectionInfo extends GroupComponentInfo {

	public BankTransferMoneySectionInfo() {
		super(R.string.section_title_transfer_money, new FragmentComponentInfo(
				R.string.section_title_transfer_money,
				HomeSummaryFragment.class), new FragmentComponentInfo(
				R.string.sub_section_title_scheduled_transfers,
				HomeSummaryFragment.class), new FragmentComponentInfo(
				R.string.sub_section_title_transfer_history,
				HomeSummaryFragment.class), new FragmentComponentInfo(
				R.string.sub_section_title_manage_external_accounts,
				HomeSummaryFragment.class));
	}

}
