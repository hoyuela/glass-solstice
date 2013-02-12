package com.discover.mobile.bank.paybills;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discover.mobile.bank.BankNavigator;
import com.discover.mobile.bank.BankServiceCallFactory;
import com.discover.mobile.bank.BankUser;
import com.discover.mobile.bank.R;
import com.discover.mobile.common.BaseFragment;

/**
 * Landing page for when the user clicks on the pay bills in the navigation.  Based on the users payment eligibility
 * this will display one of three views.  If the user in not eligible it will show the pay bills landing page.
 * If the user is eligible, but not enrolled it will show the terms and conditions screen.  If the user is not enrolled
 * and eligible it will get the list of payees available for the customer and display them in a list so that the
 * payee can be selected.
 * 
 * @author jthornton
 *
 */
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

		final View view = inflater.inflate(R.layout.payee_no_eligible, null);


		/**
		 * This will eventually be moved out of this class.  This class will also need to become a fragment based
		 * on the new menu design.
		 */
		final boolean isEligible = BankUser.instance().getCustomerInfo().getPaymentsEligibility();
		final boolean isEnrolled = BankUser.instance().getCustomerInfo().getPaymentsEnrolled();

		if(!isEligible){
			BankNavigator.navigateToPayBillsLanding();
		} else if(isEligible && !isEnrolled){
			BankNavigator.navigateToPayBillsTerms(null);
		} else{
			getPayees();
		}

		/**
		 * End removal stuff
		 */

		return view;
	}


	/**
	 * Starts the service call to get all the payees for the customer
	 */
	public void getPayees(){
		BankServiceCallFactory.createGetPayeeServiceRequest().submit();
	}

	/**
	 * Set the title in the action bar.
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.pay_a_bill_title;
	}

}