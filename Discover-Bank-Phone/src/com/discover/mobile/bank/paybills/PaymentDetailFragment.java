package com.discover.mobile.bank.paybills;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.services.payment.PaymentDetail;
import com.discover.mobile.bank.ui.fragments.DetailFragment;
import com.discover.mobile.bank.ui.table.ListItemGenerator;
import com.discover.mobile.bank.ui.table.ViewPagerListItem;

public class PaymentDetailFragment extends DetailFragment{
	private PaymentDetail item;
	/**
	 * Reference to button used to delete a scheduled payment
	 */
	private Button deleteButton;
	/**
	 * Reference to button used to edit a scheduled payment
	 */
	private Button editButton;
	
	@Override
	protected int getFragmentLayout() {
		return R.layout.payment_detail;
	}
	
	/**
	 * Insert the appropriate data into the layout to be displayed.
	 */
	@Override
	protected void setupFragmentLayout(final View fragmentView) {
		final Bundle argumentBundle = getArguments();
		item = (PaymentDetail)argumentBundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM);
		final ListItemGenerator generator = new ListItemGenerator(this.getActivity());
		final List<ViewPagerListItem>elementList = generator.getScheduledPaymentDetailList(item);
		final LinearLayout contentTable = (LinearLayout)fragmentView.findViewById(R.id.content_table);
		
		for(final ViewPagerListItem element : elementList) {
			if(element != null)
				contentTable.addView(element);
		}
		
		customSetup(fragmentView);
	}


	/**
	 * If the current Fragment is a completed payment, hide the
	 * payment and edit buttons because it is not editable.
	 */
	protected void customSetup(final View mainView) {
		//If the payment is not a scheduled payment, hide the delete and edit button.
		deleteButton = (Button)mainView.findViewById(R.id.delete_payment_button);
		editButton = (Button)mainView.findViewById(R.id.edit_payment_button);
		if(!"SCHEDULED".equals(item.status) || item.isJointPayment ){
			deleteButton.setVisibility(View.GONE);
			editButton.setVisibility(View.GONE);
		}else{
			deleteButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(final View v) {
					BankConductor.navigateToDeleteConfirmation(item);
				}
			});
			
			editButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(final View v) {
					BankConductor.navigateToPayBillStepTwo(getArguments());
				}
			});
		}
	}
}
