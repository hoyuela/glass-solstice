package com.discover.mobile.bank.paybills;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.BankNavigator;
import com.discover.mobile.bank.BankRotationHelper;
import com.discover.mobile.bank.BankUser;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
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
				if(!isEligible()){
					BankNavigator.navigateToPayBillsLanding();
				} else if(isEligible() && !isEnrolled()){
					sendToTermsScreen(R.string.section_title_pay_bills);
				} else{
					BankServiceCallFactory.createGetPayeeServiceRequest().submit();
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
				if(!isEligible()) {
					BankNavigator.navigateToPayBillsLanding();
				} else if (isEligible() && !isEnrolled()) {
					sendToTermsScreen(R.string.sub_section_title_manage_payees);
				}else {
					BankServiceCallFactory.createManagePayeeServiceRequest().submit();
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
		BankNavigator.navigateToPayBillsTerms(bundle);
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
				if(!isEligible()){
					BankNavigator.navigateToPayBillsLanding();
				} else if(isEligible() && !isEnrolled()){
					sendToTermsScreen(R.string.review_payments_title);
				} else{
					BankRotationHelper.getHelper().setBundle(null);
					final String url = BankUrlManager.generateGetPaymentsUrl(PaymentQueryType.SCHEDULED);
					BankServiceCallFactory.createGetPaymentsServerCall(url).submit();
				}
			}
		};
	}

	/**
	 * Method call to see if a uses is eligible for payments
	 * @return if a user is eligible for payments
	 */
	protected static boolean isEligible(){
		return BankUser.instance().getCustomerInfo().isPaymentsEligibility();
	}

	/**
	 * Method call to see if a users is enrolled in payments
	 * @return if a user is enrolled in payments
	 */
	protected static boolean isEnrolled(){
		return BankUser.instance().getCustomerInfo().isPaymentsEnrolled();
	}
}
