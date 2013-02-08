package com.discover.mobile.bank.navigation;

import android.os.Bundle;

import com.discover.mobile.navigation.NavigationItem;
import com.discover.mobile.navigation.NavigationMenuFragment;
import com.discover.mobile.section.Sections;

public class BankNavigationMenuFragment extends NavigationMenuFragment {

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);


		/**
		 * Initializes the navigation menu
		 */
		NavigationItem.initializeAdapterWithSections(navigationItemAdapter, Sections.BANK_SECTION_LIST);
		setListAdapter(navigationItemAdapter);
	}



}
