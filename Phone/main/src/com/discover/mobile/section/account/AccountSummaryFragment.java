package com.discover.mobile.section.account;

import roboguice.inject.InjectView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.discover.mobile.R;
import com.discover.mobile.RoboSherlockFragment;

public class AccountSummaryFragment extends RoboSherlockFragment {
	
	@InjectView(R.id.account_summary_items)
	private ListView accountSummaryList;
	
	// TEMP
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		
		final View view = inflater.inflate(R.layout.account_summary_landing, null);
		return view;
	}
	
}
