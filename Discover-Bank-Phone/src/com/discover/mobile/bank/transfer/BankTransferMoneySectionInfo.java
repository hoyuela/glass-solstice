package com.discover.mobile.bank.transfer;

import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.bank.BankNavigator;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.account.BankAccountSummaryFragment;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.common.nav.section.ClickComponentInfo;
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
				BankAccountSummaryFragment.class), new ClickComponentInfo(
				R.string.sub_section_title_manage_external_accounts,true,
				externalLink(BankUrlManager.getOpenAccountUrl())));
	}
	
	private static OnClickListener externalLink(final String url){

		return new OnClickListener() {

			@Override
			public void onClick(final View v) {
				BankNavigator.navigateToBrowser(url);
			}
		};
	}

}
