package com.discover.mobile.bank.deposit;

import android.view.View;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.bank.ui.fragments.BankMessageFragment;

/**
 * Fragment used to display a duplicate check error to the user. The user will be allowed to navigate
 * to deposit another check from this fragment.
 * 
 * @author henryoyuela
 *
 */
public class DuplicateCheckErrorFragment extends BankMessageFragment {

	@Override
	public void onClick(final View v) {
		final BankNavigationRootActivity activity = (BankNavigationRootActivity)this.getActivity();
		activity.popTillFragment(BankDepositSelectAccount.class);
	}
	
	@Override
	public String getActionButtonText() {
		return this.getResources().getString(R.string.bank_deposit_another);
	}	

	@Override
	public String getPageTitle() {
		return this.getResources().getString(R.string.bank_deposit_duplicate_check_title);
	}

	@Override
	public String getBodyText() {
		return this.getResources().getString(R.string.bank_deposit_duplicate_check_body);
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
