package com.discover.mobile.bank.deposit;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.BankUser;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.ui.fragments.BankOneButtonFragment;
import com.discover.mobile.bank.ui.table.ViewPagerListItem;

/**
 * Fragment used to display the Check Deposit - Select Account page. This is the first step in the
 * Check Deposit work-flow. Displays a list of accounts the user can select from for depositing a check.
 * 
 * @author henryoyuela
 *
 */
public class BankDepositSelectAccount extends BankOneButtonFragment {

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
		
		/**Setup Progress Indicator to show Payment Details and Payment Scheduled, on step 1, and hide step 2 **/
		progressIndicator.initChangePasswordHeader(0);
		progressIndicator.setTitle(R.string.bank_deposit_enter_details, R.string.bank_deposit_capture, R.string.bank_deposit_confirmation);

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
				item = new BankSelectAccountItem(context, account);	
				
				if( items.size() > 0 ) {
					item.drawTopStroke(context);
				} 
				
				item.setOnClickListener(this);
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
	protected int getPageTitle() {
		return R.string.bank_deposit_select_account;
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
	 * Method called by base class in onCreateView to determine what string to display in the action bar
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.bank_deposit_title;
	}

	/**
	 * Method used to retrieve menu group this fragment class is associated with.
	 */
	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.DEPOSIT_CHECK_GROUP;
	}

	/**
	 * Method used to retreive the menu section this fragment class is associated with.
	 */
	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.DEPOSIT_NOW_SECTION;
	}
	
	/**
	 * Click handler for when an item in the list displayed is selected by the user.
	 */
	@Override
	public void onClick(final View sender) {
		super.onClick(sender);
		
		if( sender instanceof BankSelectAccountItem) {
			final BankSelectAccountItem item = (BankSelectAccountItem) sender;
		}
	}
	
	/**
	 * Method used to determine whether the user should be prompted with a modal before navigating
	 * to dialer when tapping on Need Help footer.
	 */
	@Override
	public boolean promptUserForNeedHelp(){
		return true;
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
