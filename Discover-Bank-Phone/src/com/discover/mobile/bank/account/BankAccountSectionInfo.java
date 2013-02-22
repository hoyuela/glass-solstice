package com.discover.mobile.bank.account;

import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.bank.BankNavigator;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.common.nav.section.ClickComponentInfo;
import com.discover.mobile.common.nav.section.FragmentComponentInfo;
import com.discover.mobile.common.nav.section.GroupComponentInfo;

public final class BankAccountSectionInfo extends GroupComponentInfo {

	public BankAccountSectionInfo() {
		super(R.string.section_title_account,
				new FragmentComponentInfo(R.string.sub_section_title_account_summary, BankAccountSummaryFragment.class), 
				new ClickComponentInfo(R.string.sub_section_title_statement,true,externalLink(BankUrlManager.getStatementsUrl())),
				new ClickComponentInfo(R.string.sub_section_title_open_new_account,true, externalLink(BankUrlManager.getOpenAccountUrl())));
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
