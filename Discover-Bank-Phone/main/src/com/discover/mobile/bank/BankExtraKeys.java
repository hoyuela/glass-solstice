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


	/**
	 * NOT INSTANTIABLE
	 */
	private BankExtraKeys() {
		throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
	}
}
