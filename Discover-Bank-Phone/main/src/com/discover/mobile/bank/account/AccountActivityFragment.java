package com.discover.mobile.bank.account;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.discover.mobile.bank.BankServiceCallFactory;
import com.discover.mobile.bank.DynamicDataFragment;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.account.activity.ActivityDateDetail;
import com.discover.mobile.bank.services.account.activity.ActivityDetail;
import com.discover.mobile.bank.services.account.activity.ListActivityDetail;
import com.discover.mobile.bank.ui.table.ActivityTable;
import com.discover.mobile.bank.ui.table.BankTable;
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

		/**
		 * TODO: Remove this
		 */
		this.title.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(final View v) {
				BankServiceCallFactory.createGetActivityServerCall("/api/accounts/1/activity?status=posted").submit();
			}

		});

		final Bundle bundle = this.getArguments();
		if(null == bundle){

			final ListActivityDetail list = new ListActivityDetail();
			list.activities = new ArrayList<ActivityDetail>();

			//TODO: Remove this
			for (int i = 0; i < 10; ++i){
				final ActivityDetail item = new ActivityDetail();
				final Random rand = new Random(new Date().getTime() * i);
				item.id = "" + (65465432 & rand.nextInt());
				item.amount = "" + rand.nextInt();
				item.balance = rand.nextInt();
				item.type = "TRANSACTION";
				item.dates = new ActivityDateDetail();
				item.dates.formattedDate = "13/12/1654" ;
				item.description = "Some description, Chicago, IL";
				list.activities.add(item);
			}

			this.table.setActivities(list);
			this.table.showItems();
		}else{
			final ListActivityDetail list = (ListActivityDetail) bundle.getSerializable(BankTable.DATA_LIST);
			this.table.setActivities(list);
			this.table.showItems();
		}

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
		final ListActivityDetail list = (ListActivityDetail) bundle.getSerializable(BankTable.DATA_LIST);
		this.table.setActivities(list);
		this.table.showItems();
	}

}