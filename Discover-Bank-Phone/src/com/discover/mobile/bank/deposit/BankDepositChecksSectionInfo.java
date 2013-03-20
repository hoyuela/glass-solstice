package com.discover.mobile.bank.deposit;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.nav.section.ClickComponentInfo;
import com.discover.mobile.common.nav.section.GroupComponentInfo;

public final class BankDepositChecksSectionInfo extends GroupComponentInfo {
	/**
	 * Used for printing into Logs into Android Logcat
	 */
	protected static final String TAG = "CheckDeposit";


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
				final Activity activity = DiscoverActivityManager.getActiveActivity();

				/**Verify that the user is logged in and the BankNavigationRootActivity is the active activity*/
				if( activity != null && activity instanceof BankNavigationRootActivity ) {
					final BankNavigationRootActivity navActivity = (BankNavigationRootActivity) activity;
				
					/**Check if user is already in the Check Deposit work-flow*/
					if( navActivity.getCurrentContentFragment().getGroupMenuLocation()  != BankMenuItemLocationIndex.DEPOSIT_CHECK_GROUP) {
						/**Navigate the user back to the home fragment before navigating to the check deposit work-flow*/
						BankConductor.navigateToHomePage();
						
						/**Navigates to either to Check Deposit - Select Account Page or Check Deposit - Accept Terms page*/
						BankConductor.navigateToCheckDepositWorkFlow(null, BankDepositWorkFlowStep.SelectAccount);
					} else {
						
						if( Log.isLoggable(TAG, Log.WARN)) {
							Log.w(TAG,"User is already in the check deposit work-flow");
						}
						
						navActivity.hideSlidingMenuIfVisible();
					}
				}
			}
		};
	}
	
}
