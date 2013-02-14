package com.discover.mobile.bank.account;

import java.util.Comparator;

import com.discover.mobile.bank.services.account.Account;

/**
 * Comparator class used for sorting Bank Account objects based on name and then by nickname.
 * 
 * @author henryoyuela
 *
 */
public class BankAccountComparable implements Comparator<Account> {

	@Override
	public int compare(final Account arg0, final Account arg1) {
		/**Compare account names to sort alphabetically firstly by name*/
		int ret = arg0.name.compareTo(arg1.name);
		
		/**If names are equal then compare nick names to sort by nickname secondly*/
		if( ret == 0) {
			ret = arg0.nickname.compareTo(arg1.nickname);
		}
		
		return ret;
		 
		
	}

}
