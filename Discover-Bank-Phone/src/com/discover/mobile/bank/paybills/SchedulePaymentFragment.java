package com.discover.mobile.bank.paybills;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discover.mobile.bank.R;
import com.discover.mobile.common.BaseFragment;

public class SchedulePaymentFragment extends BaseFragment{

	/**
	 * Create the view
	 * @param inflater - inflater to inflate the layout
	 * @param container - container holding the group
	 * @param savedInstanceState - state of the fragment
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {

		final View view = inflater.inflate(R.layout.schedule_payment, null);

		return view;
	}

	/**
	 * Set the title in the action bar.
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.pay_a_bill_title;
	}

}