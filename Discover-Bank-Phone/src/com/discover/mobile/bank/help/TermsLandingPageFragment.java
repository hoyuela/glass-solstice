package com.discover.mobile.bank.help;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.services.customer.Customer;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.help.HelpWidget;

/**
 * Class used to display the Privacy and Terms Landing page.
 * 
 * @author henryoyuela
 *
 */
public class TermsLandingPageFragment extends BaseFragment {

	/**
	 * Setup the view using a default adapter for the list. 
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.bank_privacy_terms_layout, null);
		
		/**Help icon setup*/
		final HelpWidget help = (HelpWidget) view.findViewById(R.id.help);
		help.showHelpItems(HelpMenuListFactory.instance().getAccountHelpItems());
		
		/**Populate menu based on users eligibility of features*/
		final List<String> allValues = new ArrayList<String>(
				Arrays.asList(getResources().getStringArray(R.array.terms_sections)));
		final List<String> values = new ArrayList<String>();
		values.add(allValues.get(0));
		values.add(allValues.get(1));
		
		/**Check if user has logged in already*/
		if( BankUser.instance() != null && BankUser.instance().getCustomerInfo() != null ) {
			final Customer customer = BankUser.instance().getCustomerInfo();
			/**Check if user is eligible for Paybill features*/
			if( customer.getPaymentsEligibility() != null &&
			    customer.getPaymentsEligibility().isEligible() ) {
				values.add(allValues.get(2));
			}
			
			/**Check if user is eligible for Check Deposit features*/
			if(  customer.getDepositsEligibility() != null &&
				 customer.getDepositsEligibility().isEligible() ) {
				values.add(allValues.get(3));
			}
			
			/**Add Google Menu Item*/
			values.add(allValues.get(4));
		} else {
			/**Add Google Menu Item*/
			values.add(allValues.get(4));
		}
		
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), 
				R.layout.single_item_table_cell, android.R.id.text1, values);
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
			navigateTo(itemTitle.getText().toString());
		}
		
	};
	
	private void navigateTo(final String title) {
		final Resources res = getResources();
		
		if(title.equals(res.getString(R.string.bank_terms_privacy_statement)))
			BankConductor.navigateToPrivacyTerms(PrivacyTermsType.MobilePrivacyStatement);
		else if(title.equals(res.getString(R.string.bank_terms_of_use)))
			BankConductor.navigateToPrivacyTerms(PrivacyTermsType.MobileTermsOfUse);
		else if(title.equals(res.getString(R.string.bank_terms_bill_pay)))
			BankConductor.navigateToPrivacyTerms(PrivacyTermsType.BillPayTermsOfUse);
		else if(title.equals(res.getString(R.string.bank_deposit_check)))
			BankConductor.navigateToPrivacyTerms(PrivacyTermsType.DepositTermsOfUse);
		else if(title.equals(res.getString(R.string.bank_terms_google))) 
			BankConductor.navigateToPrivacyTerms(PrivacyTermsType.GoogleTermsOfUse);
	}
		
	@Override
	public int getActionBarTitle() {
		return R.string.bank_terms_privacy_n_terms;
	}

	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.PRIVACY_AND_TERMS_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return -1;
	}

}
