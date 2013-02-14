package com.discover.mobile.bank.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.BankServiceCallFactory;
import com.discover.mobile.bank.BankUser;
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
	private AccountActivityHeader header;

	private ToggleButton posted;

	private ToggleButton scheduled;

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

		table = (ActivityTable) view.findViewById(R.id.activity_table);
		header = (AccountActivityHeader) view.findViewById(R.id.header);
		posted = (ToggleButton) view.findViewById(R.id.posted_button);
		scheduled = (ToggleButton) view.findViewById(R.id.scheduled_button);

		posted.setOnCheckedChangeListener(getPostedListener());

		scheduled.setOnCheckedChangeListener(getShceduledListener());

		final BankUser user = BankUser.instance();
		user.getAccounts();

		//		final Account account = new Account();
		//		account.balance="$-366.97";
		//		account.nickname="Jon NickNamed Account";
		//		account.ending="1234566766u908q43u509q";

		//header.addAccount(account);

		//TODO: show stuff on the on resume to prevent slow performance

		/**
		 * TODO: Remove this
		 */
		header.getHelp().setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(final View v) {
				BankServiceCallFactory.createGetActivityServerCall("api/accounts/0/activity").submit();
			}

		});

		return view;
	}

	public OnCheckedChangeListener getPostedListener(){
		return new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
				if(isChecked){
					posted.setTextColor(getResources().getColor(R.color.white));
					scheduled.setTextColor(getResources().getColor(R.color.body_copy));
					posted.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_left_on));
					scheduled.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_right_off));
					scheduled.setChecked(false);
					getPostedTransactions();
				}
			}

		};
	}

	public OnCheckedChangeListener getShceduledListener(){
		return new OnCheckedChangeListener(){

			@Override 
			public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
				if(isChecked){
					scheduled.setTextColor(getResources().getColor(R.color.white));
					posted.setTextColor(getResources().getColor(R.color.body_copy));
					posted.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_left_off));
					scheduled.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_right_on));
					posted.setChecked(false);
					getScheduledTransactions();
				}
			}

		};
	}

	protected void getScheduledTransactions(){
		table.clearList();
		BankServiceCallFactory.createGetActivityServerCall("/api/accounts/0/activity?status=posted").submit();
	}

	protected void getPostedTransactions(){
		table.clearList();
		BankServiceCallFactory.createGetActivityServerCall("/api/accounts/0/activity?status=posted").submit();
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
		table.showItems(list.activities);
		table.setActivities(list);
	}
}
