package com.discover.mobile.bank.deposit;

import java.io.Serializable;

/**
 * Enum used to specify what step in the check deposit to display to the user
 * when invoking navigateToCheckDepositWorkFlow() in BankConductor.
 * 
 * @author henryoyuela
 *
 */
public enum BankDepositWorkFlowStep implements Serializable {
	SelectAmount,
	SelectAccount,
	ReviewDeposit,
	Confirmation,
	DepositError,
	DuplicateError,
	ForbiddenError
}
