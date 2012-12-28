package com.discover.mobile.section.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.BaseFragment;

public class AccountRecentActivityFragment extends BaseFragment {
	
	// TEMP
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		
		final View view = inflater.inflate(R.layout.section_account_home, null);
		final TextView titleView = (TextView) view.findViewById(R.id.title);
		titleView.setText(R.string.sub_section_title_recent_activity);
		return view;
	}
	
	/**
	 * Return the integer value of the string that needs to be displayed in the title
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.recent_activity_title;
	}
	
}
