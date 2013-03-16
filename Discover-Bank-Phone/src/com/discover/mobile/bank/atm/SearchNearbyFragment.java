/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.atm;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.R;

/**
 * Fragment used for searching for locations near the user
 * @author jthornton
 *
 */
public class SearchNearbyFragment extends AtmMapFragment{

	@Override
	public int getLayout() {
		return R.layout.bank_atm_search_nearby;
	}

	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.ATM_LOCATOR_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.FIND_NEARBY_SECTION;
	}

	@Override
	public int getMapFragmentId() {
		return R.id.discover_map_2;
	}

	@Override
	public int getListFragmentId() {
		return R.id.discover_list_2;
	}
}