package com.discover.mobile.section.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.RoboSherlockFragment;

public class AccountSearchTransactionFragment extends RoboSherlockFragment {
	
	// TEMP
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		
		final View view = inflater.inflate(R.layout.section_account_home, null);
		final TextView titleView = (TextView) view.findViewById(R.id.title);
		titleView.setText(R.string.sub_section_title_search_transaction);
		return view;
	}
	
	/**
	 * Return the integer value of the string that needs to be displayed in the title
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.search_transactions_title;
	}
	
}
