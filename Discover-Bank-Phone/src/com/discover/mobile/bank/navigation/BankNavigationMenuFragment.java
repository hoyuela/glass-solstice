package com.discover.mobile.bank.navigation;


import java.util.Calendar;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.account.BankAccountSectionInfo;
import com.discover.mobile.bank.account.BankAccountSummaryFragment;
import com.discover.mobile.bank.account.BankOpenAccountFragment;
import com.discover.mobile.bank.atm.BankAtmLocatorInfo;
import com.discover.mobile.bank.customerservice.BankCustomerServiceSectionInfo;
import com.discover.mobile.bank.deposit.BankDepositChecksSectionInfo;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.help.PrivacyTermsType;
import com.discover.mobile.bank.paybills.BankPayBillsSectionInfo;
import com.discover.mobile.bank.transfer.BankTransferMoneySectionInfo;
import com.discover.mobile.common.nav.NavigationItem;
import com.discover.mobile.common.nav.NavigationMenuFragment;
import com.discover.mobile.common.nav.NavigationRootActivity;
import com.discover.mobile.common.nav.section.ComponentInfo;
import com.discover.mobile.common.utils.CommonUtils;
import com.google.common.collect.ImmutableList;

public class BankNavigationMenuFragment extends NavigationMenuFragment {

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		final TextView version = (TextView) getActivity().findViewById(R.id.navigation_version);
		final TextView copy = (TextView) getActivity().findViewById(R.id.navigation_copyright);
		final TextView privacy = (TextView)getActivity().findViewById(R.id.navigation_privacy);
		final Calendar cal = Calendar.getInstance();
		final View footerView = getActivity().getLayoutInflater().inflate(R.layout.list_view_footer, null);

		final String year = String.valueOf(cal.get(Calendar.YEAR));

		version.setText("Version " + CommonUtils.getApplicationVersionNumber());
		copy.setText("\u00a9" + year + " Discover Bank, Member FDIC");
		final ListView lv = getListView();
		lv.setDivider(null);
		lv.setDividerHeight(0);
		lv.addFooterView(footerView, null, false);

		privacy.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(final View v) {
				BankConductor.navigateToPrivacyTerms(PrivacyTermsType.LandingPage);
			}
		});

		/**Underline text for Privacy & Terms*/
		privacy.setPaintFlags(privacy.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

		/**
		 * Initializes the navigation menu
		 */		
		final NavigationRootActivity activity = (NavigationRootActivity) getActivity();
		Fragment homeFragment = activity.getCurrentContentFragment();
		activity.setMenu(this);

		/**Check if there are no fragments already loaded and this is the first time the app is launched **/
		if( homeFragment == null ) {		
			/**Show BankAccountSummaryFragment if user has any accounts otherwise show BankOpenAccountFragment()*/
			homeFragment = BankUser.instance().hasAccounts() ? new BankAccountSummaryFragment() : new BankOpenAccountFragment();
			NavigationItem.initializeAdapterWithSections(navigationItemAdapter, BANK_SECTION_LIST, homeFragment);
		} else {
			NavigationItem.initializeAdapterWithSections(navigationItemAdapter, BANK_SECTION_LIST, null);
		}


		setListAdapter(navigationItemAdapter);
	}

	public static final ImmutableList<ComponentInfo> BANK_SECTION_LIST = ImmutableList.<ComponentInfo>builder()
			//Add Sections below
			.add(new BankAccountSectionInfo())
			.add(new BankTransferMoneySectionInfo())
			.add(new BankDepositChecksSectionInfo())
			.add(new BankPayBillsSectionInfo())
			.add(new BankAtmLocatorInfo())
			.add(new BankCustomerServiceSectionInfo())
			.build();



}
