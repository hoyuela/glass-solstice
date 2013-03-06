package com.discover.mobile.bank.deposit;

import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.bank.BankNavigator;
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
				BankNavigator.navigateToCheckDepositWorkFlow();
			}
		};
	}
	
}
