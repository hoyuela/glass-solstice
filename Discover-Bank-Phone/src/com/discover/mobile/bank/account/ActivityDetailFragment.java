package com.discover.mobile.bank.account;

import java.util.List;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.account.activity.ActivityDetail;
import com.discover.mobile.bank.ui.fragments.DetailFragment;
import com.discover.mobile.bank.ui.table.ListItemGenerator;
import com.discover.mobile.bank.ui.table.ViewPagerListItem;
import com.discover.mobile.bank.util.BankStringFormatter;
import com.google.common.base.Strings;

/**
 * The Fragment responsible for presenting detailed information about a users Transactions.
 * @author scottseward
 *
 */
public class ActivityDetailFragment extends DetailFragment {
	
	@Override
	protected int getFragmentLayout() {
		return R.layout.transaction_detail;
	}

	/**
	 * Setup the fragment layout with necessary information found in the ActivityDetail object 
	 * to present.
	 * This method is called by an AsyncTask.
	 */
	@Override
	protected void setupFragmentLayout(final View fragmentView) {
		final ActivityDetail item = (ActivityDetail)getArguments().getSerializable(BankExtraKeys.DATA_LIST_ITEM);
		
		if(Strings.isNullOrEmpty(item.status) || ActivityDetail.POSTED.equalsIgnoreCase(item.status)){
			setupTransactionData(item, fragmentView);
		}else{
			setupScheduledTransactionData(item, fragmentView);
		}
		
	}


	/**
	 * Inserts the information provided by the ActivityDetail item into the content table that exists
	 * inside of the fragmentView layout.
	 * @param item an ActivityDetail object which contains information about a scheduled transaction.
	 * @param fragmentView
	 */
	private void setupScheduledTransactionData(final ActivityDetail item, final View fragmentView) {
		final ListItemGenerator generator = new ListItemGenerator(fragmentView.getContext());
		final LinearLayout contentTable = (LinearLayout)fragmentView.findViewById(R.id.content_table);
		List<ViewPagerListItem> items = null;
		contentTable.removeAllViews();
		if(ActivityDetail.TYPE_PAYMENT.equals(item.type)) {
			items = generator.getScheduledBillPayList(item);
		}
		else if(ActivityDetail.TYPE_DEPOSIT.equals(item.type)) {
			items = generator.getScheduledDepositList(item);
		}
		else if(ActivityDetail.TYPE_TRANSFER.equals(item.type)) {
			items = generator.getScheduledTransferList(item);
		}
		
		//Add the items to the content table.
		if(items != null){
			for(final ViewPagerListItem row : items){ 
				contentTable.addView(row);
			}
		}
		
	}
	
	/**
	 * Sets up this fragment do display standard Transaction data.
	 * This data is data which has already been posted to the current account and is not scheduled.
	 * @param item an ActivityDetail item which contains information that can describe the 
	 * 				posted ActivityDetail.
	 * @param fragmentView the layout which will contains the content table that will be populated with data.
	 */
	private void setupTransactionData(final ActivityDetail item, final View fragmentView) {
		final LinearLayout contentTable = (LinearLayout)fragmentView.findViewById(R.id.content_table);
		
		((TextView)contentTable.findViewById(R.id.amount_cell))
		.setText(BankStringFormatter.convertCentsToDollars(item.amount.value));
		((TextView)contentTable.findViewById(R.id.description_cell)).setText(item.description);
		final TextView transactionId = ((TextView)contentTable.findViewById(R.id.transaction_id));
		if(!item.id.equals("0")){
			transactionId.setText(item.id);
		}else{
			transactionId.setVisibility(View.GONE);
		}
		
		((TextView)contentTable.findViewById(R.id.date_cell)).setText(
				BankStringFormatter.convertDate(
						item.postedDate.split(ActivityDetail.DATE_DIVIDER)[0]));
		((TextView)contentTable.findViewById(R.id.balance_cell))
		.setText(BankStringFormatter.convertCentsToDollars(item.balance.value));
	}

}
