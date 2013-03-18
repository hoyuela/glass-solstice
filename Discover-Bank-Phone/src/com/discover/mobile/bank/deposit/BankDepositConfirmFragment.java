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
