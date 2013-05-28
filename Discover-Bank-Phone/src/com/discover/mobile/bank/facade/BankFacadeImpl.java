package com.discover.mobile.bank.facade;

import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.common.facade.BankFacade;

public class BankFacadeImpl implements BankFacade {

	@Override
	public void navToCardPrivacyTerms() {
		BankConductor.navigateToCardPrivacyAndTermsLanding();
	}

}
