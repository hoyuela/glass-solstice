package com.discover.mobile.navigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.discover.mobile.R;

public class HomeFragment extends SherlockFragment {
	
	// TODO persistent state
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.logged_in_landing, null);
		
		// TEMP
		view.setPadding(5, 5, 5, 0);
		
		return view;
	}
	
}
