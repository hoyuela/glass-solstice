package com.discover.mobile.card.navigation;

import android.os.Bundle;

import com.discover.mobile.card.account.AccountSectionInfo;
import com.discover.mobile.card.home.HomeSectionInfo;
import com.discover.mobile.card.profile.ProfileAndSettingsSectionInfo;
import com.discover.mobile.common.delegates.DelegateFactory;
import com.discover.mobile.common.nav.NavigationItem;
import com.discover.mobile.common.nav.NavigationMenuFragment;
import com.discover.mobile.common.nav.section.ComponentInfo;
import com.google.common.collect.ImmutableList;

public class CardNavigationMenuFragment extends NavigationMenuFragment {
	
	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		NavigationItem.initializeAdapterWithSections(navigationItemAdapter, CARD_SECTION_LIST,this);
		setListAdapter(navigationItemAdapter);
	}
	
	/**
	 * Immutable list showing all the top level sections that are displayed in the sliding nav menu
	 */
	public static final ImmutableList<ComponentInfo> CARD_SECTION_LIST = ImmutableList.<ComponentInfo>builder()
			//Add Sections below
			.add(new HomeSectionInfo())
			.add(new AccountSectionInfo())
			.add(new ProfileAndSettingsSectionInfo())
			.add(DelegateFactory.getCustomerServiceDelegate().getCustomerServiceSection())
			.build();
	
	
}
