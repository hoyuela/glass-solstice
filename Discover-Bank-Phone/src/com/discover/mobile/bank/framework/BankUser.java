package com.discover.mobile.bank.framework;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.discover.mobile.bank.deposit.CheckDepositCaptureActivity;
import com.discover.mobile.bank.services.BankHolidays;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.account.AccountList;
import com.discover.mobile.bank.services.customer.Customer;
import com.discover.mobile.bank.services.payee.ListPayeeDetail;
import com.discover.mobile.bank.services.payment.ListPaymentDetail;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.framework.CacheManager;

/**
 * Class used to maintain session information for a user logged into a Bank
 * account. This class follows a singleton design pattern allowing only one
 * instance of this class to ever exists. It's data members are set by
 * NetworkServiceCall<> objects upon receiving a successful response. As an
 * example, the customerInfo object is updated by the CustomerServiceCall class.
 * 
 * @author henryoyuela
 * 
 */
public final class BankUser extends CacheManager implements Serializable {
	/**
	 * Serialized!
	 */
	private static final long serialVersionUID = -8534001018414145158L;
	/**
	 * Singleton instance of this class
	 */
	private static BankUser currentBankUser = new BankUser();
	/**
	 * Holds a reference to a AccountList set from the
	 * GetCustomerAccountServerCall on a successful download of accounts.
	 */
	private AccountList accountList;
	/**
	 * Holds a reference to a Customer object set from the CustomerServiceCall
	 * on a successful download of customer information.
	 */
	private Customer customerInfo;
	/**
	 * Holds a reference to the Account whose details are currently be viewed by
	 * the user.
	 */
	private Account currentAccount;

	/**List of bank holidays where user cannot schedule certain bank transactions*/
	private BankHolidays holidays = new BankHolidays();

	/**List of scheduled payments*/
	private ListPaymentDetail scheduled;

	/**List of completed payments*/
	private ListPaymentDetail completed;

	/**List of cancelled payments*/
	private ListPaymentDetail cancelled;

	/**Boolean set to true if the login occurred through an SSO sequence*/
	private boolean ssoUser = false;

	/**
	 * 
	 * @return Returns reference to single instance of BankUser
	 */
	public static BankUser instance() {
		return currentBankUser;
	}

	/**
	 * 
	 * @return Returns list of accounts downloaded via
	 *         GetCustomerAccountsServerCall
	 */
	public AccountList getAccounts() {
		return accountList;
	}

	/**
	 * @return Returns reference to list of accounts that are capable of scheduled payments
	 */
	public AccountList getPaymentCapableAccounts() {
		final AccountList newList = new AccountList();
		newList.accounts = new ArrayList<Account>();

		if( this.hasAccounts() ) {
			for(final Account account : accountList.accounts) {
				if( account.canSchedulePayment() ) {
					newList.accounts.add(account);
				}
			}
		}
		return newList;
	}

	/**
	 * Returns the {@code Account} for a given Account id.
	 * 
	 * @param accountId
	 *            id of the Account to be returned.
	 * @return Account or {@code null} if not found.
	 */
	public Account getAccount(final String accountId) {
		for (final Account a : accountList.accounts) {
			if (a.id.equals(accountId)) {
				return a;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param accounts
	 *            Sets the list of accounts downloaded via
	 *            GetCustomerAccountsServerCall
	 */
	public void setAccounts(final AccountList accounts) {
		accountList = accounts;
	}

	/**
	 * 
	 * @return Returns True if user has Bank accounts, false otherwise.
	 */
	public boolean hasAccounts() {
		return (accountList != null && !accountList.accounts.isEmpty());
	}


	/**
	 * 
	 * @return Returns True if user has a Checking, Money Market or Savings Account, false otherwise.
	 */
	public boolean hasDepositEligibleAccounts() {
		boolean ret = false;

		if( hasAccounts() ) {
			for( final Account account : accountList.accounts) {
				if( account.type.equalsIgnoreCase(Account.ACCOUNT_CHECKING) ||
						account.type.equalsIgnoreCase(Account.ACCOUNT_SAVINGS) ||
						account.type.equalsIgnoreCase(Account.ACCOUNT_MMA)) {
					ret = true;
					break;
				}
			}
		}

		return ret;
	}

	/**
	 * 
	 * @return Returns reference to a Customer object that is set after a
	 *         successful response to Bank Customer Service API via
	 *         CustomerServiceCall.
	 */
	public Customer getCustomerInfo() {
		return customerInfo;
	}

	/**
	 * 
	 * @param customerInfo
	 *            Sets reference to Customer object downloaded via
	 *            CustomerServiceCall
	 */
	public void setCustomerInfo(final Customer customerInfo) {
		this.customerInfo = customerInfo;
	}

	/**
	 * Get the list of payees
	 * @return the list of payees
	 */
	public ListPayeeDetail getPayees(){
		return (ListPayeeDetail) this.getObjectFromCache(ListPayeeDetail.class);
	}

	/**
	 * Used to clear all cached data during the session of a user logged into
	 * Bank
	 */
	@Override
	public void clearSession() {
		accountList = null;
		customerInfo = null;
		BankUrlManager.clearLinks();
		currentAccount = null;
		scheduled = null;
		completed = null;
		cancelled = null;
		ssoUser = false;
		//Ensure that any cached check images are deleted upon logout or timeout.
		CheckDepositCaptureActivity.deleteBothImages(DiscoverActivityManager.getActiveActivity());
		//Clear the cache manager
		super.clearSession();
	}

	/**
	 * Method used to set the current account being viewed by the user
	 * 
	 * @param value
	 *            Reference to Account whose details are being viewed by the
	 *            user
	 */
	public void setCurrentAccount(final Account value) {
		currentAccount = value;
	}

	/**
	 * @return Reference to an Account whose details are currently being
	 *         accessed by the User, if not set then returns null.
	 */
	public Account getCurrentAccount() {
		return currentAccount;
	}

	public void setBankUser(final BankUser bu) {
		currentBankUser = bu;
	}

	/**
	 * @return Returns a list of dates that are considered Bank Holidays
	 */
	public ArrayList<Date> getHolidays() {
		return holidays.getDates();
	}

	/**
	 * Method used to set the Bank Holidays
	 * 
	 * @param value Reference to a BankHolidays object generated from a response to a BankHolidayServiceCall.
	 */
	public void setHolidays(final BankHolidays value) {
		if( value != null ) {
			holidays = value;
		}
	}

	/**
	 * @return the scheduled
	 */
	public ListPaymentDetail getScheduled() {
		return scheduled;
	}

	/**
	 * @param scheduled the scheduled to set
	 */
	public void setScheduled(final ListPaymentDetail scheduled) {
		this.scheduled = scheduled;
	}

	/**
	 * @return the completed
	 */
	public ListPaymentDetail getCompleted() {
		return completed;
	}

	/**
	 * @param completed the completed to set
	 */
	public void setCompleted(final ListPaymentDetail completed) {
		this.completed = completed;
	}

	/**
	 * @return the cancelled
	 */
	public ListPaymentDetail getCancelled() {
		return cancelled;
	}

	/**
	 * @param cancelled the cancelled to set
	 */
	public void setCancelled(final ListPaymentDetail cancelled) {
		this.cancelled = cancelled;
	}

	/**
	 * @return the ssoUser
	 */
	public boolean isSsoUser() {
		return ssoUser;
	}

	/**
	 * @param ssoUser the ssoUser to set
	 */
	public void setSsoUser(final boolean ssoUser) {
		this.ssoUser = ssoUser;
	}
}
