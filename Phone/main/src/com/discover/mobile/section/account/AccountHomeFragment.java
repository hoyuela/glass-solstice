package com.discover.mobile.section.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discover.mobile.R;
import com.discover.mobile.RoboSherlockFragment;

public class AccountHomeFragment extends RoboSherlockFragment {
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.account_home, null);
	}
	
}
