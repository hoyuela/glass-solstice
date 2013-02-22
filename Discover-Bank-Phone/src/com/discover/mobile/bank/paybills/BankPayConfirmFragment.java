package com.discover.mobile.bank.paybills;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.BankNavigator;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.account.BankAccountSummaryFragment;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.bank.services.payment.PaymentDetail;
import com.discover.mobile.bank.ui.fragments.BankOneButtonFragment;
import com.discover.mobile.bank.ui.table.ListItemGenerator;
import com.discover.mobile.bank.ui.table.ViewPagerListItem;
import com.discover.mobile.common.DiscoverActivityManager;

/**
 * This class is used to show confirmation of a Payment scheduled by a user. It consists
 * of a progress indicator, an action button to make another payment, a link to reivew
 * a list of payments, a feedback link and a help button.
 * 
 * 
 * @author henryoyuela
 *
 */
final public class BankPayConfirmFragment extends BankOneButtonFragment {
	/**
	 * Holds list of content that is to be displayed in the contentTable linearLayout created by the
	 * base class.
	 */
	private List<ViewPagerListItem> contentItems;
	
	/**
	 * Generates the list of content required for this view using ListItemGenerator class. Configures
	 * the Progress Indicator to be in step 2 in the work-flow. Sets any attributes required for the
	 * views in this Fragment.
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {	
		/**Create list of items that will be render the Payment Detail information on the screen*/
		final ListItemGenerator generator = new ListItemGenerator(DiscoverActivityManager.getActiveActivity());
	
		/**Fetch data from bundle passed to fragment**/
		final Bundle bundle = getArguments();
		final PaymentDetail item = (PaymentDetail)bundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM);
		contentItems =  generator.getScheduledPaymentDetailList(item);
		
		final View view = super.onCreateView(inflater, container, savedInstanceState);
			
		/**Set text to Make Another Payment*/
		this.actionButton.setText(R.string.bank_pmt_make_another);
		
		/**Set text to Review Payment*/
		this.actionLink.setText(R.string.bank_pmt_review);
		
		/**Setup Progress Indicator to show Payment Details and Payment Scheduled, on step 1, and hide step 2 **/
		this.progressIndicator.initChangePasswordHeader(2);
		this.progressIndicator.hideStepTwo();
		this.progressIndicator.setTitle(R.string.bank_pmt_details, R.string.bank_pmt_scheduled, R.string.bank_pmt_scheduled);

		
		return view;
	}


	@Override
	public int getActionBarTitle() {
		return R.string.section_title_pay_bills;
	}

	/**
	 * Method implementation of Bank com.discover.mobile.bank.ui.fragments.BankOneButtonFragment.onActionButtonClick()
	 * Navigates User to previous BankSelectPayee which originally brought the user to this Fragment
	 */
	@Override
	protected void onActionButtonClick() {
		final BankNavigationRootActivity activity = (BankNavigationRootActivity)this.getActivity();
		activity.popTillFragment(BankSelectPayee.class);
	}

	/**
	 * Method implementation of Bank com.discover.mobile.bank.ui.fragments.BankOneButtonFragment.onActionLinkClick()
	 * Navigates User to Review Payements Screen
	 */
	@Override
	protected void onActionLinkClick() {
		BankNavigator.navigateToReviewPayments(null, false);
	}

	/**
	 * Returns list of content generated in onCreate to the base class BankOneButtonFragment to display
	 * the content.
	 */
	@Override
	protected List<ViewPagerListItem> getViewPagerListContent() {
		return contentItems;
	}


	@Override
	protected List<RelativeLayout> getRelativeLayoutListContent() {
		return null;
	}
	
}
