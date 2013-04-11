package com.discover.mobile.bank.transfer;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.nav.section.ClickComponentInfo;
import com.discover.mobile.common.nav.section.GroupComponentInfo;
/**
 * This is the class which holds the information related to the entry in the sliding menu for Transfer Money.
 * @author ajleeds, scottseward
 *
 */
public final class BankTransferMoneySectionInfo extends GroupComponentInfo {

	public BankTransferMoneySectionInfo() {
		super(R.string.section_title_transfer_money, 
				new ClickComponentInfo(R.string.section_title_transfer_money, getTransferFundsLandingClickListener()),  
				new ClickComponentInfo(R.string.sub_section_title_transfer_history,true, externalLink(BankUrlManager.getOpenAccountUrl())), 
				new ClickComponentInfo(R.string.sub_section_title_manage_external_accounts,true,externalLink(BankUrlManager.getOpenAccountUrl())));
	}

	private static OnClickListener externalLink(final String url){

		return new OnClickListener() {

			@Override
			public void onClick(final View v) {
				BankConductor.navigateToBrowser(url);
			}
		};
	}

	/**
	 * Returns a click listener that is responsible for navigating the user to the transfer money workflow.
	 * @return
	 */
	private static OnClickListener getTransferFundsLandingClickListener() {
		return new OnClickListener() {

			@Override
			public void onClick(final View v) {
				Activity activity = DiscoverActivityManager.getActiveActivity();
				final boolean isLoggedIn = (activity != null);
				final boolean isBankNavigationRootActivityActive = activity instanceof BankNavigationRootActivity;

				/**Verify that the user is logged in and the BankNavigationRootActivity is the active activity*/
				if( isLoggedIn && isBankNavigationRootActivityActive) {
					final BankNavigationRootActivity navActivity = (BankNavigationRootActivity) activity;
					activity = null;

					/**Check if user is already in the Transfer Money work-flow*/
					if( navActivity.getCurrentContentFragment().getGroupMenuLocation()  != BankMenuItemLocationIndex.TRANSFER_MONEY_GROUP) {
						BankConductor.navigateToHomePage();
						BankConductor.navigateToTransferMoneyLandingPage(null);
					} else {
						final String TAG = BankTransferMoneySectionInfo.class.getSimpleName();
						
						//Log.isLoggable will throw an exception if it is given a tag of length > 23, 
						//so we need to truncate it.
						final String limitedTAG = TAG.substring(0, Math.min(TAG.length(), 23));

						if( Log.isLoggable(limitedTAG, Log.WARN)) {
							Log.w(limitedTAG, "User is already in the check deposit work-flow");
						}

						navActivity.hideSlidingMenuIfVisible();
					}
				}
			}

		};
	}

}
