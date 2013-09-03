/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.analytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;

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
import com.discover.mobile.bank.services.customer.Customer;
import com.discover.mobile.bank.transfer.BankTransferConfirmationFragment;
import com.discover.mobile.bank.transfer.BankTransferStepOneFragment;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.utils.CommonUtils;
import com.discover.mobile.common.utils.StringUtility;
import com.google.common.base.Strings;

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
	 * Special keys for the check deposit analytics
	 */
	public static final String TRACKING_IMAGE_HEIGHT = "height";
	public static final String TRACKING_IMAGE_WIDTH = "width";
	public static final String TRACKING_IMAGE_SIZE = "size";
	public static final String TRACKING_IMAGE_COMPRESSION = "compression";
	public static final String TRACKING_IMAGE_AMOUNT = "amount";
	public static final String TRACKING_IMAGE_ACCOUNT = "account";
	public static final String TRACKING_SUBMIT_TIME = "timestamp";
	public static final String TRACKING_IMAGE_RESPONSE_CODE = "responseCode";
	public static final String TRACKING_IMAGE_RESPONSE_JSON = "responseJson";

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
		}
		previousTrackedPage = className;
	}

	/**
	 * Clear the previously tracked page so that the next page that is navigated to will get tracked.
	 */
	public static void clearPreviousTrackedPage() {
		previousTrackedPage = StringUtility.EMPTY;
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

		if(BankUser.instance().isSsoUser()){
			map.put(TrackingHelper.SSO_TAG, TrackingHelper.SINGLE_SIGN_ON_VALUE);
		}
		
		final Customer customer = BankUser.instance().getCustomerInfo();
		//If the customer value is not equal to null add these values (meaning that the user is logged in)
		if(null != customer){
			//Set these variables for the customer id
			map.put(TrackingHelper.CONTEXT_EDS_PROP, customer.id);
			map.put(TrackingHelper.CONTEXT_EDS_VAR, customer.id);
			//Set these variables so that the analytics knows its a customer
			map.put(TrackingHelper.CONTEXT_USER_PROP, TrackingHelper.CUSTOMER);
			map.put(TrackingHelper.CONTEXT_USER_VAR, TrackingHelper.CUSTOMER);
			map.put(TrackingHelper.CONTEXT_VSTR_ID_PROP, TrackingHelper.CUSTOMER);
			map.put(TrackingHelper.CONTEXT_VSTR_ID_VAR, TrackingHelper.CUSTOMER);
		}else{
			//Set these variables so that the analytics knows the user has not logged in
			map.put(TrackingHelper.CONTEXT_USER_PROP, TrackingHelper.PROSPECT);
			map.put(TrackingHelper.CONTEXT_USER_VAR, TrackingHelper.PROSPECT);
			map.put(TrackingHelper.CONTEXT_VSTR_ID_PROP, TrackingHelper.PROSPECT);
			map.put(TrackingHelper.CONTEXT_VSTR_ID_VAR, TrackingHelper.PROSPECT);
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

			for(int i = 0; count < MAX_ACCOUNT_NUMBER && i < types.size(); i++){
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
		final String className = DiscoverActivityManager.getString(trackingString);
		TrackingHelper.trackBankPage(className, getExtras(className));
	}

	/**
	 * Create the map of class names to the strings that are used to track that page.
	 */
	private static void createMap(){
		trackingMap = new HashMap<String, Integer>(); 
		trackingMap.put(LoginActivity.class.getSimpleName(), R.string.bank_login);
		trackingMap.put(BankAccountSummaryFragment.class.getSimpleName(), R.string.bank_account_summary);
		trackingMap.put(BankTransferConfirmationFragment.class.getSimpleName(), R.string.bank_transfer_confirmation);
		trackingMap.put(BankDepositConfirmFragment.class.getSimpleName(), R.string.bank_deposit_confirmation);
		trackingMap.put(BankPayConfirmFragment.class.getSimpleName(), R.string.bank_pay_bill_confirmation);
		trackingMap.put(BankTransferStepOneFragment.class.getSimpleName(), R.string.bank_transfer_start);
		trackingMap.put(BankDepositSelectAccount.class.getSimpleName(),R.string.bank_select_account);
		trackingMap.put(BankSelectPayee.class.getSimpleName(), R.string.bank_payment_start);
		trackingMap.put(SearchByLocationFragment.class.getSimpleName(), R.string.bank_atm_start);
		trackingMap.put(SearchNearbyFragment.class.getSimpleName(), R.string.bank_atm_start);
		trackingMap.put(BankDepositSelectAmount.class.getSimpleName(), R.string.bank_deposit_amount_tracking);
		trackingMap.put(CaptureReviewFragment.class.getSimpleName(), R.string.bank_capture_confirm);
		trackingMap.put(DepositSubmissionActivity.class.getSimpleName(), R.string.bank_capture_sending);
		trackingMap.put(BankDepositConfirmFragment.class.getSimpleName(), R.string.bank_capture_acknowledge);
		trackingMap.put(BankSelectPayee.class.getSimpleName(), R.string.bank_select_payee);
	}

	/**
	 * A private constructor to enforce static use of this class
	 */
	private BankTrackingHelper() {
		throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
	}

	/**
	 * Track specific details about the check deposit submission
	 * @param extras - bundle of extras 
	 */
	public static void trackDepositSubmission(final Bundle extras) {
		final Map<String, Object> map = new HashMap<String, Object>();

		//Customer
		map.put(TrackingHelper.CONTEXT_EDS_PROP, BankUser.instance().getCustomerInfo().id);
		map.put(TrackingHelper.CONTEXT_EDS_VAR, BankUser.instance().getCustomerInfo().id);

		//Account
		map.put(DiscoverActivityManager.getString(R.string.context_account_var), extras.get(TRACKING_IMAGE_ACCOUNT));
		map.put(DiscoverActivityManager.getString(R.string.context_account_prop), extras.get(TRACKING_IMAGE_ACCOUNT));

		//Amount
		map.put(DiscoverActivityManager.getString(R.string.context_amount_var), extras.get(TRACKING_IMAGE_AMOUNT));
		map.put(DiscoverActivityManager.getString(R.string.context_amount_var), extras.get(TRACKING_IMAGE_AMOUNT));

		//Response Code
		map.put(DiscoverActivityManager.getString(R.string.context_http_response_prop), 
				extras.get(TRACKING_IMAGE_RESPONSE_CODE));

		//Error JSON
		final String errorMessage = extras.getString(TRACKING_IMAGE_RESPONSE_JSON);
		if(!Strings.isNullOrEmpty(errorMessage)){
			map.put(DiscoverActivityManager.getString(R.string.context_json_response_prop), errorMessage);
		}

		//Compression
		map.put(DiscoverActivityManager.getString(R.string.context_compression_ration_prop), 
				extras.get(TRACKING_IMAGE_COMPRESSION));

		//Build Id
		map.put(DiscoverActivityManager.getString(R.string.context_build_version_var), 
				CommonUtils.getApplicationVersionNumber());
		map.put(DiscoverActivityManager.getString(R.string.context_build_version_prop), 
				CommonUtils.getApplicationVersionNumber());

		//Image size
		map.put(DiscoverActivityManager.getString(R.string.context_image_size_var), extras.get(TRACKING_IMAGE_SIZE));
		map.put(DiscoverActivityManager.getString(R.string.context_image_size_prop), extras.get(TRACKING_IMAGE_SIZE));

		//Pixels long
		map.put(DiscoverActivityManager.getString(R.string.context_image_pixel_width_var), 
				extras.get(TRACKING_IMAGE_HEIGHT));
		map.put(DiscoverActivityManager.getString(R.string.context_image_pixel_width_prop), 
				extras.get(TRACKING_IMAGE_HEIGHT));

		//Pixels wide
		map.put(DiscoverActivityManager.getString(R.string.context_image_pixel_height_var), 
				extras.get(TRACKING_IMAGE_WIDTH));
		map.put(DiscoverActivityManager.getString(R.string.context_image_pixel_height_prop), 
				extras.get(TRACKING_IMAGE_WIDTH));

		//Request Timestamp
		map.put(DiscoverActivityManager.getString(R.string.context_request_timestamp_var), 
				extras.get(TRACKING_SUBMIT_TIME));
		map.put(DiscoverActivityManager.getString(R.string.context_request_timestamp_prop), 
				extras.get(TRACKING_SUBMIT_TIME));

		TrackingHelper.trackBankPage(DiscoverActivityManager.getString(R.string.bank_capture_send_result), map);

	}

}
