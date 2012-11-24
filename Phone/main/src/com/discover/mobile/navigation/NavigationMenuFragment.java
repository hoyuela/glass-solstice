package com.discover.mobile.navigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discover.mobile.R;
import com.discover.mobile.RoboSherlockListFragment;

public class NavigationMenuFragment extends RoboSherlockListFragment {
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.navigation_menu_list, null);
	}
	
	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		final NavigationItemAdapter adapter = new NavigationItemAdapter(getActivity());
		setListAdapter(adapter);
	}
	
}
