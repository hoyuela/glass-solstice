package com.discover.mobile.bank.transfer;

import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.navigation.BankNavigationHelper;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.services.transfer.TransferType;
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
				new ClickComponentInfo(R.string.sub_section_title_review_transfers, getReviewTransfersClickListener()), 
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
	
	private static OnClickListener getReviewTransfersClickListener() {
		return new OnClickListener() {

			@Override
			public void onClick(final View v) {
				if(isAlreadyViewingReviewTransfers()) {
					BankNavigationHelper.hideSlidingMenu();
				} else {
					BankConductor.navigateToReviewTransfers(TransferType.Scheduled);
				}
			}
		};
	}
	
	private static boolean isAlreadyViewingReviewTransfers() {
		return BankNavigationHelper.isViewingMenuSection(BankMenuItemLocationIndex.TRANSFER_MONEY_GROUP, 
															BankMenuItemLocationIndex.REVIEW_TRANSFERS_SECTION);
	}
	
	/**
	 * Returns a click listener that is responsible for navigating the user to the transfer money workflow.
	 * @return
	 */
	private static OnClickListener getTransferFundsLandingClickListener() {
		return new OnClickListener() {

			@Override
			public void onClick(final View v) {
				/**Start download for bank holiday concurrently with payees download*/
				if( BankUser.instance().getHolidays().isEmpty() ) {
					BankServiceCallFactory.createBankHolidayDownloadServiceCall().submit();
				}

				/** Check if user is already in the Transfer Money work-flow */
				if (!isUserAlreadyInTransferFunds()) {
					BankConductor.navigateToTransferMoney();
				} else {
					BankNavigationHelper.hideSlidingMenu();
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
	
	
