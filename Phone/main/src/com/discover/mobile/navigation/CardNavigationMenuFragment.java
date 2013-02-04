package com.discover.mobile.navigation;

import android.os.Bundle;

import com.discover.mobile.section.Sections;

public class CardNavigationMenuFragment extends NavigationMenuFragment {
	
	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		NavigationItem.initializeAdapterWithSections(navigationItemAdapter, Sections.CARD_SECTION_LIST);
		setListAdapter(navigationItemAdapter);
	}
	
	
	
}
