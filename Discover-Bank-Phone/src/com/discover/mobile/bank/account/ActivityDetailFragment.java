package com.discover.mobile.bank.account;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.account.activity.ActivityDetail;
import com.discover.mobile.bank.ui.fragments.DetailFragment;
import com.discover.mobile.bank.util.BankStringFormatter;

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

		final LinearLayout contentTable = (LinearLayout)fragmentView.findViewById(R.id.content_table);

		((TextView)contentTable.findViewById(R.id.amount_cell))
		.setText(BankStringFormatter.convertCentsToDollars(item.amount.value));
		((TextView)contentTable.findViewById(R.id.description_cell)).setText(item.description);
		((TextView)contentTable.findViewById(R.id.transaction_id)).setText(item.id);
		((TextView)contentTable.findViewById(R.id.date_cell)).setText(
				BankStringFormatter.convertDate(
						item.dates.get(ActivityDetail.POSTED).split(ActivityDetail.DATE_DIVIDER)[0]));
		((TextView)contentTable.findViewById(R.id.balance_cell))
		.setText(BankStringFormatter.convertCentsToDollars(item.balance.value));		
	}

}
