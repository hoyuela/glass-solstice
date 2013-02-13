package com.discover.mobile.bank.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.BankServiceCallFactory;
import com.discover.mobile.bank.DynamicDataFragment;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.account.activity.ListActivityDetail;
import com.discover.mobile.bank.ui.table.ActivityTable;
import com.discover.mobile.common.BaseFragment;

/**
 * Fragment that holds posted and scheduled activity
 * @author jthornton
 *
 */
public class AccountActivityFragment extends BaseFragment implements DynamicDataFragment{

	/**Table holding all the activity*/
	private ActivityTable table;

	/**Title view of the page*/
	private TextView title;

	/**
	 * Create the view
	 * @param inflater - inflater to inflate the layout
	 * @param container - container holding the group
	 * @param savedInstanceState - state of the fragment
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {

		final View view = inflater.inflate(R.layout.bank_account_activity, null);

		this.table = (ActivityTable) view.findViewById(R.id.activity_table);
		this.title = (TextView) view.findViewById(R.id.title);

		//TODO: show stuff on the on resume to prevent slow performance

		/**
		 * TODO: Remove this
		 */
		this.title.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(final View v) {
				BankServiceCallFactory.createGetActivityServerCall("/api/accounts/1/activity?status=posted").submit();
			}

		});

		return view;
	}

	/**
	 * Get the action bar title
	 * @return the title of the fragment
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.pay_a_bill_title;
	}

	/**
	 * Handle the data received from the server.
	 * @param bundle - bundle containing the data
	 */
	@Override
	public void handleReceivedData(final Bundle bundle) {
		final ListActivityDetail list = (ListActivityDetail) bundle.getSerializable(BankExtraKeys.DATA_LIST);
		this.table.showItems(list.activities);
		this.table.setActivities(list);
	}
}