package com.discover.mobile.bank.account;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.payment.PaymentDetail;

public class PaymentDetailFragment extends DetailFragment {
	private PaymentDetail item;
	
	@Override
	protected int getFragmentLayout() {
		
		return R.layout.payment_detail;
	}

	@Override
	protected void loadListItemsTo(final LinearLayout contentTable) {
		item = (PaymentDetail)getArguments().getSerializable(BankExtraKeys.DATA_LIST_ITEM);
		
		loadListElementsToLayoutFromList(contentTable, generator.getScheduledPaymentDetailList(item));
	}
	
	@Override
	protected void customSetup(final View mainView) {
		if("COMPLETED".equals(item.status)){
			((Button)mainView.findViewById(R.id.delete_payment_button)).setVisibility(View.GONE);
			((Button)mainView.findViewById(R.id.edit_payment_button)).setVisibility(View.GONE);
		}
	}
	
}
