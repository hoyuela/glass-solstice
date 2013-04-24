package com.discover.mobile.bank.atm;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.bank.R;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.nav.NavigationRootActivity;
import com.discover.mobile.common.nav.section.ClickComponentInfo;
import com.discover.mobile.common.nav.section.GroupComponentInfo;

public final class BankAtmLocatorInfo extends GroupComponentInfo {

	public BankAtmLocatorInfo() {
		super(R.string.section_title_atm_locator,
				new ClickComponentInfo(R.string.sub_section_title_find_nearby, getNearbyListener()),
				new ClickComponentInfo(R.string.sub_section_title_search_location, getLocationListener()));
	}

	public static OnClickListener getNearbyListener(){
		return new OnClickListener(){

			@Override
			public void onClick(final View v) {
				final NavigationRootActivity activity = (NavigationRootActivity)DiscoverActivityManager.getActiveActivity();
				if(!(activity.getCurrentContentFragment() instanceof SearchNearbyFragment)){
					final SearchNearbyFragment fragment = new SearchNearbyFragment();
					/**Provide a bundle in arguments to handle storing its state on rotation*/
					fragment.setArguments(new Bundle());
					activity.makeFragmentVisible(fragment);
				}else{
					activity.getSlidingMenu().toggle();
				}
			}
		};
	}

	public static OnClickListener getLocationListener(){
		return new OnClickListener(){

			@Override
			public void onClick(final View v) {
				final NavigationRootActivity activity = (NavigationRootActivity)DiscoverActivityManager.getActiveActivity();
				if(!(activity.getCurrentContentFragment() instanceof SearchByLocationFragment)){
					final SearchByLocationFragment fragment = new SearchByLocationFragment();
					/**Provide a bundle in arguments to handle storing its state on rotation*/
					fragment.setArguments(new Bundle());
					activity.makeFragmentVisible(fragment);
				}else{
					activity.getSlidingMenu().toggle();
				}
			}
		};
	}

}
