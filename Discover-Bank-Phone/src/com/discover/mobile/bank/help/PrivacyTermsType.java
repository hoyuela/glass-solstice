package com.discover.mobile.bank.help;

import java.io.Serializable;

/**
 * Enum used to specify what page to display to the user
 * when invoking navigateToPrivacyTerms() in BankConductor.
 * 
 * @author henryoyuela
 *
 */
public enum PrivacyTermsType implements Serializable{
	LandingPage,
	MobilePrivacyStatement,
	MobileTermsOfUse,
	BillPayTermsOfUse,
	DepositTermsOfUse
}
