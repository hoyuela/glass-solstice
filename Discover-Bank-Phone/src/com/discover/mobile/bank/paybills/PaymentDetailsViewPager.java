package com.discover.mobile.bank.paybills;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.help.HelpMenuListFactory;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.bank.services.account.activity.ListActivityDetail;
import com.discover.mobile.bank.services.json.ReceivedUrl;
import com.discover.mobile.bank.services.payment.ListPaymentDetail;
import com.discover.mobile.bank.services.payment.PaymentDetail;
import com.discover.mobile.bank.ui.SpinnerFragment;
import com.discover.mobile.bank.ui.widgets.DetailViewPager;
import com.discover.mobile.bank.util.FragmentOnBackPressed;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.help.HelpWidget;
import com.google.common.base.Strings;

public class PaymentDetailsViewPager extends DetailViewPager implements FragmentOnBackPressed{
	private ListPaymentDetail detailList = new ListPaymentDetail();
	private int initialViewPosition = 0;
	private int fragmentTitle;

	@Override
	public int getActionBarTitle() {
		return R.string.payment_detail;
	}

	/**
	 * Get any data that was either passed in from another Fragment as Bundle extras or as savedInstanceState
	 * extras from a rotation change. In the onCreate we give precedence to a savedInstanceState bundle 
	 * because if this is not null that means we have a rotation change with potentially more up to date
	 * information.
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		loadBundleArgs(getArguments());

		if(savedInstanceState != null) {
			detailList = (ListPaymentDetail)savedInstanceState.getSerializable(BankExtraKeys.PRIMARY_LIST);
			initialViewPosition = savedInstanceState.getInt(BankExtraKeys.DATA_SELECTED_INDEX);
		}

		//Make sure the list is not null so that the Fragment will not crash upon getting no data.
		if(detailList.payments == null) {
			detailList.payments = new ArrayList<PaymentDetail>();
		}

	}


	/**
	 * 
	 * @return if the currently displayed data set has more data that can be retrieved from the server.
	 */
	private boolean canLoadMore() {
		return detailList != null && detailList.links != null &&
				detailList.links.get(ListActivityDetail.NEXT) != null;
	}

	/**
	 * Save the current Bundle state so that we can restore it on rotation change.
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState) {
		outState.putAll(getCurrentFragmentBundle());
		super.onSaveInstanceState(outState);
	}

	/**
	 * Gets the list of ActivityDetail objects from a bundle along with the current selected position in that list
	 * to show.
	 * @param bundle a Bundle that contains a ListActivityDetail object and an integer representing the current
	 * 			visible item.
	 */
	public void loadBundleArgs(final Bundle bundle) {
		if(bundle != null){
			detailList = (ListPaymentDetail)bundle.getSerializable(BankExtraKeys.PRIMARY_LIST);
			initialViewPosition = bundle.getInt(BankExtraKeys.DATA_SELECTED_INDEX);
			fragmentTitle = bundle.getInt(BankExtraKeys.TITLE_TEXT);
		}
	}

	/**
	 * Get the current Bundle for this Fragment, then update the data list and index fields on it
	 * and return the updated Bundle
	 * @return an up to date Bundle with the current information of the ViewPager.
	 */
	private Bundle getCurrentFragmentBundle() {
		Bundle currentBundle = getArguments();
		if(currentBundle == null) {
			currentBundle = new Bundle();
		}

		final ViewPager viewPager = getViewPager();
		if(viewPager != null) {
			currentBundle.putInt(BankExtraKeys.DATA_SELECTED_INDEX, viewPager.getCurrentItem());
		}
		if(detailList != null) {
			currentBundle.putSerializable(BankExtraKeys.PRIMARY_LIST, detailList);
		}
		return currentBundle;

	}

	/**
	 * Called by the DetailViewPager class to retrieve a constructed PaymentDetail Fragment.
	 * This method returns a payment Fragment that is ready to be shown in the DetailViewPager.
	 */
	@Override
	protected Fragment getDetailItem(final int position) {
		Fragment pageFragment = null;
		if(position < detailList.payments.size()) {
			pageFragment = new PaymentDetailFragment();
			final PaymentDetail payment = detailList.payments.get(position);
			final Bundle bundle = new Bundle();
			bundle.putSerializable(BankExtraKeys.DATA_LIST_ITEM, payment);
			pageFragment.setArguments(bundle);
		}else {
			pageFragment = new SpinnerFragment();
		}

		return pageFragment;	
	}

