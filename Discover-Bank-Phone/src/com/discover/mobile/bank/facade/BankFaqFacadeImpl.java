/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.facade;

import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.common.facade.BankFaqFacade;

public class BankFaqFacadeImpl implements BankFaqFacade {

	@Override
	public void launchBankFaq() {
		BankConductor.navigateToFAQLandingPage();
	}

	@Override
	public void navigateToBankFaqDetail(final String faqType) {
		BankConductor.navigateToFAQDetail(faqType);
	}
}
