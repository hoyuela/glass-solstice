package com.discover.mobile.bank.account;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.bank.BankNavigator;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.checkdeposit.CheckDespositCaptureActivity;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.nav.section.ClickComponentInfo;
import com.discover.mobile.common.nav.section.FragmentComponentInfo;
import com.discover.mobile.common.nav.section.GroupComponentInfo;

public final class BankAccountSectionInfo extends GroupComponentInfo {

	public BankAccountSectionInfo() {
		super(R.string.section_title_account,
				new ClickComponentInfo(R.string.check_deposit, launchCheckDepositActivity()), 
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
	
	private static OnClickListener launchCheckDepositActivity() {
		return new OnClickListener() {
			
			@Override
			public void onClick(final View v) {
				final Intent checkDeposit = new Intent(DiscoverActivityManager.getActiveActivity(), CheckDespositCaptureActivity.class);
				DiscoverActivityManager.getActiveActivity().startActivity(checkDeposit);
			}
		};
	}
}
