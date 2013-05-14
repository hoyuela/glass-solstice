package com.discover.mobile.bank.account;


import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.help.HelpMenuListFactory;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.account.AccountList;
import com.discover.mobile.bank.services.customer.Customer;
import com.discover.mobile.bank.services.json.Name;
import com.discover.mobile.bank.ui.widgets.BankLayoutFooter;
import com.discover.mobile.bank.ui.widgets.FooterType;
import com.discover.mobile.bank.util.FragmentOnBackPressed;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.help.HelpWidget;
import com.discover.mobile.common.ui.widgets.AccountToggleView;
import com.discover.mobile.common.utils.CommonUtils;

/**
 * Fragment used to display all of a user's account information in a single view using BankGroupView and BankAccountView
 * to organize the data in the layout. The layout used to display the content can be found 
 * in res/layout/bank_account_summary_view.xml.
 * The fragment uses the list of Accounts stored in the BankUser singleton instance.
 * 
 * @author henryoyuela
 *
 */
public class BankAccountSummaryFragment extends BaseFragment implements OnClickListener, FragmentOnBackPressed {

	private static final String TAG = "AccountSummary";

	private static final String SHOW_TOGGLE_KEY = "showToggle";

	private LinearLayout accountSummary; 
	private Button openAccount;
	private AccountToggleView toggleView;
	private View view;
	private ImageView accountToggleIcon;
	private RelativeLayout accountToggleSection;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.bank_account_summary_view, null);

		accountToggleSection = (RelativeLayout) view.findViewById(R.id.account_toggle_layout);
		
		/**Fetch linear layout that will contain list of account groups*/
		accountSummary = (LinearLayout)view.findViewById(R.id.bank_summary_list);

		/**Button used to open a new account*/
		openAccount = (Button)view.findViewById(R.id.openAccount);
		openAccount.setOnClickListener(this);

		/**Help icon setup*/
		final HelpWidget help = (HelpWidget) view.findViewById(R.id.help);
		help.showHelpItems(HelpMenuListFactory.instance().getAccountHelpItems());

		/**Footer setup*/
		final BankLayoutFooter footer = (BankLayoutFooter) view.findViewById(R.id.bank_footer);
		footer.setFooterType(FooterType.PRIVACY_TERMS | FooterType.PROVIDE_FEEDBACK | FooterType.NEED_HELP);

		/**Setup list of account groups using the list of Accounts downloaded at login*/
		this.populateList(BankUser.instance().getAccounts());

		accountToggleIcon = (ImageView) view.findViewById(R.id.cardBankIcon);
		toggleView = (AccountToggleView) view.findViewById(R.id.acct_toggle);

		//If card and bank are authenticated then show the down arrow, since we are here
		//Bank must be authenticated already so we only need to check to see if the card is 
		//authenticated.
		if(BankUser.instance().isSsoUser()){
			view.findViewById(R.id.downArrow).setVisibility(View.VISIBLE);
			view.findViewById(R.id.cardBankIcon).setVisibility(View.VISIBLE);
			setupAccountToggle();
		}

		if (savedInstanceState != null
				&& savedInstanceState.getBoolean(SHOW_TOGGLE_KEY, false)) {
			toggleView.toggleVisibility();
		}

		//Update the background to prevent a pixelated view
		CommonUtils.fixBackgroundRepeat(view.findViewById(R.id.background_view));
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getActivity().registerReceiver(timeListener, new IntentFilter(Intent.ACTION_TIME_TICK));
		updateGreeting();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(timeListener);
	}

	@Override
	public void onSaveInstanceState(final Bundle outState) {
		if(toggleView != null && toggleView.getVisibility() == View.VISIBLE) {
			outState.putBoolean(SHOW_TOGGLE_KEY, true);
		}
		super.onSaveInstanceState(outState);
	}

	/** Set the first name in the status bar. The first name can sometimes be 
	 * returned in all caps and only the first letter should be capitalized. 
	 */
	private String getFirstName() {
		final Customer currentUser = BankUser.instance().getCustomerInfo();
		final StringBuilder nameBuilder = new StringBuilder("");
		Name customerName = null;

		if(currentUser != null) {
			customerName = currentUser.name;
		}
		
		if(customerName != null) {
			final String firstName = customerName.type;
			
			if(firstName != null) {
				final String name = firstName.toLowerCase(Locale.US);
				nameBuilder.append(name.substring(0,1).toUpperCase(Locale.US) + name.substring(1));
			}
		}
		
		return nameBuilder.toString();
	}
		
	private void updateGreeting() {
		final TextView salutation = (TextView) view.findViewById(R.id.account_name);

		new Thread(new SalutationUpdater(salutation, getFirstName(), getActivity())).start();
	}

	/**
	 * A broadcast receiver that will call the update greeting method when the time changes
	 * and if it determines that the time should be updated.
	 * This broadcast is sent by the operating system every minute.
	 */
	private final BroadcastReceiver timeListener = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			if(intent != null) {
				final String action = intent.getAction();
				if(action.equalsIgnoreCase(Intent.ACTION_TIME_TICK)) {
					if(SalutationUpdater.shouldUpdateGreeting()) {
						updateGreeting();
					}
				}
			}
		}
	};

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
			drawBottomStroke(prevGroup);
			
		} else {
			if( Log.isLoggable(TAG, Log.WARN)) {
				Log.w(TAG, "Account List is Empty");
			}
		}

	}
	
	/**
	 * Draws the final stroke on a group of account items.
	 * @param group a group of BankAccountGroupView objects.
	 */
	private void drawBottomStroke(final BankAccountGroupView group) {
		if(group != null) {
			if(group.getGroupSize() == 1) {
				group.addAllStrokes(getActivity());
			} else {
				group.addBottomStroke(getActivity());
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
		if( sender.equals(openAccount) ) {
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
				if(!toggleView.hasIndicatorBeenDrawn()) {

					toggleView.setIndicatorPosition(accountToggleSection.getLeft() - accountToggleIcon.getWidth() / 2,
							accountToggleIcon.getTop(),
							accountToggleIcon.getWidth(),
							accountToggleIcon.getHeight());
				}
			}
		});

		final ImageView accountToggleArrow = (ImageView) view
				.findViewById(R.id.downArrow);
		accountToggleArrow.setOnClickListener(new AccountToggleListener());
		accountToggleIcon.setOnClickListener(new AccountToggleListener());
		accountToggleSection.setOnClickListener(new AccountToggleListener());
	}
	
	@Override
	public void onBackPressed() {
		if(toggleView.getVisibility() == View.VISIBLE) {
			toggleView.setVisibility(View.INVISIBLE);
		}

	}

	@Override
	public boolean isBackPressDisabled() {
		if(toggleView.getVisibility() == View.VISIBLE) {
			return true;
		}

		return false;
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
