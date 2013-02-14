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
	public static final String DATA_LIST = "data";

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
