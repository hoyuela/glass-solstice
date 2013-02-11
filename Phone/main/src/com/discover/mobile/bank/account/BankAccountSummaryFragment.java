package com.discover.mobile.bank.account;


import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.discover.mobile.bank.R;
import com.discover.mobile.common.BankUser;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.bank.account.Account;
import com.discover.mobile.common.bank.account.AccountList;

public class BankAccountSummaryFragment extends BaseFragment{
	private LinearLayout accountSummary; 
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.bank_account_summary_view, null);

		accountSummary = (LinearLayout)view.findViewById(R.id.bank_summary_list);
		
		this.populateList(BankUser.instance().getAccounts());
			
		return view;
	}
	
	public void populateList(final AccountList accountList) {
		if( null != accountList ) {
			final Context context = this.getActivity();
		
			//Create a hash map to help sort accounts into groups 
			final HashMap<String, BankAccountGroupView> groupsMap = new HashMap<String, BankAccountGroupView>();
			
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
	
	@Override
	public int getActionBarTitle() {
		return R.string.bank_account_summary;
	}

}
