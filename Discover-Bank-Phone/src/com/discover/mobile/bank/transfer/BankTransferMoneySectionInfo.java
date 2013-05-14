package com.discover.mobile.bank.transfer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.services.account.AccountList;
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
				new ClickComponentInfo(R.string.sub_section_title_review_transfers,true, 
															externalLink(BankUrlManager.getStatementsUrl())), 
				new ClickComponentInfo(R.string.sub_section_title_manage_external_accounts,true,
														externalLink(BankUrlManager.getManageExternalAccountsUrl())));
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
			public void onClick(final View v) {/**Start download for bank holiday concurrently with payees download*/
				if( BankUser.instance().getHolidays().isEmpty() ) {
					BankServiceCallFactory.createBankHolidayDownloadServiceCall().submit();
				}
				Activity activity = DiscoverActivityManager.getActiveActivity();
				final boolean isLoggedIn = (activity != null);
				final boolean isBankNavigationRootActivityActive = activity instanceof BankNavigationRootActivity;

				/**Verify that the user is logged in and the BankNavigationRootActivity is the active activity*/
				if( isLoggedIn && isBankNavigationRootActivityActive) {
					final BankNavigationRootActivity navActivity = (BankNavigationRootActivity) activity;
					activity = null;

					final boolean isEligible = BankUser.instance().getCustomerInfo().isTransferEligible();

					/**Check if user is already in the Transfer Money work-flow*/
					if( !isUserAlreadyInTransferFunds() ) {
						final AccountList cachedExternalAccounts = BankUser.instance().getExternalAccounts();
						if(isEligible && cachedExternalAccounts == null) {
							BankServiceCallFactory.createGetExternalTransferAccountsCall().submit();
						} else if(isEligible){
							final Bundle args = new Bundle();
							args.putSerializable(BankExtraKeys.EXTERNAL_ACCOUNTS, cachedExternalAccounts);
							BankConductor.navigateToTransferMoneyLandingPage(args);
						} else {
							//User is not eligible and will be taken to the inelligible landing page.
							BankConductor.navigateToTransferMoneyLandingPage(null);
						}
					} else {
						final String debugTag = BankTransferMoneySectionInfo.class.getSimpleName();

						//Log.isLoggable will throw an exception if it is given a tag of length > 23, 
						//so we need to restrict it.
						final String limitedTag = debugTag.substring(0, Math.min(debugTag.length(), 23));

						if( Log.isLoggable(limitedTag, Log.WARN)) {
							Log.w(limitedTag, "User is already in the check deposit work-flow");
						}

						navActivity.hideSlidingMenuIfVisible();
					}
				}
			}

		};
	}
	
	private static boolean isUserAlreadyInTransferFunds() {
		//Assume we are not in transfer funds.
		boolean alreadyInTransferFunds = false;
		
		if(DiscoverActivityManager.getActiveActivity() instanceof BankNavigationRootActivity) {
			final BankNavigationRootActivity navActivity = 
									(BankNavigationRootActivity) DiscoverActivityManager.getActiveActivity();
			alreadyInTransferFunds = true;
			
			alreadyInTransferFunds &= 
					navActivity.getCurrentContentFragment().getSectionMenuLocation() == 
					BankMenuItemLocationIndex.TRANSFER_MONEY_SECTION;
			
			alreadyInTransferFunds &= 
					navActivity.getCurrentContentFragment().getGroupMenuLocation() == 
					BankMenuItemLocationIndex.TRANSFER_MONEY_GROUP;
		}
		
		return alreadyInTransferFunds;
	}

}
