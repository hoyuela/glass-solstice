package com.discover.mobile.bank;

/**
 * Class of keys that passed around in bundles so that data can be extracted from the bundles.
 * @author jthornton
 *
 */
public final class BankExtraKeys {

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
	
	/**
	 * Used to let the first step of check deposit know if the user has just accepted 
	 * the terms and should see the how it works modal
	 */
	public static final String ACCEPTED_TERMS = "accepted";

	/**Used to signify sort set up*/
	public static final int SORT_DATE_DESC = 0;
	public static final int SORT_DATE_ASC = 1;
	public static final int SORT_DESCRIP_DESC = 2;
	public static final int SORT_DESCRIP_ASC = 3;
	public static final int SORT_AMOUNT_DESC = 4;
	public static final int SORT_AMOUNT_ASC = 5;


	/**
	 * NOT INSTANTIABLE
	 */
	private BankExtraKeys() {
		throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
	}
}
