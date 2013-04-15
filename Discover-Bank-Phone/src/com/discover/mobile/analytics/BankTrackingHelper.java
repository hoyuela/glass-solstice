/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.analytics;

import java.util.HashMap;
import java.util.Map;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.account.BankAccountSummaryFragment;
import com.discover.mobile.bank.atm.SearchByLocationFragment;
import com.discover.mobile.bank.atm.SearchNearbyFragment;
import com.discover.mobile.bank.deposit.BankDepositConfirmFragment;
import com.discover.mobile.bank.deposit.BankDepositSelectAccount;
import com.discover.mobile.bank.deposit.BankDepositSelectAmount;
import com.discover.mobile.bank.deposit.CaptureReviewFragment;
import com.discover.mobile.bank.deposit.DepositSubmissionActivity;
import com.discover.mobile.bank.login.LoginActivity;
import com.discover.mobile.bank.paybills.BankPayConfirmFragment;
import com.discover.mobile.bank.paybills.SchedulePaymentFragment;
import com.discover.mobile.bank.transfer.BankTransferStepOneFragment;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.analytics.TrackingHelper;

/**
 * Class used to wrap the tracking helper to track the users as they move through out the application.
 * This will keep track of key features that are used in the application.
 * 
 * @author jthornton
 *
 */
public final class BankTrackingHelper {

	/**Map of class names to the string resource id for the tracking of pages*/
	private static Map<String, Integer> trackingMap;

	/**Last page that was tracked*/
	private static String previousTrackedPage;

	/**
	 * Track the page in the application.  This method will determine if the page needs to 
	 * be tracked.
	 * 
	 * @param className - name of the class that is going to be displayed. The string that needs
	 * to be sent in is [CLASS_NAME].class.getSimpleName(); so that it can be looked up.
	 */
	public static void trackPage(final String className){
		if(null == trackingMap){
			createMap();
		}
		if(!previousTrackedPage.equals(className) && null != trackingMap.get(className)){
			TrackingHelper.trackBankPage(DiscoverActivityManager.getActiveActivity().getString(trackingMap.get(className)));
			previousTrackedPage = className;
		}
	}

	/**
	 * Force the application to track a page.
	 * @param trackingString - resource id of the string that should be used to track the page
	 */
	public static void forceTrackPage(final int trackingString){
		TrackingHelper.trackBankPage(DiscoverActivityManager.getActiveActivity().getString(trackingMap.get(trackingString)));
	}

	/**
	 * Create the map of class names to the strings that are used to track that page.
	 */
	private static void createMap(){
		trackingMap = new HashMap<String, Integer>(); 
		trackingMap.put(LoginActivity.class.getSimpleName(), R.string.bank_login);
		trackingMap.put(BankAccountSummaryFragment.class.getSimpleName(), R.string.bank_account_summary);
		//TODO: Need transfer confirmation
		trackingMap.put(BankDepositConfirmFragment.class.getSimpleName(), R.string.bank_deposit_confirmation);
		trackingMap.put(BankPayConfirmFragment.class.getSimpleName(), R.string.bank_pay_bill_confirmation);
		trackingMap.put(BankTransferStepOneFragment.class.getSimpleName(), R.string.bank_transfer_start);
		trackingMap.put(BankDepositSelectAccount.class.getSimpleName(),R.string.bank_select_account);
		trackingMap.put(SchedulePaymentFragment.class.getSimpleName(), R.string.bank_payment_start);
		trackingMap.put(SearchByLocationFragment.class.getSimpleName(), R.string.bank_atm_start);
		trackingMap.put(SearchNearbyFragment.class.getSimpleName(), R.string.bank_atm_start);
		trackingMap.put(BankDepositSelectAmount.class.getSimpleName(), R.string.bank_deposit_amount);
		trackingMap.put(CaptureReviewFragment.class.getSimpleName(), R.string.bank_capture_confirm);
		trackingMap.put(DepositSubmissionActivity.class.getSimpleName(), R.string.bank_capture_sending);
		trackingMap.put(BankDepositConfirmFragment.class.getSimpleName(), R.string.bank_capture_acknowledge);
	}

	/**
	 * A private constructor to enforce static use of this class
	 */
	private BankTrackingHelper() {
		throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
	}

}
