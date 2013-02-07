package com.discover.mobile.bank.paybills;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discover.mobile.BaseFragment;
import com.discover.mobile.R;

public class BankSchedulePaymentLandingPage extends BaseFragment{

	/**
	 * Create the view
	 * @param inflater - inflater to inflate the layout
	 * @param container - container holding the group
	 * @param savedInstanceState - state of the fragment
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {

		final View view;
		final boolean isEnrolled = false;
		final boolean isEligible = false;

		//If the user is not eligible then show the no eligible view
		if(!isEligible){
			view = new BankPayeeNotEligibleLayout(getActivity(), null);

			//User is eligible but not enrolled
		} else if(isEligible && !isEnrolled){
			view = new BankPayTerms(getActivity(), null);

			//User is eligible and enrolled
		} else{
			view = new BankSelectPayee(getActivity(), null);
		}

		return view;
	}

	@Override
	public int getActionBarTitle() {
		return R.string.pay_a_bill_title;
	}

}
