package com.discover.mobile.bank.deposit;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.ui.fragments.BankOneButtonFragment;
import com.discover.mobile.bank.ui.table.ViewPagerListItem;
import com.discover.mobile.bank.ui.widgets.BankHeaderProgressIndicator;

/**
 * Base fragment class used for the Check Deposit - work flow. This class is based off of BankOneButton Fragment which uses the layout
 * defined in the res/layout/bank_one_button_layout.xml. In addition to the abstract methods expected to be implemented for BankOneButtonFragment
 * the getProgressIndicatorStep() method is required to be implemented by sub-classes to specify the step in the check deposit work-flow
 * the sub-class is representing.
 * 
 * @author henryoyuela
 *
 */
public abstract class BankDepositBaseFragment extends BankOneButtonFragment {

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		
		/**Setup Progress Indicator to show Payment Details and Payment Scheduled, on step 1, and hide step 2 **/
		BankHeaderProgressIndicator progressIndicator = getProgressIndicator();
		progressIndicator.initialize(getProgressIndicatorStep());
		progressIndicator.setTitle(R.string.bank_deposit_enter_details, R.string.bank_deposit_capture, 
																		R.string.bank_deposit_confirmation);

		return view;
	}

	protected abstract int getProgressIndicatorStep();
	
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
	 * Method used to determine whether the user should be prompted with a modal before navigating
	 * to dialer when tapping on Need Help footer.
	 */
	@Override
	public boolean promptUserForNeedHelp(){
		return true;
	}
	
	@Override
	protected List<ViewPagerListItem> getViewPagerListContent() {
		return null;
	}

}
