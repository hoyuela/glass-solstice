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

public class BankDepositSelectAmount extends BankDepositBaseFragment {

	private Account account;
	
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
		
		/**Hide top note as it is not needed for this view**/
		final TextView topNote = (TextView)view.findViewById(R.id.top_note_text);
		topNote.setVisibility(View.GONE);
		
		actionButton.setText(R.string.continue_text);

		actionLink.setText(R.string.cancel_text);
		
		final Drawable d = getActivity().getResources().getDrawable(R.drawable.light_gray_bkgrd);
		contentTable.setBackgroundDrawable(d);
		
		return view;
	}
	
	/**
	 * Method called by base class in onCreateView to determine what the title of the page should be.
	 */
	@Override
	protected String getPageTitle() {
		return account.nickname;
	}

	@Override
	protected int getProgressIndicatorStep() {
		return 0;
	}


	@Override
	protected List<RelativeLayout> getRelativeLayoutListContent() {
		final List<RelativeLayout> items = new ArrayList<RelativeLayout>();
		
		items.add( new BankAmountItem(getActivity()) );
		
		return items;
	}

	@Override
	protected void onActionButtonClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onActionLinkClick() {
		// TODO Auto-generated method stub
		
	}

	
}
