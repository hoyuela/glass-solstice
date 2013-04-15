package com.discover.mobile.bank.help;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.common.BaseFragment;
/**
 * This is the landing page for the FAQ section.
 * This page displays all FAQ categories for a Bank user.
 * 
 * @author scottseward
 *
 */
public class FAQLandingPageFragment extends BaseFragment {

	/**
	 * Setup the view using a default adapter for the list. 
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.faq_landing_page, null);
		
		final String[] values = getResources().getStringArray(R.array.faq_sections);
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.single_item_table_cell, android.R.id.text1, values);
		final ListView list = (ListView)view.findViewById(android.R.id.list);
		list.setAdapter(adapter);
		list.setOnItemClickListener(itemClickListener);
		return view;
	}

	private final OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(final AdapterView<?> arg0, final View clickedView, final int position,
				final long id) {
			final TextView itemTitle = (TextView)clickedView.findViewById(android.R.id.text1);
			decideWhereToNavigateFromSectionTitle(itemTitle.getText().toString());
		}
		
	};
	
	private void decideWhereToNavigateFromSectionTitle(final String title) {
		final Resources res = getResources();
		
		if(title.equals(res.getString(R.string.general)))
			BankConductor.navigateToFAQDetail(BankExtraKeys.GENERAL_FAQ);
		else if(title.equals(res.getString(R.string.online_bill_pay)))
			BankConductor.navigateToFAQDetail(BankExtraKeys.BILL_PAY_FAQ);
		else if(title.equals(res.getString(R.string.deposit_a_check)))
			BankConductor.navigateToFAQDetail(BankExtraKeys.CHECK_DEPOSIT_FAQ);
		else if(title.equals(res.getString(R.string.atm_locator_single_line)))
			BankConductor.navigateToFAQDetail(BankExtraKeys.ATM_LOCATOR_FAQ);
	
	}
	
	@Override
	public int getActionBarTitle() {
		return R.string.faq_title;
	}

	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.CUSTOMER_SERVICE_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.FREQUENTLY_ASKED_QUESTIONS;
	}
}
