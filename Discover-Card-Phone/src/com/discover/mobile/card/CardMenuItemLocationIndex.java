/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.card;


/**
 * Class holding all of the indexes of items in the menu.  The group values are the components in the menu
 * that expand and collapse.  These indexes start at 0.  The section values are the location under
 * each group that the section under the location.  For example, if the section was immediately below the
 * group when the group is expanded the index would be 1.
 * 
 * @author jthornton
 *
 */
public final class CardMenuItemLocationIndex {

	/**
	 * Home indexes
	 */
	public static final int HOME_GROUP = 0;
	public static final int HOME_SECTION = 0;

	/**
	 * Account indexes
	 */
	public static final int ACCOUNT_GROUP = 1;
	public static final int ACCOUNT_SUMMARY_SECTION = 1;
	public static final int RECENT_ACTIVITY_SECTION = 2;
	public static final int SEARCH_TRANSACTION_SECTION = 3;
	public static final int STATEMENTS_SECTION = 4;

	/**
	 * Profile and Setting Indexes
	 */
	public static final int PROFILE_AND_SETTINGS_GROUP = 2;
	public static final int MANAGE_ALERTS_SECTION = 1;
	public static final int ALERTS_HISTORY_SECTION = 2;
	public static final int ENROLL_REMINDERS_SECTION = 3;

	/**
	 * Contact Us indexes
	 */
	public static final int CONTACT_US_GROUP = 3;
	public static final int CONTACT_US_SECTION = 1;

	/**
	 * Private constructor so the class cannot be instantiated
	 */
	private CardMenuItemLocationIndex(){}
}