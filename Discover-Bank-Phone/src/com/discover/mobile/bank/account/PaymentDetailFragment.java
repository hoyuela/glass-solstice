package com.discover.mobile.bank.account;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.BankNavigator;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.payment.PaymentDetail;

public class PaymentDetailFragment extends DetailFragment implements OnClickListener {
	private PaymentDetail item;
	/**
	 * Reference to button used to delete a scheduled payment
	 */
	private Button deleteButton;
	
	/**
	 * The layout for a PaymentDetail fragment.
	 */
	@Override
	protected int getFragmentLayout() {
		return R.layout.payment_detail;
	}

	/**
	 * Load the list items into the content table.
	 */
	@Override
	protected void loadListItemsTo(final LinearLayout contentTable) {
		final PaymentDetail paymentObject = (PaymentDetail)getArguments().getSerializable(BankExtraKeys.DATA_LIST_ITEM);
		
		if(paymentObject != null)
			loadListElementsToLayoutFromList(contentTable, generator.getScheduledPaymentDetailList(paymentObject));
		
	}
	
	/**
	 * Hide the edit and delete buttons if the transaction is not scheduled, and is therefore
	 * posted or cancelled.
	 */
	@Override
	protected void customSetup(final View mainView) {
		//If the payment is not a scheduled payment, hide the delete and edit button.
		if(!"SCHEDULED".equals(item.status)){
			((Button)mainView.findViewById(R.id.delete_payment_button)).setVisibility(View.GONE);
			((Button)mainView.findViewById(R.id.edit_payment_button)).setVisibility(View.GONE);
		}
	}

	/**
	 * Method used to handle on click events generated form views within this fragment.
	 */
	@Override
	public void onClick(final View sender) {
		//Verify delete button generated the click event
		if( sender == deleteButton ) {
			//Display a modal to confirm user action
			BankNavigator.navigateToDeleteConfirmation(item);
		}	
	}
	
}
