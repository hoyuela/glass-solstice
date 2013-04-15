/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.analytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.login.LoginActivity;
import com.discover.mobile.bank.paybills.BankPayConfirmFragment;
import com.discover.mobile.bank.paybills.BankSelectPayee;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.account.AccountList;
import com.discover.mobile.bank.transfer.BankTransferStepOneFragment;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.auth.KeepAlive;

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
	private static String previousTrackedPage = "";

	/**Accounts String*/
	private static String accountString;

	/**Separator for the list divider*/
	private static final String COLON = ":";

	/**Max number of accounts in the account tag*/
	private static final int MAX_ACCOUNT_NUMBER = 4;

	/**The start of the account for the user*/
	private static final String ACCOUNT_TAG_START = "DiscoverMobile";

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
			TrackingHelper.trackBankPage(
					DiscoverActivityManager.getActiveActivity().getString(trackingMap.get(className)), getExtras(className));
			previousTrackedPage = className;
		}
	}

	/**
	 * Get the extra values that need to be tagged if any
	 * @return extras - map of extras to add to the tracking helper
	 */
	public static HashMap<String, Object> getExtras(final String className){
		final HashMap<String, Object> map = new HashMap<String, Object>();

		if(className.equals(BankAccountSummaryFragment.class.getSimpleName()) ||
				className.equals(BankTransferStepOneFragment.class.getSimpleName()) ||
				className.equals(BankSelectPayee.class.getSimpleName()) ||
				className.equals(BankDepositSelectAccount.class.getSimpleName())){

			if(null == accountString ){
				accountString = createAccountString();
			}
			map.put(TrackingHelper.ACCOUNT_TAG, accountString);

		}

		if(KeepAlive.getBankAuthenticated() && KeepAlive.getCardAuthenticated()){
			map.put(TrackingHelper.SSO_TAG, TrackingHelper.SINGLE_SIGN_ON_VALUE);
		}

		return map;
	}

	/**
	 * Create the account string that is used throughout being logged into the application.
	 * @return the account list string
	 */
	private static String createAccountString() {
		final AccountList list = BankUser.instance().getAccounts();
		final List<String> types = new ArrayList<String>();
		final StringBuilder string = new StringBuilder(ACCOUNT_TAG_START);
		int count = 0;

		if(null != list && null != list.accounts){
			for(final Account account : list.accounts){
				types.add(account.type);
			}

			if(types.contains(Account.ACCOUNT_CHECKING)){
				string.append(COLON);
				string.append(Account.ACCOUNT_CHECKING);
				while(types.contains(Account.ACCOUNT_CHECKING)){
					types.remove(Account.ACCOUNT_CHECKING);
				}
				count++;
			}

			for(int i = 0; (count < MAX_ACCOUNT_NUMBER) && (i < types.size()); i++){
				final String type = types.get(i);
				if(!string.toString().contains(type)){
					string.append(COLON);
					string.append(type);
					count++;
				}
			}
		}
		return string.toString();
	}

	/**
	 * Force the application to track a page.
	 * @param trackingString - resource id of the string that should be used to track the page
	 */
	public static void forceTrackPage(final int trackingString){
		TrackingHelper.trackBankPage(DiscoverActivityManager.getActiveActivity().getString(trackingString));
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
		trackingMap.put(BankSelectPayee.class.getSimpleName(), R.string.bank_payment_start);
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
