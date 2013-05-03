package com.discover.mobile.bank.account;

import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.common.nav.section.ClickComponentInfo;
import com.discover.mobile.common.nav.section.GroupComponentInfo;


public final class BankAccountSectionInfo extends GroupComponentInfo {

	public BankAccountSectionInfo() {
		super(R.string.section_title_account,
				new ClickComponentInfo(R.string.sub_section_title_account_summary, openAccountSummary()), 
				new ClickComponentInfo(R.string.sub_section_title_open_new_account,true, 
													externalLink(BankUrlManager.getOpenAccountUrl())),
				new ClickComponentInfo(R.string.sub_section_title_statement,true,
													externalLink(BankUrlManager.getStatementsUrl())));
	}

	private static OnClickListener externalLink(final String url){

		return new OnClickListener() {

			@Override
			public void onClick(final View v) {
				BankConductor.navigateToBrowser(url);
			}
		};
	}


	private static OnClickListener openAccountSummary() {
		return new OnClickListener() {

			@Override
			public void onClick(final View arg0) {
				if(null == BankUser.instance().getAccounts() || BankUser.instance().isAccountOutDated()){
					BankServiceCallFactory.createGetCustomerAccountsServerCall().submit();
				}else{
					BankConductor.navigateToHomePage();		
				}
			}
		};

	}
}
