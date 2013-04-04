package com.discover.mobile.bank.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.ui.fragments.DetailFragment;
/**
 * A fragment that shows a loading spinner.
 * 
 * @author scottseward
 *
 */
public class SpinnerFragment extends DetailFragment {

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, 
			final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.spinner_layout, null);
		
		return view;
	}

	@Override
	protected int getFragmentLayout() {
		return R.layout.spinner_layout;
	}

	@Override
	protected void setupFragmentLayout(final View fragmentView) {
		
	}

}
