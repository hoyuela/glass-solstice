package com.discover.mobile.bank.deposit;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.account.BankAccountSummaryFragment;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;

/**
 * Fragment used to display the Check Deposit - Confirmation Page after submitting a check deposit
 * successfully. The information displayed to the user is based on the user entered information in
 * the check deposit review page and what is returned from the server after executing the Bank Web Service
 * Create a Deposit. This class inherits from BankDepositBaseFragment which uses the layout defined 
 * in res/layout/bank_one_button_layout.xml.
 * 
 * @author henryoyuela
 *
 */
public class BankDepositConfirmFragment extends BankDepositBaseFragment {

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		
		/**Hide controls that are not needed*/
		noteTitle.setVisibility(View.GONE);
		noteTextMsg.setVisibility(View.GONE);
		feedbackLink.setVisibility(View.GONE);
		
		/**Hide top note as it is not needed for this view**/
		final TextView topNote = (TextView)view.findViewById(R.id.top_note_text);
		topNote.setVisibility(View.GONE);
		
		/**Show Action Button text in single button on screen*/
		actionButton.setText(R.string.bank_deposit_received_actionbutton);
		
		/**Show Link text in link on screen*/
		actionLink.setText(R.string.bank_deposit_received_actionlink);
			
		return view;
	}
	
	@Override
	public boolean isBackPressDisabled() {
		return true;
	}
	
	@Override
	public void onBackPressed() {
		//this is not required for this screen
	}

	/**
	 * Used to specify the step in the bread crumb displayed above this page.
	 */
	@Override
	protected int getProgressIndicatorStep() {
		return 3;
	}

	@Override
	protected List<RelativeLayout> getRelativeLayoutListContent() {
		return BankDepositListGenerator.getDepositConfirmationList(getActivity(), null);
	}

	@Override
	protected void onActionButtonClick() {
		
	}

	@Override
	protected void onActionLinkClick() {
		final BankNavigationRootActivity activity = (BankNavigationRootActivity)this.getActivity();
		activity.popTillFragment(BankAccountSummaryFragment.class);
	}
	
	/**
	 * Returns a string that is displayed as the title on the fragment layout.
	 */
	@Override
	protected String getPageTitle() {		
		return this.getActivity().getResources().getString(R.string.bank_deposit_received_title);
	}


}
