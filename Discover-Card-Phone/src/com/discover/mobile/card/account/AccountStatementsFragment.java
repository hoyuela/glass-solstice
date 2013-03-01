package com.discover.mobile.card.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.discover.mobile.card.CardMenuItemLocationIndex;
import com.discover.mobile.card.R;
import com.discover.mobile.common.BaseFragment;

public class AccountStatementsFragment extends BaseFragment {

	// TEMP
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {

		final View view = inflater.inflate(R.layout.section_account_home, null);
		final TextView titleView = (TextView) view.findViewById(R.id.title);
		titleView.setText(R.string.sub_section_title_statements);
		return view;
	}

	/**
	 * Return the integer value of the string that needs to be displayed in the title
	 */
	@Override
	public int getActionBarTitle() {
		//FIXME: Put the correct title here
		return R.string.bank_account_summary;
	}

	@Override
	public int getGroupMenuLocation() {
		return CardMenuItemLocationIndex.ACCOUNT_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return CardMenuItemLocationIndex.ACCOUNT_SUMMARY_SECTION;
	}

}
