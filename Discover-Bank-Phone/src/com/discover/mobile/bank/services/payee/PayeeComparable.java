package com.discover.mobile.bank.services.payee;

import java.util.Comparator;

/**
 * Comparator class used for sorting PayeeDetail objects based on nickname.
 * 
 * @author henryoyuela
 *
 */
public class PayeeComparable implements Comparator<PayeeDetail> {

	@Override
	public int compare(final PayeeDetail arg0, final PayeeDetail arg1) {

		return arg0.nickName.compareToIgnoreCase(arg1.nickName);
	}

}
