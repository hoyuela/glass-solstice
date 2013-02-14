package com.discover.mobile.bank.paybills;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discover.mobile.bank.R;
import com.discover.mobile.common.BaseFragment;

/**
 * ScrollView that will hold the not eligible for payments view.
 * This just holds static context.
 * 
 * @author jthornton
 *
 */
public class BankPayeeNotEligibleFragment extends BaseFragment{

	/**
	 * Create the view
	 * @param inflater - inflater to inflate the layout
	 * @param container - container holding the group
	 * @param savedInstanceState - state of the fragment
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState){
		return inflater.inflate(R.layout.payee_no_eligible, null);

	}

	/**
	 * Set the title in the action bar.
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.pay_a_bill_title;
	}
}