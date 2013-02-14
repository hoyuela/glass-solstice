package com.discover.mobile.bank.account;

import android.widget.LinearLayout;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.account.activity.ActivityDetail;

public class ActivityDetailFragment extends DetailFragment {
	
	@Override
	protected int getFragmentLayout() {
		return R.layout.transaction_detail;
	}

	@Override
	protected void loadListItemsTo(final LinearLayout contentTable) {
		final ActivityDetail item = (ActivityDetail)getArguments().getSerializable(BankExtraKeys.DATA_LIST_ITEM);
		loadListElementsToLayoutFromList(contentTable, generator.getDetailTransactionList(item));
	}

}
