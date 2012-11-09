package com.discover.mobile.navigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.SherlockListFragment;
import com.discover.mobile.R;

public class NavigationMenuFragment extends SherlockListFragment {
	
	// TODO see if this can be done by just overridding the padding on the default ListView
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.navigation_menu_list, null);
	}
	
	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// TEMP
		final ArrayAdapter<String> tempAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1,
				android.R.id.text1,
				new String[] { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" });
		setListAdapter(tempAdapter);
	}
	
}
