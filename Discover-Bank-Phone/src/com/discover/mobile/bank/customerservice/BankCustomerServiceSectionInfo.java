package com.discover.mobile.bank.customerservice;

import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.help.ContactUsType;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.common.facade.FacadeFactory;
import com.discover.mobile.common.nav.section.ClickComponentInfo;
import com.discover.mobile.common.nav.section.GroupComponentInfo;

public final class BankCustomerServiceSectionInfo extends GroupComponentInfo {

	public BankCustomerServiceSectionInfo() {
		super(R.string.section_title_customer_service,
				new ClickComponentInfo(R.string.sub_section_title_contact_us, false, onContactUsClick() ),
				new ClickComponentInfo(R.string.sub_section_title_faq, false, onFaqClick()),
				new ClickComponentInfo(R.string.sub_section_title_secure_message,true, 
						externalLink(BankUrlManager.getStatementsUrl())),
						new ClickComponentInfo(R.string.sub_section_title_user_profile,true, 
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

	private static OnClickListener onFaqClick(){

		return new OnClickListener() {

			@Override
			public void onClick(final View v) {
				FacadeFactory.getBankFaqFacade().launchBankFaq();
			}
		};
	}

	private static OnClickListener onContactUsClick(){

		return new OnClickListener() {

			@Override
			public void onClick(final View v) {
				BankConductor.navigateToContactUs(ContactUsType.BANK);
			}
		};
	}
}
