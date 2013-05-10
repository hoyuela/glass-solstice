/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile;

/**
 * Class holding all of the indexes of items in the menu.  The group values are the components in the menu
 * that expand and collapse.  These indexes start at 0.  The section values are the location under
 * each group that the section under the location.  For example, if the section was immediately below the
 * group when the group is expanded the index would be 1.
 * 
 * @author jthornton
 *
 */
public final class BankMenuItemLocationIndex {

	/**
	 * Account Summary Indexes
	 */
	public static final int ACCOUNT_SUMMARY_GROUP = 0;
	public static final int ACCOUNT_SUMMARY_SECTION = 1;
	public static final int OPEN_NEW_ACCOUNT_SECTION = 2;
	public static final int VIEW_STATEMENTS_SECTION = 3;

	/**
	 * Transfer Money Indexes
	 */
	public static final int TRANSFER_MONEY_GROUP = 1;
	public static final int REVIEW_TRANSFERS_SECTION = 1;
	public static final int MANAGE_EXTERNAL_ACCOUNTS_SECTION = 2;

	/**
	 * Check Deposit Indexes
	 */
	public static final int DEPOSIT_CHECK_GROUP = 2;
	public static final int DEPOSIT_NOW_SECTION = 1;

	/**
	 * Pay Bills Indexes
	 */
	public static final int PAY_BILLS_GROUP = 3;
	public static final int PAY_BILLS_SECTION = 1;
	public static final int REVIEW_PAYEMENTS_SECTION = 2;
	public static final int MANAGE_PAYEES_SECTION = 3;

	/**
	 * ATM Locator Indexes
	 */
	public static final int ATM_LOCATOR_GROUP = 4;
	public static final int FIND_NEARBY_SECTION = 1;
	public static final int SEARCH_BY_LOCATION = 2;

	/**
	 * Customer Service Indexes
	 */
	public static final int CUSTOMER_SERVICE_GROUP = 5;
	public static final int CONTACT_US_SECTION = 1;
	public static final int FREQUENTLY_ASKED_QUESTIONS = 2;
	public static final int SECURE_MEASSAGE_CENTER = 3;
	public static final int PROFILE = 4;

	/**
	 * Privacy and Terms Indexes
	 */
	public static final int PRIVACY_AND_TERMS_GROUP = 6;
	public static final int MOBILE_PRIVACY_STATEMENT = 1;
	public static final int MOBILE_TERMS_OF_USE = 2;
	public static final int BILL_PAY_TERMS_OF_USE = 3;
	public static final int DEPOSIT_A_CHECK_TERMS_OF_USE = 4;
	
	/**
	 * Provide Feedback Indexes
	 */
	public static final int PROVIDE_FEEDBACK_GROUP = 7;
	public static final int PROVIDE_FEEDBACK = 1;

	/**
	 * Private constructor so the class cannot be instantiated
	 */
	private BankMenuItemLocationIndex(){}

}
