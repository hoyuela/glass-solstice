package com.discover.mobile.bank.deposit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.help.HelpMenuListFactory;
import com.discover.mobile.bank.ui.fragments.BankMessageFragment;
import com.discover.mobile.common.help.HelpWidget;

/**
 * Fragment displayed to the user when a Check Deposit times-out.
 * 
 * @author henryoyuela
 *
 */
public class CheckDepositErrorFragment extends BankMessageFragment {
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		
		/**Help widget setup to show faq*/
		final HelpWidget help = (HelpWidget) view.findViewById(R.id.help);
		help.showHelpItems(HelpMenuListFactory.instance().getCheckDepositHelpItems());
		
		return view;
	}
	
	@Override
	public void onClick(final View v) {
		//Nothing to do here
	}

	@Override
	public String getActionButtonText() {
		return null;
	}

	@Override
	public String getPageTitle() {
		return this.getResources().getString(R.string.bank_deposit_error_title);
	}

	@Override
	public String getBodyText() {
		return this.getResources().getString(R.string.bank_deposit_error_body);
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
}
