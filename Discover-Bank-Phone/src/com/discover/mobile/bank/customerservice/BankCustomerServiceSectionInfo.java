package com.discover.mobile.bank.customerservice;

import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.account.BankAccountSummaryFragment;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.help.FAQLandingPageFragment;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.common.nav.section.ClickComponentInfo;
import com.discover.mobile.common.nav.section.FragmentComponentInfo;
import com.discover.mobile.common.nav.section.GroupComponentInfo;

public final class BankCustomerServiceSectionInfo extends GroupComponentInfo {
	
	public BankCustomerServiceSectionInfo() {
		super(R.string.section_title_customer_service,
				new FragmentComponentInfo(R.string.sub_section_title_contact_us, BankAccountSummaryFragment.class),
				new FragmentComponentInfo(R.string.sub_section_title_faq, FAQLandingPageFragment.class),
				new ClickComponentInfo(R.string.sub_section_title_secure_message,true, externalLink(BankUrlManager.getOpenAccountUrl())),
				new ClickComponentInfo(R.string.sub_section_title_user_profile,true, externalLink(BankUrlManager.getOpenAccountUrl())));
	}
	
	
	private static OnClickListener externalLink(final String url){

		return new OnClickListener() {

			@Override
			public void onClick(final View v) {
				BankConductor.navigateToBrowser(url);
			}
		};
	}
}
