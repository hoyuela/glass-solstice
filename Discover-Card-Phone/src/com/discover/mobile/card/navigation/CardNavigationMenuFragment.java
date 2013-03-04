package com.discover.mobile.card.navigation;

import android.os.Bundle;

import com.discover.mobile.card.account.AccountSectionInfo;
import com.discover.mobile.card.home.HomeSectionInfo;
import com.discover.mobile.card.home.HomeSummaryFragment;
import com.discover.mobile.card.profile.ProfileAndSettingsSectionInfo;
import com.discover.mobile.common.facade.FacadeFactory;
import com.discover.mobile.common.nav.NavigationItem;
import com.discover.mobile.common.nav.NavigationMenuFragment;
import com.discover.mobile.common.nav.NavigationRootActivity;
import com.discover.mobile.common.nav.section.ComponentInfo;
import com.google.common.collect.ImmutableList;

public class CardNavigationMenuFragment extends NavigationMenuFragment {

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		final NavigationRootActivity activity = (NavigationRootActivity) getActivity();
		activity.setMenu(this);

		/**Check if there are no fragments already loaded and this is the first time the app is launched **/
		if( activity.getCurrentContentFragment() == null ) {	
			NavigationItem.initializeAdapterWithSections(navigationItemAdapter, CARD_SECTION_LIST,new HomeSummaryFragment());
		} else {
			NavigationItem.initializeAdapterWithSections(navigationItemAdapter, CARD_SECTION_LIST, null);
		}

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
			.add(FacadeFactory.getCustomerServiceFacade().getCustomerServiceSection())
			.build();


}
