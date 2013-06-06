package com.discover.mobile.bank.framework;

import java.util.ArrayList;
import java.util.List;

import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.account.AccountList;

final class BankUserVisitors {
	static public void visitAccountChangeListeners(final BankUser sender, final List<BankUserListener> listeners, final AccountList acctList) {
		if (sender != null && listeners != null) {
			final List<Account> accounts = (acctList != null && acctList.accounts != null) ? acctList.accounts : new ArrayList<Account>();

			for (final BankUserListener listener : listeners) {
				listener.onAccountsUpdate(sender, accounts);
			}
		}
	}

	static public void visitCurrentAccountChangeListeners(final BankUser sender, final List<BankUserListener> listeners, final Account account) {
		if (sender != null && listeners != null) {
			for (final BankUserListener listener : listeners) {
				listener.onCurrentAccountUpdate(sender, account);
			}
		}
	}
}
