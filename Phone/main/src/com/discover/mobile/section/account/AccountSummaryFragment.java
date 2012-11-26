package com.discover.mobile.section.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discover.mobile.R;
import com.discover.mobile.RoboSherlockFragment;

public class AccountSummaryFragment extends RoboSherlockFragment {
	
	// TEMP
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		
		final View view = inflater.inflate(R.layout.account_summary_landing, null);
		return view;
	}
	
}
