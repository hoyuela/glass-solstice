package com.discover.mobile.bank.deposit;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.BankNavigator;
import com.discover.mobile.bank.BankUser;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.ui.table.ViewPagerListItem;

/**
 * Fragment used to display the Check Deposit - Select Account page. This is the first step in the
 * Check Deposit work-flow. Displays a list of accounts the user can select from for depositing a check.
 * 
 * @author henryoyuela
 *
 */
public class BankDepositSelectAccount extends BankDepositBaseFragment {
	/**
	 * Used to log into Android logcat
	 */
	private static final String TAG = "SelectAccount";

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		
		/**Hide controls that are not needed*/
		actionButton.setVisibility(View.GONE);
		actionLink.setVisibility(View.GONE);
		noteTitle.setVisibility(View.GONE);
		noteTextMsg.setVisibility(View.GONE);

		
		/**Hide top note as it is not needed for this view**/
		final TextView topNote = (TextView)view.findViewById(R.id.top_note_text);
		topNote.setVisibility(View.GONE);
		
		return view;
	}
	
	@Override
	protected List<ViewPagerListItem> getViewPagerListContent() {
		//Return null so the base class knows to use getRelativeLayoutListContent instead.
		return null;
	}

	/**
	 * Method returns a list of RelativeLayout objects displaying account information. This method is called by the super
	 * class in onCreateView method.
	 * 
	 * @return Returns a list of RelativeLayout objects 
	 */
	@Override
	protected List<RelativeLayout> getRelativeLayoutListContent() {
		/**Get list of accounts downloaded at login*/
		final List<Account> accounts = BankUser.instance().getAccounts().accounts;
		final List<RelativeLayout> items = new ArrayList<RelativeLayout>();
				
		final Context context = getActivity();
		BankSelectAccountItem item = null;
		
		for( int i = 0; i < accounts.size(); i++) {
			final Account account = accounts.get(i);
			
			if( account.isDepositEligible() ) {		
				item = new BankSelectAccountItem(context, account, this);	
				
				if( items.size() > 0 ) {
					item.drawTopStroke(context);
				} 
				
				items.add(item);
			}
		}
		
		if( item != null ){
			item.drawBottomStroke(context);
		}
		
		return items;													
	}

	/**
	 * Method called by base class in onCreateView to determine what the title of the page should be.
	 */
	@Override
	protected String getPageTitle() {
		return getActivity().getResources().getString( R.string.bank_deposit_select_account );
	}
	
	@Override
	protected void onActionButtonClick() {
		//Nothing to do here	
	}

	@Override
	protected void onActionLinkClick() {
		//Nothing to do here		
	}
	
	/**
	 * Click handler for when an item in the list displayed is selected by the user.
	 */
	@Override
	public void onClick(final View sender) {
		super.onClick(sender);
		
		/**Verify that a BankSelectAccountItem generated the click event*/
		if( sender instanceof BankSelectAccountItem) {
			/**Fetch reference to account object associated with the BankSelectAccountItem that generated the click event*/
			final Account account = ((BankSelectAccountItem)sender).getAccount();
			
			/**Verify account reference is not null*/
			if( null != account ) {
				/**See if the limits for the account have already been downloaded and cached*/
				if( null != account.limits) {
					/**Navigate to Check Deposit - Select Amount Page*/
					final Bundle bundle = new Bundle();
					bundle.putSerializable(BankExtraKeys.DATA_LIST_ITEM, account);
					BankNavigator.navigateToCheckDepositWorkFlow(bundle);
				} else {
					/**
					 * Send a request to download account limits for the selected account. If successful
					 * it will navigate to select account step 2 in check deposit work flow and send selected account
					 * */
					BankServiceCallFactory.createGetAccountLimits(account).submit();
				}
			} else {
				if( Log.isLoggable(TAG, Log.ERROR)) {
					Log.e(TAG, "Unable to retreive account limits");
				}
			}
		}
	}

	@Override
	protected int getProgressIndicatorStep() {
		return 0;
	}

	@Override
	public boolean isBackPressDisabled() {
		return false;
	}

	@Override
	public void onBackPressed() {
		//Nothing To Do Here
	}
}
