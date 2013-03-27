package com.discover.mobile.bank.account;


import java.util.Collections;
import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.help.HelpMenuListFactory;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.account.AccountList;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.help.HelpWidget;
import com.discover.mobile.common.ui.help.NeedHelpFooter;
import com.discover.mobile.common.ui.widgets.AccountToggleView;

/**
 * Fragment used to display all of a user's account information in a single view using BankGroupView and BankAccountView
 * to organize the data in the layout. The layout used to display the content can be found in res/layout/bank_account_summary_view.xml.
 * The fragment uses the list of Accounts stored in the BankUser singleton instance.
 * 
 * @author henryoyuela
 *
 */
public class BankAccountSummaryFragment extends BaseFragment implements OnClickListener {
	private static final String TAG = "AccountSummary";
	private LinearLayout accountSummary; 
	private Button openAccount;
	private NeedHelpFooter helpFooter;
	private AccountToggleView toggleView;
	private View view;
	private ImageView accountToggleIcon;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		this.view = inflater.inflate(R.layout.bank_account_summary_view, null);


		final TextView salutation = (TextView) view.findViewById(R.id.account_name);
		salutation.setText(setFirstName());

		/**Fetch linear layout that will contain list of account groups*/
		accountSummary = (LinearLayout)view.findViewById(R.id.bank_summary_list);

		/**Button used to open a new account*/
		openAccount = (Button)view.findViewById(R.id.openAccount);
		openAccount.setOnClickListener(this);

		/**Help icon setup*/
		final HelpWidget help = (HelpWidget) view.findViewById(R.id.help);
		help.showHelpItems(HelpMenuListFactory.instance().getAccountHelpItems());

		/**Create footer that will listen when user taps on Need Help Number to dial*/
		helpFooter = new NeedHelpFooter((ViewGroup)view);
		helpFooter.setToDialNumberOnClick(com.discover.mobile.bank.R.string.bank_need_help_number_text);

		/**Setup list of account groups using the list of Accounts downloaded at login*/
		this.populateList(BankUser.instance().getAccounts());
		
		//TODO: Commented this out as it is causing a crash
		//setupAccountToggle();

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
			BankAccountGroupView prevGroup = null;
			//Iterate through list of accounts, group them together and add to the summary list view
			for(final Account account : accountList.accounts) {
				final String groupKey = account.getGroupCategory();

				//Fetch group from hashmap if it already exists
				BankAccountGroupView group = groupsMap.get(groupKey);	

				//if group type does not exist add new group to hashmap
				if( null == group ) {
					// Ensures that previous group has last element drawn correctly.
					if(prevGroup != null) {
						if(prevGroup.getGroupSize() == 1) {
							prevGroup.addAllStrokes(context);
						} else {
							prevGroup.addBottomStroke(context);
						}
					}
					//Create new group to hold list of accounts for the type specified in account
					group = new BankAccountGroupView(context);
					prevGroup = group;
					//Add group to hashmap to help sort
					groupsMap.put(groupKey, group);

					//Add new group view to the summary list
					accountSummary.addView(group);
				}

				// Add account to group
				group.addAccount(account);
				group.addTopStroke(context);
			}
			//Ensures the last group drawn has the bottom as a solid stroke.
			if(prevGroup != null) {
				if(prevGroup.getGroupSize() == 1) {
					prevGroup.addAllStrokes(context);
				} else {
					prevGroup.addBottomStroke(context);
				}
			}
		} else {
			if( Log.isLoggable(TAG, Log.WARN)) {
				Log.w(TAG, "Account List is Empty");
			}
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
			BankConductor.navigateToBrowser(BankUrlManager.getOpenAccountUrl());
		}
	}


	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.ACCOUNT_SUMMARY_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.ACCOUNT_SUMMARY_SECTION;
	}
	
	/**
	 * Determines the placement of the icon upon its layout. It's then used to
	 * measure the postion of the indicator. Additionally, this implements the
	 * listeners for the AccountToggle.
	 */
	private void setupAccountToggle() {
		final ViewTreeObserver vto = accountToggleIcon.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				toggleView.setIndicatorPosition(accountToggleIcon.getLeft(),
						accountToggleIcon.getTop(),
						accountToggleIcon.getWidth(),
						accountToggleIcon.getHeight());
			}
		});

		final ImageView accountToggleArrow = (ImageView) view
				.findViewById(R.id.downArrow);
		accountToggleArrow.setOnClickListener(new AccountToggleListener());
		accountToggleIcon.setOnClickListener(new AccountToggleListener());
	}
	
	/**
	 * Listener associated with items that hide/show the Account Toggle Widget. 
	 */
	private class AccountToggleListener implements OnClickListener {

		@Override
		public void onClick(final View v) {
			toggleView.toggleVisibility();
		}
		
	}

}
