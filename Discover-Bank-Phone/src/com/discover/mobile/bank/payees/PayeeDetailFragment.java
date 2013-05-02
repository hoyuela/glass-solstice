package com.discover.mobile.bank.payees;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.services.payee.PayeeDetail;
import com.discover.mobile.bank.ui.fragments.DetailFragment;
import com.discover.mobile.bank.ui.table.ListItemGenerator;
import com.discover.mobile.bank.ui.table.ViewPagerListItem;
import com.discover.mobile.common.DiscoverActivityManager;

public class PayeeDetailFragment extends DetailFragment implements OnClickListener {
	private PayeeDetail item;

	/**
	 * The layout for a PaymentDetail fragment.
	 */
	@Override
	protected int getFragmentLayout() {
		return R.layout.payee_detail;
	}

	@Override
	protected void setupFragmentLayout(final View fragmentView) {
		final Bundle argumentBundle = getArguments();
		item = (PayeeDetail)argumentBundle.getSerializable(BankExtraKeys.SELECTED_PAYEE);
		final ListItemGenerator generator = new ListItemGenerator(DiscoverActivityManager.getActiveActivity());
		final List<ViewPagerListItem>listElements = generator.getPayeeDetailList(item);
		
		final LinearLayout content = (LinearLayout)fragmentView.findViewById(R.id.content_table);
		for(final ViewPagerListItem element: listElements){
			content.addView(element);
		}
		
		final Button editButton = (Button)fragmentView.findViewById(R.id.edit_payee_button);
		editButton.setOnClickListener(this);
		
		final Button deleteButton = (Button)fragmentView.findViewById(R.id.delete_payee_button);
		deleteButton.setOnClickListener(this);
		
	}

	@Override
	public void onClick(final View sender) {
		final int senderId = sender.getId(); 
		final Bundle bundle = new Bundle();
		bundle.putSerializable(BankExtraKeys.DATA_LIST_ITEM, item);
		
		if( senderId == R.id.edit_payee_button ) {
			/**Check if payee is managed or unmanaged*/
			if( item.verified ) {
				BankConductor.navigateToAddPayee(BankAddManagedPayeeFragment.class, bundle);
			} else {
				BankConductor.navigateToAddPayee(BankAddUnmanagedPayeeFragment.class, bundle);
			}
		} else if( senderId == R.id.delete_payee_button) {		
			BankConductor.navigateToDeletePayeeModal(bundle);
		}
	}

}