	/**
	 * The DetailViewPager calls this method to find out how many detail items can be shown.
	 */
	@Override
	protected int getViewCount() {
		return detailList.payments.size();
	}

	/**
	 * The DetailViewPager will call this method to setup the first visible Fragment.
	 */
	@Override
	protected int getInitialViewPosition() {
		return initialViewPosition;
	}

	/**
	 * When a Fragment is shown in the DetailViewPager, it calls this method to upate the title for the
	 * detail item that is being shown.
	 */
	@Override
	protected int getTitleForFragment(final int position) {
		if(detailList.payments.size() > 0){
			return fragmentTitle;
		} else {
			return R.string.no_data_found;
		}
	}

	@Override
	protected boolean isUserPrimaryHolder(final int position) {
		return !detailList.payments.get(position).isJointPayment;
	}

	/**
	 * This method is used to update the detailList of payments with new payments that get sent
	 * back to the DetailViewPager on a load more service call.
	 * The returned results are appended to the current list of payments.
	 * The navigation buttnons get updated because when the user is at the end of the list,
	 * the next button gets disabled and should be re-enabled if we successfully load more.
	 */
	@Override
	public void handleReceivedData(final Bundle bundle) {
		setIsLoadingMore(false);
		final ListPaymentDetail newDetails = (ListPaymentDetail)bundle.getSerializable(BankExtraKeys.PRIMARY_LIST);
		detailList.payments.addAll(newDetails.payments);
		updateNavigationButtons();
		detailList.links.putAll(newDetails.links);
		resetViewPagerAdapter();
	}

	/**
	 * This method is called when the back button is pressed on this Fragment.
	 */
	// FIXME need to have a navigator method defined that allows navigating back to the view payments Fragment.
	@Override
	public void onPause() {
		super.onPause();
		initialViewPosition = getViewPager().getCurrentItem();
	}

	/**
	 * If we reach the end of the list of elements, load more if possible.
	 * @param position
	 */
	@Override
	protected void loadMoreIfNeeded(final int position) {
		final int loadMorePosition = getViewCount() - 2;

		if(loadMorePosition <= position && null != detailList.links.get(ListActivityDetail.NEXT)){
			final ReceivedUrl url = getLoadMoreUrl();
			if(url != null && !Strings.isNullOrEmpty(url.url)) {
				loadMore(detailList.links.get(ListActivityDetail.NEXT).url);
			}
		}
	}

	/**
	 * Get the load more URL
	 * @return get the load more URL from the correct object
	 */
	private ReceivedUrl getLoadMoreUrl() {
		return detailList.links.get(ListActivityDetail.NEXT);
	}

	/**
	 * Submit the load more service call to load more data.
	 */
	@Override
	protected void loadMore(final String url) {
		setIsLoadingMore(true);
		BankServiceCallFactory.createGetPaymentsServerCall(url).submit();		
	}

	/**
	 * This method determines if the current Fragment is editable, and will show or hide the
	 * delete and edit buttons based on that.
	 * If the payment is not scheduled or the current user is not the account holder then it cannot be edited.
	 */
	@Override
	protected boolean isFragmentEditable(final int position) {
		return (detailList != null && 
				detailList.payments != null && 
				detailList.payments.size() > 0) &&
				isUserPrimaryHolder(position) &&
				"SCHEDULED".equalsIgnoreCase(detailList.payments.get(position).status );
	}

	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.PAY_BILLS_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.REVIEW_PAYEMENTS_SECTION;
	}

	@Override
	protected void helpMenuOnClick(final HelpWidget help) {
		help.showHelpItems(HelpMenuListFactory.instance().getPayBillsHelpItems());
	}

	@Override
	public void onBackPressed() {
		final BankNavigationRootActivity activity = 
				(BankNavigationRootActivity)DiscoverActivityManager.getActiveActivity();
		activity.getSupportFragmentManager().popBackStackImmediate();
		final BaseFragment fragment = activity.getCurrentContentFragment();
		if(fragment instanceof ReviewPaymentsTable){
			((ReviewPaymentsTable) fragment).loadDataFromBundle(getCurrentFragmentBundle());
		}
	}

	@Override
	public boolean isBackPressDisabled() {
		return true;
	}

}
