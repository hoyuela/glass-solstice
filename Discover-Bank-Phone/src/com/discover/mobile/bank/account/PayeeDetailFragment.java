package com.discover.mobile.bank.account;

import android.widget.LinearLayout;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.payee.PayeeDetail;

public class PayeeDetailFragment extends DetailFragment {
	private PayeeDetail item;

	/**
	 * The layout for a PaymentDetail fragment.
	 */
	@Override
	protected int getFragmentLayout() {
		return R.layout.payee_detail;
	}

	/**
	 * Load list elements from the list item generator into the content table.
	 */
	@Override
	protected void loadListItemsTo(final LinearLayout contentTable) {
		item = (PayeeDetail)getArguments().getSerializable(BankExtraKeys.SELECTED_PAYEE);
		
		loadListElementsToLayoutFromList(contentTable, generator.getPayeeDetailList(item));
	}


}
