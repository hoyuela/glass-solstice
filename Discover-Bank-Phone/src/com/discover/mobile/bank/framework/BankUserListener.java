package com.discover.mobile.bank.framework;

import java.util.List;

import com.discover.mobile.bank.services.account.Account;

public interface BankUserListener {
	public void onAccountsUpdate(BankUser sender, List<Account> accounts);

	public void onCurrentAccountUpdate(BankUser sender, Account account);
}
