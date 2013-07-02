/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.atm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.R;

/**
 * Fragment used for searching a location for ATMs
 * @author jthornton
 *
 */
public class SearchByLocationFragment extends AtmMapFragment{

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState){
		final View v = super.onCreateView(inflater, container, savedInstanceState);
		super.setLocationStatus(NOT_USING_LOCATION);
		
		if (AtmTapAndHoldCoachOverlay.shouldShowCoachOverlay()) {
			this.setCoachOverlay((AtmTapAndHoldCoachOverlay)v.findViewById(R.id.tap_and_hold_coach));
			this.getCoachOverlay().showCoach();
		}
		
		return v;
	}

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
}
