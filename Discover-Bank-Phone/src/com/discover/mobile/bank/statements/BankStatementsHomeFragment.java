package com.discover.mobile.bank.statements;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.paybills.SimpleChooseListItem;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.account.AccountList;
import com.discover.mobile.bank.ui.fragments.BankOneButtonFragment;
import com.discover.mobile.bank.ui.table.ViewPagerListItem;
/*
 * This class displays a list of accounts available to the user.  Upon clicking an account,
 * the user will be navigated to a screen that displays the available statements for the selected
 * account.
 */
public class BankStatementsHomeFragment extends BankOneButtonFragment implements OnClickListener {

	/*
	 * Fragment Life cycle methods
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		//hide the action button and action link as they are not needed
		getActionButton().setVisibility(View.GONE);
		getActionLink().setVisibility(View.GONE);
		getPageTitleView().setText(R.string.statements_home_page_title);
		getPageTitleView().setVisibility(View.VISIBLE);
		return view;
	}
	/*
	 * End of Life Cycle Methods
	 * Following methods are inherited from BankOneButtonFragment
	 */
	@Override
	public void onBackPressed() {}

	@Override
	protected List<ViewPagerListItem> getViewPagerListContent() {
		return null;
	}
	/*
	 * Generate a List of the accounts so user can select which one
	 * they would like to view statements
	 */
	@Override
	protected List<RelativeLayout> getRelativeLayoutListContent() {
		List<RelativeLayout> list = new ArrayList<RelativeLayout>();
		//Get the stored accounts and create a list from them
		AccountList stashedAccounts = BankUser.instance().getAccounts();
		for (Account a : stashedAccounts.accounts) {
			SimpleChooseListItem aView = new SimpleChooseListItem(getActivity(), null,
					a, a.getDottedFormattedAccountNumber());
			aView.setOnClickListener(this);
			//add the listener
			list.add(aView);
		}
		return list;
	}
	
	/*
	 * Function not needed since action button is hidden
	 */
	@Override
	protected void onActionButtonClick() {}

	/*
	 * Function not needed since action link is hidden 
	 */
	@Override
	protected void onActionLinkClick() {}

	@Override
	public int getActionBarTitle() {
		return R.string.statements_action_title;
	}

	@Override
	public int getGroupMenuLocation() {
		//View statements is the first group
		return 0;
	}

	@Override
	public int getSectionMenuLocation() {
		//second in the group list
		return 2;
	}
	/*
	 * End of BankOneButtonFragment Methods
	 */
	
	/*
	 * On click of a list item, navigate to the account statements landing page.
	*/
	@Override
	public void onClick(View v) {
		SimpleChooseListItem listItem = (SimpleChooseListItem) v;
		Account account = (Account)listItem.getItem();
		//Navigate to the account landing page
		BankConductor.navigateToAccountStatements(account);
	}
}
