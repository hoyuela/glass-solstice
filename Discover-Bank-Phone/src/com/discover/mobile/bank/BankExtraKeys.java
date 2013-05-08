package com.discover.mobile.bank;

/**
 * Class of keys that passed around in bundles so that data can be extracted from the bundles.
 * @author jthornton
 *
 */
public final class BankExtraKeys {

	/**Payload that would be retrieved from the service*/
	public static final String PAYLOAD = "payLoad";

	/**Used to get the list of payees from a bundle*/
	public static final String PAYEES_LIST = "payees-list";

	/**used to get the payee selected in pay bills*/
	public static final String SELECTED_PAYEE = "selected-payee";

	/**Used to send a list of details to the detail screen from the table*/
	public static final String PRIMARY_LIST = "data1";

	/**Used to send the other list of details to the detail screen from the table*/
	public static final String SECOND_DATA_LIST = "data2";

	/**Used to send the other list of details to the detail screen from the table*/
	public static final String SCHEDULED_LIST = "data4";

	/**Used to send the other list of details to the detail screen from the table*/
	public static final String COMPLETED_LIST = "data2";

	/**Used to send the other list of details to the detail screen from the table*/
	public static final String CANCELED_LIST = "data3";

	/**Used to send the selected index of the detail to the detail screen*/
	public static final String DATA_SELECTED_INDEX = "index";

	/**Used to send the selected category to the detail screen*/
	public static final String CATEGORY_SELECTED = "category-selected";

	/**Used to send the sorting of the table to the detail fragment and back*/
	public static final String SORT_ORDER = "sort-order";

	/**Used to signify if the table title was expanded*/
	public static final String TITLE_EXPANDED = "expanded";

	/**Used to send a single data item to a Fragment in the arguments bundle*/
	public static final String DATA_LIST_ITEM = "item";

	/**Used to notify a fragment that a transaction item has been deleted from its list*/
	public static final String CONFIRM_DELETE = "delete";

	/**Used to pass a String resource that is meant to be displayed as the title in the action bar*/
	public static final String TITLE_TEXT = "title";

	/**Used to let the check deposit capture screen know which image is being retaken */
	public static final String RETAKE_PICTURE = "retake";

	/**Used to pass the amount of the check deposit around between fragments and activities*/
	public static final String AMOUNT = "amount";

	/**Used to pass a boolean to the check deposit workflow for when a user goes back to a previous
	 * fragment to adjust the information they previously entered
	 */
	public static final String REENTER_AMOUNT = "newAmount";

	/**
	 * Used to let the select account screen in check deposit know that an account is being reselected and should alter
	 * its navigation.
	 */
	public static final String RESELECT_ACCOUNT = "newAccount";

	/**
	 * Used to let the first step of check deposit know if the user has just accepted
	 * the terms and should see the how it works modal
	 */
	public static final String ACCEPTED_TERMS = "accepted";

	/** The key used when navigating to a specific FAQ section */
	public static final String FAQ_TYPE = "faq type";

	/** The key used when navigating to the gernal FAQs */
	public static final String GENERAL_FAQ = "general faq";

	/** The key used when navigating to bill pay FAQs */
	public static final String BILL_PAY_FAQ = "pay bills faq";

	/** The key used for navigating to check deposit FAQs*/
	public static final String CHECK_DEPOSIT_FAQ = "deposit faq";

	/** The key used for navigating to ATM Locator FAQs */
	public static final String ATM_LOCATOR_FAQ = "atm faq";

	/** A key used for when something is loading more content and needs so save this state to a bundle */
	public static final String IS_LOADING_MORE = "loading";

	/** A key used for when a user is toggling between sections in posted/scheduled activity*/
	public static final String IS_TOGGLING_ACTIVITY = "acttog";

	public static final String GENERAL_CARD_FAQ = "general";

	public static final String DISCOVER_EXTRAS_CARD_FAQ = "discoverExtras";

	public static final String TRAVEL_CARD_FAQ  = "travel";

	public static final String PAYMENTS_AND_TRANS_CARD_FAQ  = "paymentsAndTerms";

	public static final String PUSH_TEXT_ALERT_CARD_FAQ  = "pushTextAlert";

	public static final String REFER_FRIEND_CARD_FAQ  = "referFriend";

	public static final String SEND_MONEY_CARD_FAQ  = "sendMoney";

	public static final String CARD_MODE_KEY = "cmk";

	/**
	 * String used to send data back from the frequency widget
	 */
	public static final String FREQUENCY_CODE = "code";
	public static final String FREQUENCY_TEXT = "text";

	/** Keys used when passing around internal and external accounts */
	public static final String INTERNAL_ACCOUNTS = "intaccts";
	public static final String EXTERNAL_ACCOUNTS = "extaccts";
	public static final String DATE = "date";
	public static final String SHOULD_NAVIGATE_BACK = "snb";
	public static final String TRANSFER_SUCCESS_DATA = "tsd";

	/**Used to signify sort set up*/
	public static final int SORT_DATE_DESC = 0;
	public static final int SORT_DATE_ASC = 1;
	public static final int SORT_DESCRIP_DESC = 2;
	public static final int SORT_DESCRIP_ASC = 3;
	public static final int SORT_AMOUNT_DESC = 4;
	public static final int SORT_AMOUNT_ASC = 5;

	/**ATM bundle keys*/
	public static final String LAT = "lat";
	public static final String LON = "lon";
	public static final String STREET_LAT = "slat";
	public static final String STREET_LON = "slon";
	public static final String ATM_ID = "atmId";
	public static final String FROM_ADDRESS = "from";
	public static final String TO_ADDRESS = "to";

	/**
	 * NOT INSTANTIABLE
	 */
	private BankExtraKeys() {
		throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
	}
}
