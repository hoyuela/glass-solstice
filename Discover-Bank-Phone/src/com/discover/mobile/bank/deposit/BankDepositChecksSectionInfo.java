package com.discover.mobile.bank.deposit;

import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.bank.BankNavigator;
import com.discover.mobile.bank.BankUser;
import com.discover.mobile.bank.R;
import com.discover.mobile.common.nav.section.ClickComponentInfo;
import com.discover.mobile.common.nav.section.GroupComponentInfo;

public final class BankDepositChecksSectionInfo extends GroupComponentInfo {
	
	public BankDepositChecksSectionInfo() {
		super(R.string.section_title_deposit_checks,
				new ClickComponentInfo(R.string.sub_section_title_deposit_a_check, getCheckDepositLandingClickListener()));
	}


	/**
	 * Click listener for the review payments menu item.  Makes the service call to the initial set 
	 * of data.
	 * @return the click listener
	 */
	public static OnClickListener getCheckDepositLandingClickListener(){
		return new OnClickListener(){
			@Override
			public void onClick(final View v) {
				if(!isEligible()){
					//TODO: Need to figure out to see where to go if not eligible
				} else if(isEligible() && !isEnrolled()){
					BankNavigator.navigateToDepositTerms();
				} else{
					//Check if User has accounts
					if( BankUser.instance().hasAccounts() ) {
						//Check if User has more than one account
						if( BankUser.instance().getAccounts().accounts.size() > 1 ) {
							//Navigate to Select account
						} else {
							//Navigate to Set 
						}
					}
				}
			}
		};
	}

	/**
	 * Method call to see if a uses is eligible for Check Deposit
	 * @return if a user is eligible for payments
	 */
	protected static boolean isEligible(){
		return BankUser.instance().getCustomerInfo().isPaymentsEligibility();
	}

	/**
	 * Method call to see if a users is enrolled in Check Deposit
	 * @return if a user is enrolled in payments
	 */
	protected static boolean isEnrolled(){
		return BankUser.instance().getCustomerInfo().isPaymentsEnrolled();
	}
}
