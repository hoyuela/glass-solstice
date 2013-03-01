package com.discover.mobile.bank.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.R;
import com.discover.mobile.common.BaseFragment;

/**
 * Fragment used to server as a placeholder for a Fragment that is currently under development
 * for development purposes only.
 * 
 * @author henryoyuela
 *
 */
public class BankUnderDevelopmentFragment extends BaseFragment {


	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.bank_under_development, null);	

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public int getActionBarTitle() {
		return R.string.bank_under_development;
	}

	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.PAY_BILLS_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.PAY_BILLS_SECTION;
	}

}
