package com.discover.mobile.bank.help;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.services.customer.Customer;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.help.HelpWidget;
import com.discover.mobile.common.utils.CommonUtils;

/**
 * Class used to display the Privacy and Terms Landing page.
 * 
 * @author henryoyuela
 *
 */
public class TermsLandingPageFragment extends BaseFragment {

	/* STRING ARRAY INDEXES */
	private static final int INDEX_PRIVACY = 0;
	private static final int INDEX_USE = 1;
	private static final int INDEX_BILL_PAY = 2;
	private static final int INDEX_DEPOSIT = 3;
	private static final int INDEX_GOOGLE = 4;
	
	/**
	 * Setup the view using a default adapter for the list. 
	 */
	@SuppressLint("NewApi")
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.bank_privacy_terms_layout, null);
		
		/**Help icon setup*/
		final HelpWidget help = (HelpWidget) view.findViewById(R.id.help);
		if( !Globals.isLoggedIn() ) {
			help.showHelpItems(HelpMenuListFactory.instance().getLoggedOutHelpItems());
		} else {
			help.showHelpItems(HelpMenuListFactory.instance().getAccountHelpItems());
		}
		
		// Populate menu based on users eligibility of features
		final List<String> values = getValues();
		final LinearLayout list = (LinearLayout) view.findViewById(R.id.terms_list);
		
		// Build the linear layout table.
		for (int i = 0; i < values.size(); ++i) {

			if (i > 0) {
				insertDividerLine(list);
			}

			// Set the text of the section
			final RelativeLayout item = (RelativeLayout) inflater.inflate(R.layout.single_item_table_cell, null);
			final TextView label = (TextView) item.findViewById(android.R.id.text1);
			label.setText(values.get(i));
			item.setOnClickListener(getListClickListener(item));

			// Add the constructed list item to the table.
			list.addView(item);
		}

		// Disable hardware acceleration for the UI so that the dotted line gets drawn correctly.
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		} else {
			// Tiled background is often broken for older devices
			CommonUtils.fixBackgroundRepeat(view.findViewById(R.id.terms_layout));
		}
		
		return view;
	}
	
	/**
	 * A click listener that when onClick is called, navigates to a specific FAQ section based
	 * on the title of the listItem.
	 * @param listItem a listItem that is used to indicate a FAQ section.
	 * @return an OnClickListener that will navigate to a FAQ section.
	 */
	private OnClickListener getListClickListener(final RelativeLayout listItem) {
		return new OnClickListener() {
			@Override
			public void onClick(final View v) {
				final TextView itemTitle = (TextView)listItem.findViewById(android.R.id.text1);
				navigateTo(itemTitle.getText().toString());
			}
		};
	}
	
	/** @return a List of String values to be displayed on this instance of the Fragment 
	 * 	based on user credentials. */
	private List<String> getValues() {
		final List<String> allValues = new ArrayList<String>(
				Arrays.asList(getResources().getStringArray(R.array.terms_sections)));
		final List<String> values = new ArrayList<String>();
		values.add(allValues.get(INDEX_PRIVACY));
		values.add(allValues.get(INDEX_USE));
		
		/**Check if user has logged in already*/
		if( BankUser.instance() != null && BankUser.instance().getCustomerInfo() != null ) {
			final Customer customer = BankUser.instance().getCustomerInfo();
			/**Check if user is eligible for Pay bills features*/
			if( customer.getPaymentsEligibility() != null &&
			    customer.getPaymentsEligibility().isEligible() ) {
				values.add(allValues.get(INDEX_BILL_PAY));
			}
			
			/**Check if user is eligible for Check Deposit features*/
			if(  customer.getDepositsEligibility() != null &&
				 customer.getDepositsEligibility().isEligible() ) {
				values.add(allValues.get(INDEX_DEPOSIT));
			}
			
			/**Add Google Menu Item*/
			values.add(allValues.get(INDEX_GOOGLE));
		} else {
			/**Add Google Menu Item*/
			values.add(allValues.get(INDEX_GOOGLE));
		}
		
		return values;
	}
	
	/**
	 * Place a divider line at the next available position in the linear layout.
	 * @param view a linear layout to add a divider line to.
	 */
	private void insertDividerLine(final LinearLayout view) {
		final View divider = new View(getActivity(), null);
		divider.setBackgroundResource(R.drawable.table_dotted_line);
		final LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
		
		view.addView(divider, params);
	}
	
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
