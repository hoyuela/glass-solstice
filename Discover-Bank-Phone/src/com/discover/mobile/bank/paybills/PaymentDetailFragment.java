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
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.services.payee.GetPayeeServiceCall;
import com.discover.mobile.bank.services.payment.PaymentDetail;
import com.discover.mobile.bank.ui.fragments.DetailFragment;
import com.discover.mobile.bank.ui.table.ListItemGenerator;
import com.discover.mobile.bank.ui.table.ViewPagerListItem;

public class PaymentDetailFragment extends DetailFragment{
	private PaymentDetail item;

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
		item = (PaymentDetail) argumentBundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM);
		final ListItemGenerator generator = new ListItemGenerator(getActivity());
		final List<ViewPagerListItem> elementList = generator.getScheduledPaymentDetailList(item);
		final LinearLayout contentTable = (LinearLayout)fragmentView.findViewById(R.id.content_table);

		for(final ViewPagerListItem element : elementList) {
			if(element != null){
				contentTable.addView(element);
			}
		}

		customSetup(fragmentView);
	}


	/**
	 * If the current Fragment is a completed payment, hide the
	 * payment and edit buttons because it is not editable.
	 */
	protected void customSetup(final View mainView) {
		/** Reference to button used to delete a scheduled payment */
		final Button deleteButton = (Button)mainView.findViewById(R.id.delete_payment_button);
		/** Reference to button used to edit a scheduled payment */
		final Button editButton = (Button)mainView.findViewById(R.id.edit_payment_button);

		//If the payment is not a scheduled payment, hide the delete and edit button.
		if(!"SCHEDULED".equalsIgnoreCase(item.status) || item.isJointPayment ){
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
					editPayment();
				}
			});
		}
	}

	/**
	 * This method will trigger a service call to download payees if they are not cached. If payees are already cached
	 * the application navigates to the edit payment screen.
	 * 
	 */
	public void editPayment() {
		/**
		 * Specify edit mode in the bundle so upon receiving the response the handler will have context as to why this
		 * service call was made and handle it appropriately.
		 */
		final Bundle bundle = getArguments();
		bundle.putBoolean(BankExtraKeys.EDIT_MODE, true);

		/**
		 * Check if payees are cached otherwise download payees to fetch the earliest payment date for the payment being
		 * edited.
		 */
		if (BankUser.instance().hasPayees()) {
			BankConductor.navigateToPayBillStepTwo(bundle);
		} else {
			final GetPayeeServiceCall payeeService = BankServiceCallFactory.createGetPayeeServiceRequest();
			payeeService.getExtras().putAll(bundle);
			payeeService.submit();
		}
	}
}
