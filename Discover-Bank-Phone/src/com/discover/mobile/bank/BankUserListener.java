package com.discover.mobile.bank;

import java.util.List;

import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.services.account.Account;

public interface BankUserListener {
	public void onAccountsUpdate(BankUser sender, List<Account> accounts);
}
