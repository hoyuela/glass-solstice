package com.discover.mobile.bank.help;

import java.io.Serializable;

/**
 * Enum used to specify what page to display to the user
 * when invoking navigateToContactUs() in BankConductor.
 * 
 * @author henryoyuela
 *
 */
public enum ContactUsType implements Serializable {
	ALL,
	CARD,
	BANK
}
