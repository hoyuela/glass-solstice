package com.discover.mobile.bank.deposit;

import java.util.Comparator;

import com.discover.mobile.bank.services.account.Account;

/**
 * Comparator class used for sorting Bank Account objects based on type and then by nickname alphabetically.
 * 
 * Alphabetize nicknames based on the following order
 * 		Alphabetized Checking Accounts
 * 		Alphabetized Money Market Account
 * 		Alphabetized Savings Accounts
 * 
 * @author henryoyuela
 *
 */
public class BankSelectAccountComparable implements Comparator<Account> {

	@Override
	public int compare(final Account arg0, final Account arg1) {
		/**Compare account types to sort by types firstly*/
		int ret = getOrdinalValue(arg0) - getOrdinalValue(arg1);
				
		/**If types are equal then compare nick names to sort by nickname alphabetically*/
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
		} else if( account.type.equalsIgnoreCase(Account.ACCOUNT_MMA) ) {
			ret = 2;
		} else if( account.type.equalsIgnoreCase(Account.ACCOUNT_SAVINGS) ) {
			ret = 3;
		} else if(account.type.equalsIgnoreCase(Account.ACCOUNT_CD)) {
			ret = 4;
		} else if( account.type.equalsIgnoreCase(Account.ACCOUNT_IRA)) {
			ret = 5;
		} else if( account.type.equalsIgnoreCase(Account.ACCOUNT_LOAN)) {
			ret = 6;
		} else {
			ret = 7;
		}
		
		return ret;
	}

}
