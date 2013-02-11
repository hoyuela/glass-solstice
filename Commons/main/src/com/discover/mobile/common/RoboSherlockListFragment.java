package com.discover.mobile.common;

import roboguice.RoboGuice;
import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.app.SherlockListFragment;

public abstract class RoboSherlockListFragment extends SherlockListFragment {
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		RoboGuice.getInjector(getActivity()).injectMembersWithoutViews(this);
	}
	
	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		RoboGuice.getInjector(getActivity()).injectViewMembers(this);
	}
	
}
