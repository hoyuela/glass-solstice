package com.discover.mobile.bank.account;

import android.os.Bundle;

import com.discover.mobile.bank.R;

public class ActivityDetailFragment extends DetailFragment {

	@Override
	public void onCreate(final Bundle savedInstaceState) {
		super.onCreate(savedInstaceState);
	}
	
	@Override
	protected int getFragmentLayout() {
		return R.layout.transaction_detail;
	}

}
