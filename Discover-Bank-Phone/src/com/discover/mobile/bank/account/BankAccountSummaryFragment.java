package com.discover.mobile.bank.account;


import java.util.Collections;
import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.discover.mobile.bank.BankNavigator;
import com.discover.mobile.bank.BankUser;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.account.AccountList;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.ui.help.NeedHelpFooter;

/**
 * Fragment used to display all of a user's account information in a single view using BankGroupView and BankAccountView
 * to organize the data in the layout. The layout used to display the content can be found in res/layout/bank_account_summary_view.xml.
 * The fragment uses the list of Accounts stored in the BankUser singleton instance.
 * 
 * @author henryoyuela
 *
 */
public class BankAccountSummaryFragment extends BaseFragment implements OnClickListener {
	private LinearLayout accountSummary; 
	private Button openAccount;
	private NeedHelpFooter helpFooter;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.bank_account_summary_view, null);


		final TextView salutation = (TextView) view.findViewById(R.id.account_name);
		salutation.setText(setFirstName());

		/**Fetch linear layout that will contain list of account groups*/
		accountSummary = (LinearLayout)view.findViewById(R.id.bank_summary_list);
		
		/**Button used to open a new account*/
		openAccount = (Button)view.findViewById(R.id.openAccount);
		openAccount.setOnClickListener(this);

		/**Create footer that will listen when user taps on Need Help Number to dial*/
		helpFooter = new NeedHelpFooter((ViewGroup)view);
		helpFooter.setToDialNumberOnClick(com.discover.mobile.bank.R.string.bank_need_help_number_text);
		
		/**Setup list of account groups using the list of Accounts downloaded at login*/
		this.populateList(BankUser.instance().getAccounts());

		/**Hyperlink used to provide feedback*/
		final TextView feedback = (TextView)view.findViewById(R.id.provide_feedback_button);
		feedback.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				final CharSequence text = "Feedback Under Development";
				final int duration = Toast.LENGTH_SHORT;

				final Toast toast = Toast.makeText(DiscoverActivityManager.getActiveActivity(), text, duration);
				toast.show();
			}			
		});
		return view;
	}

	/** Set the first name in the status bar. The first name can sometimes be 
	 * returned in all caps and only the first letter should be capitalized. 
	 */
	private String setFirstName() {
		final String firstName = BankUser.instance().getCustomerInfo().name.type;
		final String name = firstName.toLowerCase();
		final String upperString = name.substring(0,1).toUpperCase() + name.substring(1);
		return "Hi, " + upperString;
	}

	/**
	 * Method used to display the account information stored in the AccountList object. This method
	 * sorts the list of accounts stored in accountList, groups them based on account name, then add
	 * each account into an AccountGroupView. Each AccountGroupView will only hold accounts that are of
	 * the same type.
	 * 
	 * @param accountList Reference to a list of Account objects containing a users accounts information
	 */
	public void populateList(final AccountList accountList) {
		if( null != accountList ) {
			final Context context = this.getActivity();

			//Create a hash map to help sort accounts into groups 
			final HashMap<String, BankAccountGroupView> groupsMap = new HashMap<String, BankAccountGroupView>();

			Collections.sort(accountList.accounts, new BankAccountComparable());

			//Iterate through list of accounts, group them together and add to the summary list view
			for(final Account account : accountList.accounts) {
				//Fetch group from hashmap if it already exists
				BankAccountGroupView group = groupsMap.get(account.type);

				//if group type does not exist add new group to hashmap
				if( null == group ) {
					//Create new group to hold list of accounts for the type specified in account
					group = new BankAccountGroupView(context);

					//Add group to hashmap to help sort
					groupsMap.put(account.type, group);

					//Add new group view to the summary list
					accountSummary.addView(group);
				}

				//Add account to group
				group.addAccount(account);	
			}
		} else {
			//TODO: Log an error
			return;
		}

	}

	/*
	 * (non-Javadoc)
	 * @see com.discover.mobile.BaseFragment#getActionBarTitle()
	 */
	@Override
	public int getActionBarTitle() {
		return BaseFragment.NO_TITLE;
	}

	@Override
	public void onClick(final View sender) {
		if( sender == openAccount ) {
			BankNavigator.navigateToBrowser(BankUrlManager.getOpenAccountUrl());
		}
		
	}

}
