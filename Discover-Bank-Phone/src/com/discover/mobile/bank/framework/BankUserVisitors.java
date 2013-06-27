package com.discover.mobile.bank.framework;

import java.util.ArrayList;
import java.util.List;

import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.account.AccountList;
import com.discover.mobile.common.DiscoverActivityManager;

/**
 * Non-instantiable class that contains a list of static methods used for visiting a BankUser's listeners and notifying
 * them of events.
 * 
 * @author henryoyuela
 * 
 */
final class BankUserVisitors {
	private BankUserVisitors() {
		throw new UnsupportedOperationException("This class is non-instantiable");
	}

	/**
	 * Method used to visit BankUser's listener and notify them of account list change.
	 * 
	 * @param sender
	 *            Reference to the calling BankUser object.
	 * @param listeners
	 *            List of listeners to be visited
	 * @param acctList
	 *            New updated account list
	 */
	static public void visitAccountChangeListeners(final BankUser sender, final List<BankUserListener> listeners, final AccountList acctList) {
		if (sender != null && listeners != null) {
			final List<Account> accounts = (acctList != null && acctList.accounts != null) ? acctList.accounts : new ArrayList<Account>();

			/** Make sure visitors are notified via the UI thread */
			DiscoverActivityManager.getActiveActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					for (final BankUserListener listener : listeners) {
						listener.onAccountsUpdate(sender, accounts);
					}
				}
			});
		}
	}

	/**
	 * Method used to visit BankUser's listener and notify them of the current account change.
	 * 
	 * @param sender
	 *            Reference to the calling BankUser object.
	 * @param listeners
	 *            List of listeners to be visited
	 * @param account
	 *            New updated current account
	 */
	static public void visitCurrentAccountChangeListeners(final BankUser sender, final List<BankUserListener> listeners, final Account account) {
		if (sender != null && listeners != null) {

			/** Make sure visitors are notified via the UI thread */
			DiscoverActivityManager.getActiveActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					for (final BankUserListener listener : listeners) {
						listener.onCurrentAccountUpdate(sender, account);
					}
				}
			});
		}
	}
}
