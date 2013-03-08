package com.discover.mobile.bank.deposit;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.account.Account;

/**
 * Fragment used to display the Check Deposit - Select Amount page. This is the second step in the
 * Check Deposit work-flow. Displays a an amount text where the user can specify the dollar amount for the check 
 * being deposited. Supports inline error handling as well using the BankAmountItem class.
 * 
 * @author henryoyuela
 *
 */
public class BankDepositSelectAmount extends BankDepositBaseFragment {
	/**
	 * Reference to Account selected by the user in step 1 of Check Deposit workflow via the Select Account page
	 * and passed to this fragment via a bundle. 
	 */
	private Account account;
	/**
	 * Reference to view that will contains the amount field value which validates user entries.
	 */
	private BankAmountItem amountItem;
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		
		final Bundle bundle = this.getArguments();
		if( null != bundle &&  null != bundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM)) {
			account = (Account)bundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM);
		}
		
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		
		/**Hide controls that are not needed*/
		noteTitle.setVisibility(View.GONE);
		noteTextMsg.setVisibility(View.GONE);
		actionLink.setVisibility(View.GONE);
		feedbackLink.setVisibility(View.GONE);
		
		/**Hide top note as it is not needed for this view**/
		final TextView topNote = (TextView)view.findViewById(R.id.top_note_text);
		topNote.setVisibility(View.GONE);
		
		/**Show "Continue" text in single button on screen*/
		actionButton.setText(R.string.continue_text);

		final Drawable d = getActivity().getResources().getDrawable(R.drawable.light_gray_bkgrd);
		contentTable.setBackgroundDrawable(d);
		
		return view;
	}
	
	/**
	 * Method called by base class in onCreateView to determine what the title of the page should be.
	 */
	@Override
	protected String getPageTitle() {
		return account.getDottedFormattedAccountNumber();
	}

	/**
	 * Used to determine the step in the bread-crumb.
	 * 
	 * @return Returns 0 to specify to base class to show indicator in step 1 of bread-crumb.
	 */
	@Override
	protected int getProgressIndicatorStep() {
		return 0;
	}


	/**
	 * Only one item is displayed for this page which is a BankAmountItem for the user to enter
	 * a dollar amount for the next step in the work-flow.
	 */
	@Override
	protected List<RelativeLayout> getRelativeLayoutListContent() {
		final List<RelativeLayout> items = new ArrayList<RelativeLayout>();
		
		items.add( new BankAmountItem(getActivity()) );
		
		return items;
	}

	@Override
	protected void onActionButtonClick() {
		
		
	}

	@Override
	protected void onActionLinkClick() {
		//this is not used for this screen
	}

	@Override
	public void onBackPressed() {
		//this is not required for this screen
		
	}
}
