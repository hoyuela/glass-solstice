package com.discover.mobile.bank.paybills.schedule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discover.mobile.BaseFragment;
import com.discover.mobile.R;
import com.discover.mobile.bank.BankServiceCallFactory;

public class SchedulePaymentLandingPage extends BaseFragment{

	/**
	 * Create the view
	 * @param inflater - inflater to inflate the layout
	 * @param container - container holding the group
	 * @param savedInstanceState - state of the fragment
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.account_summary, null);

		getPayees();


		return view;
	}

	public void getPayees(){
		BankServiceCallFactory.createGetPayeeServiceRequest().submit();
	}


	@Override
	public int getActionBarTitle() {
		return R.string.pay_a_bill_title;
	}

}
