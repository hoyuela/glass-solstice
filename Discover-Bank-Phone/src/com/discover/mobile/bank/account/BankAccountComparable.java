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
		int ret = getOrdinalValue(arg0) - getOrdinalValue(arg1);
				
		/**If names are equal then compare nick names to sort by nickname secondly*/
		if( ret == 0) {
			ret = arg0.nickname.compareToIgnoreCase(arg1.nickname);
		}
	
		return ret;
		 
		
	}
	
	/**
	 * Method used to sort accounts based on type.
	 * 
	 * @param account Reference to an Account object whose type will be evaluated for sorting.
	 * 
	 * @return Returns a value from 5 to 0 based on Account Type.
	 */
	public int getOrdinalValue(final Account account) {
		int ret = 0;
		
		if( account.type.equalsIgnoreCase(Account.ACCOUNT_CHECKING)) {		
			ret = 1;	
		} else if( account.type.equalsIgnoreCase(Account.ACCOUNT_SAVINGS) || 
				 account.type.equalsIgnoreCase(Account.ACCOUNT_MMA) ||
				 account.type.equalsIgnoreCase(Account.ACCOUNT_CD)) {
			ret = 2;
			
		} else if( account.type.equalsIgnoreCase(Account.ACCOUNT_IRA)) {
			ret = 3;
		} else if( account.type.equalsIgnoreCase(Account.ACCOUNT_LOAN)) {
			ret = 4;
		} else {
			ret = 5;
		}
		
		return ret;
	}

}
