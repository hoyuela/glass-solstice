package com.discover.mobile.bank.paybills;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.account.BankAccountSummaryFragment;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.help.HelpMenuListFactory;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.services.payment.PaymentDetail;
import com.discover.mobile.bank.services.payment.PaymentQueryType;
import com.discover.mobile.bank.ui.fragments.BankOneButtonFragment;
import com.discover.mobile.bank.ui.table.ListItemGenerator;
import com.discover.mobile.bank.ui.table.ViewPagerListItem;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.help.HelpWidget;

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
		actionButton.setText(R.string.bank_pmt_make_another);

		/**Set text to Review Payment*/
		actionLink.setText(R.string.bank_pmt_review);

		/**Setup Progress Indicator to show Payment Details and Payment Scheduled, on step 1, and hide step 2 **/
		progressIndicator.initialize(2);
		progressIndicator.hideStepTwo();
		progressIndicator.setTitle(R.string.bank_pmt_details, R.string.confirm, R.string.confirm);

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
		/**
		 * Remove this fragment from the transactions list, this seems to be required since 
		 * makeVisible(fragment, boolean) was used.
		 */
		getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();

		final BankNavigationRootActivity activity = (BankNavigationRootActivity)this.getActivity();
		activity.popTillFragment(BankSelectPayee.class);
	}

	/**
	 * Method implementation of Bank com.discover.mobile.bank.ui.fragments.BankOneButtonFragment.onActionLinkClick()
	 * Navigates User to Review Payements Screen
	 */
	@Override
	protected void onActionLinkClick() {

		/**
		 * Remove this fragment from the transactions list, this seems to be required since 
		 * makeVisible(fragment, boolean) was used.
		 */
		getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();

		//Generate a url to download schedule payments
		final String url = BankUrlManager.generateGetPaymentsUrl(PaymentQueryType.SCHEDULED);

		final BankNavigationRootActivity activity = (BankNavigationRootActivity)this.getActivity();
		activity.popTillFragment(BankAccountSummaryFragment.class);

		BankServiceCallFactory.createGetPaymentsServerCall(url).submit();
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

	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.PAY_BILLS_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.PAY_BILLS_SECTION;
	}

	@Override
	public boolean isBackPressDisabled() {
		return true;
	}

	@Override
	public void onBackPressed() {
		//Nothing todo here
	}


	@Override
	protected void helpMenuOnClick(final HelpWidget help) {
		help.showHelpItems(HelpMenuListFactory.instance().getPayBillsHelpItems());
	}
}
