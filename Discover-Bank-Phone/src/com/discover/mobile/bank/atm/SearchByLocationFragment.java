/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.atm;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.R;

/**
 * Fragment used for searching a location for ATMs
 * @author jthornton
 *
 */
public class SearchByLocationFragment extends AtmMapFragment{

	@Override
	public int getLayout() {
		return R.layout.bank_atm_map;
	}

	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.ATM_LOCATOR_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.SEARCH_BY_LOCATION;
	}

	@Override
	public int getMapFragmentId() {
		return R.id.discover_map;
	}

}
