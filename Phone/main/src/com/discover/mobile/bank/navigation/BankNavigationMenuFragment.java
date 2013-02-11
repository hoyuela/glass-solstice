package com.discover.mobile.bank.navigation;


import android.os.Bundle;

import com.discover.mobile.bank.account.BankAccountSectionInfo;
import com.discover.mobile.bank.atm.BankAtmLocatorInfo;
import com.discover.mobile.bank.customerservice.BankCustomerServiceSectionInfo;
import com.discover.mobile.bank.deposit.BankDepositChecksSectionInfo;
import com.discover.mobile.bank.paybills.BankPayBillsSectionInfo;
import com.discover.mobile.bank.transfer.BankTransferMoneySectionInfo;
import com.discover.mobile.common.nav.NavigationItem;
import com.discover.mobile.common.nav.NavigationMenuFragment;
import com.discover.mobile.common.nav.section.ComponentInfo;
import com.google.common.collect.ImmutableList;

public class BankNavigationMenuFragment extends NavigationMenuFragment {

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);


		/**
		 * Initializes the navigation menu
		 */		
		NavigationItem.initializeAdapterWithSections(navigationItemAdapter, BANK_SECTION_LIST, this);
		setListAdapter(navigationItemAdapter);
	}
	
	public static final ImmutableList<ComponentInfo> BANK_SECTION_LIST = ImmutableList.<ComponentInfo>builder()
			//Add Sections below
			.add(new BankAccountSectionInfo())
			.add(new BankTransferMoneySectionInfo())
			.add(new BankPayBillsSectionInfo())
			.add(new BankDepositChecksSectionInfo())
			.add(new BankCustomerServiceSectionInfo())
			.add(new BankAtmLocatorInfo())
			.build();



}
