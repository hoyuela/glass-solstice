package com.discover.mobile.navigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.discover.mobile.R;
import com.discover.mobile.RoboSherlockListFragment;
import com.google.inject.Inject;

public class NavigationMenuFragment extends RoboSherlockListFragment {
	
	@Inject
	private NavigationItemAdapter navigationItemAdapter;
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.navigation_menu_list, null);
	}
	
	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		NavigationItem.initializeAdapterWithSections(navigationItemAdapter);
		setListAdapter(navigationItemAdapter);
		
		// TODO show first section
	}
	
	@Override
	public void onListItemClick(final ListView listView, final View clickedView, final int position, final long id) {
		super.onListItemClick(listView, clickedView, position, id);
		
		navigationItemAdapter.getItem(position).onClick(listView);
	}
	
}
