package com.discover.mobile.bank.paybills;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.BankRotationHelper;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.navigation.BankNavigationHelper;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.services.payment.PaymentQueryType;
import com.discover.mobile.common.nav.section.ClickComponentInfo;
import com.discover.mobile.common.nav.section.GroupComponentInfo;

/**
 * Menu item for the pay bill section.  Contains the following subsections:
 * 
 * Pay a Bill
 * Review Payments
 * Manage Payees
 * 
 * @author jthornton
 *
 */
public final class BankPayBillsSectionInfo extends GroupComponentInfo {

	/**
	 * Constructor for the section info.
	 */
	public BankPayBillsSectionInfo() {
		super(R.string.section_title_pay_bills,
				new ClickComponentInfo(R.string.section_title_pay_bills, getPayBillsLandingClickListener()),
				new ClickComponentInfo(R.string.sub_section_title_review_payments, getReviewPaymentsClickListener()),
				new ClickComponentInfo(R.string.sub_section_title_manage_payees, getManagePayeesClickListener()));
	}

	/**
	 * Click listener for the pay bills title. Based on the eligibility of the user,
	 * the app will send them to another page.
	 * @return the click listener for the pay bills title
	 */
	public static OnClickListener getPayBillsLandingClickListener(){
		return new OnClickListener(){
			@Override
			public void onClick(final View v) {
				/**Start download for bank holiday concurrently with payees download*/
				if( BankUser.instance().getHolidays().isEmpty() ) {
					BankServiceCallFactory.createBankHolidayDownloadServiceCall().submit();
				}
				
				/**Check if user is already in this workflow*/
				if( !BankPayBillsSectionInfo.isViewingMenuSection(BankMenuItemLocationIndex.PAY_BILLS_SECTION)) {
					if(!isEligible()){
						BankConductor.navigateToPayBillsLanding();
					} else if(isEligible() && !isEnrolled()){
						sendToTermsScreen(R.string.section_title_pay_bills);
					} else{								
						if(null == BankUser.instance().getPayees()) {							
							BankServiceCallFactory.createGetPayeeServiceRequest().submit();
						} else{
							final Bundle bundle = new Bundle();
							bundle.putSerializable(BankExtraKeys.PAYEES_LIST, BankUser.instance().getPayees());
							BankConductor.navigateToSelectPayee(bundle);
						}
					}
				} else {
					BankNavigationHelper.hideSlidingMenu();
				}
			}
		};
	}

	/**
	 * Click listener for the manage payees screen. The app will send the user to the appropriate section of the
	 * application bsed on their eligibility and enrollment status for paybills.
	 * @return the click listener for the manage payees title.
	 */
	public static OnClickListener getManagePayeesClickListener() {
		return new OnClickListener() {
			@Override
			public void onClick(final View v) {
				/**Check if user is already in this workflow*/
				if( !BankPayBillsSectionInfo.isViewingMenuSection(BankMenuItemLocationIndex.MANAGE_PAYEES_SECTION)) {
					if(!isEligible()) {
						BankConductor.navigateToPayBillsLanding();
					} else if (isEligible() && !isEnrolled()) {
						sendToTermsScreen(R.string.sub_section_title_manage_payees);
					}else {
						BankServiceCallFactory.createManagePayeeServiceRequest().submit();
					}
				} else {
					BankNavigationHelper.hideSlidingMenu();
				}
			}
		};
	}

	/**
	 * Send the user to the pay bill terms and conditions page
	 * @param title used to signify what screen should come next
	 */
	protected static void sendToTermsScreen(final int title){
		final Bundle bundle = new Bundle();
		bundle.putInt(BankExtraKeys.TITLE_TEXT, title);
		BankConductor.navigateToPayBillsTerms(bundle);
	}

	/**
	 * Click listener for the review payments menu item.  Makes the service call to the initial set 
	 * of data.
	 * @return the click listener
	 */
	public static OnClickListener getReviewPaymentsClickListener(){
		return new OnClickListener(){
			@Override
			public void onClick(final View v) {
				/**Check if user is already in this workflow*/
				if( !BankPayBillsSectionInfo.isViewingMenuSection(BankMenuItemLocationIndex.REVIEW_PAYEMENTS_SECTION)) {
					if(!isEligible()){
						BankConductor.navigateToPayBillsLanding();
					} else if(isEligible() && !isEnrolled()){
						sendToTermsScreen(R.string.review_payments_title);
					} else{
						if(null == BankUser.instance().getPayees()) {
							BankServiceCallFactory.createGetPayeeServiceRequest(true).submit();
						} else{
							BankRotationHelper.getHelper().setBundle(null);
							final String url = BankUrlManager.generateGetPaymentsUrl(PaymentQueryType.SCHEDULED);
							BankServiceCallFactory.createGetPaymentsServerCall(url).submit();
						}
					}
				} else {
					BankNavigationHelper.hideSlidingMenu();
				}
			}
		};
	}

	/**
	 * Method call to see if a uses is eligible for payments
	 * @return if a user is eligible for payments
	 */
	protected static boolean isEligible(){
		return BankUser.instance().getCustomerInfo().isPaymentsEligible();
	}

	/**
	 * Method call to see if a users is enrolled in payments
	 * @return if a user is enrolled in payments
	 */
	protected static boolean isEnrolled(){
		return BankUser.instance().getCustomerInfo().isPaymentsEnrolled();
	}
	
	/**
	 * Method used to determine if user is already viewing a screen that the user can navigate to
	 * via the menu section specified.
	 * 
	 * @param section Menu section used to check if the current fragment displayed is part of that work-flow.
	 * @return True if current fragment is reachable via the menu section specified, otherwise false.
	 */
	public static boolean isViewingMenuSection(final int section) {
		return BankNavigationHelper.isViewingMenuSection(
				BankMenuItemLocationIndex.PAY_BILLS_GROUP,
				section);
	}
	
	
}
