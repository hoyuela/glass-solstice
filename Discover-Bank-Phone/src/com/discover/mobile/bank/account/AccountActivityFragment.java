package com.discover.mobile.bank.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.BankServiceCallFactory;
import com.discover.mobile.bank.BankUser;
import com.discover.mobile.bank.DynamicDataFragment;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.account.Account;
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

	private ListActivityDetail posted;

	private ListActivityDetail scheduled;

	private ToggleButton postedButton;

	private ToggleButton scheduledButton;

	private Account account;

	private boolean isPosted = true;

	/**
	 * TODO:
	 * 
	 * 1. Handle rotation.
	 * 2. Handle data load on button selected.
	 * 3. Handle preventing a data load while data is loading.
	 * 4. Show load more when loading more.
	 * 5. Implement load more.
	 */

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
		postedButton = (ToggleButton) view.findViewById(R.id.posted_button);
		scheduledButton = (ToggleButton) view.findViewById(R.id.scheduled_button);

		postedButton.setOnCheckedChangeListener(getPostedListener());
		scheduledButton.setOnCheckedChangeListener(getShceduledListener());

		final Bundle loadBundle = (null == savedInstanceState) ? this.getArguments() : savedInstanceState;
		loadDateFromBundle(loadBundle);
		return view;
	}

	@Override
	public void onResume(){
		super.onResume();
		account = BankUser.instance().getCurrentAccount();
		header.addAccount(BankUser.instance().getCurrentAccount());
		table.clearList();
		if(isPosted){
			showList(posted);
		}else{
			showList(scheduled);
		}
	}

	@Override
	public void onSaveInstanceState(final Bundle outState){

	}

	public void loadDateFromBundle(final Bundle bundle){
		if(null == bundle){return;}
		isPosted = bundle.getBoolean(BankExtraKeys.CATEGORY_SELECTED, true);
		posted = (ListActivityDetail)bundle.getSerializable(BankExtraKeys.DATA_LIST);
		//TODO: hanle the rest of these
	}

	public void showList(final ListActivityDetail list){
		table.clearList();
		if(null != list){
			table.clearActivties();
			table.showItems(list.activities, isPosted);
			table.setActivities(list);
		}
	}

	public OnCheckedChangeListener getPostedListener(){
		return new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
				if(isChecked){
					toggleButton(postedButton, scheduledButton, true);		
					if(null != posted && !posted.activities.isEmpty()){
						showList(posted);
					}else{
						getPostedTransactions();
					}
				}
			}

		};
	}

	public OnCheckedChangeListener getShceduledListener(){
		return new OnCheckedChangeListener(){

			@Override 
			public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
				if(isChecked){
					toggleButton(scheduledButton, postedButton, false);			
					if(null != scheduled && !scheduled.activities.isEmpty()){
						showList(scheduled);
					}else{
						getScheduledTransactions();
					}
				}
			}

		};
	}

	protected void toggleButton(final ToggleButton checked, final ToggleButton notChecked, final boolean isPosted){
		checked.setTextColor(getResources().getColor(R.color.white));
		notChecked.setTextColor(getResources().getColor(R.color.body_copy));
		notChecked.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_left_off));
		checked.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_right_on));
		notChecked.setChecked(false);
		this.isPosted = isPosted;
	}

	/**
	 * 
	 */
	protected void getScheduledTransactions(){
		table.clearList();
		final int time = 3000;
		Toast.makeText(getActivity(), "Under Construction", time).show();
	}

	protected void getPostedTransactions(){
		table.clearList();
		BankServiceCallFactory.createGetActivityServerCall("/api/accounts/1/activity?status=posted").submit();
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
		table.showItems(list.activities, isPosted);
		table.setActivities(list);
		if(isPosted){
			posted = table.getActivities();
		}else{
			scheduled = table.getActivities();
		}
	}
}
