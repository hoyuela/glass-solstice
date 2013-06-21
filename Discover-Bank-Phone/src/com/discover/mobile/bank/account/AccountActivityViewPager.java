package com.discover.mobile.bank.account;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.bank.services.account.activity.ActivityDetail;
import com.discover.mobile.bank.services.account.activity.ListActivityDetail;
import com.discover.mobile.bank.services.transfer.TransferDetail;
import com.discover.mobile.bank.ui.SpinnerFragment;
import com.discover.mobile.bank.ui.widgets.DetailViewPager;
import com.discover.mobile.bank.util.FragmentOnBackPressed;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.DiscoverActivityManager;
import com.google.common.base.Strings;

/**
 * This is a subclass of the DetailView pager.
 * The purpose of any subclass of the DetailViewPager is to handle any functionality that
 * is specific to where the ViewPager is needed. This class specifically will handle
 * passing data to and from the previous Fragment (account transactions list) and providing
 * assembled Fragments to the ViewPager to show.
 * 
 * @author scottseward
 */
public class AccountActivityViewPager extends DetailViewPager implements FragmentOnBackPressed{
	//The list that is used to display detail Activity information.
	private ListActivityDetail activityItems = null;
	private int initialViewPosition = 0;

	/**
	 * Save the state of the ViewPager for rotation.
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState) {
		outState.putAll(getCurrentFragmentBundle());
		super.onSaveInstanceState(outState);
	}

	/**
	 * Returns the current Fragment Bundle.
	 * @return the current Fragment Bundle;
	 */
	private Bundle getCurrentFragmentBundle() {
		Bundle currentBundle = this.getArguments();

		if(currentBundle == null) {
			currentBundle = new Bundle();
		}

		final ViewPager viewPager = getViewPager();
		if(viewPager != null) {
			currentBundle.putInt(BankExtraKeys.DATA_SELECTED_INDEX, viewPager.getCurrentItem());
		}
		if(activityItems != null) {
			currentBundle.putSerializable(BankExtraKeys.PRIMARY_LIST, activityItems);
		}
		return currentBundle;
	}

	/**
	 * Handles retrieving data from any arguments passed to the Fragment or from a savedInstanceState from
	 * rotation changes.
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		loadBundleArgs(getArguments());

		if(savedInstanceState != null) {
			activityItems = (ListActivityDetail)savedInstanceState.getSerializable(BankExtraKeys.PRIMARY_LIST);
		}
	}

	/**
	 * Gets the list of ActivityDetail objects from a bundle along with the current selected position in that list
	 * to show.
	 * @param bundle a Bundle that contains a ListActivityDetail object and an integer representing the current
	 * 			visible item.
	 */
	public void loadBundleArgs(final Bundle bundle) {
		if(bundle != null){
			activityItems = (ListActivityDetail)bundle.getSerializable(BankExtraKeys.PRIMARY_LIST);
			initialViewPosition = bundle.getInt(BankExtraKeys.DATA_SELECTED_INDEX);
		}
	}

	/**
	 * Returns the title to display on the action bar.
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.transaction_detail;
	}

	/**
	 * Returns the initialViewPosition so that the ViewPager can setup this Fragment to be visible first.
	 */
	@Override
	protected int getInitialViewPosition() {
		return initialViewPosition;
	}

	/**
	 * Returns the number of ActivityDetail objects in the list so that the ViewPager knows how many Fragments
	 * can exist.
	 */
	@Override
	protected int getViewCount() {
		int viewCount = 0;

		if(activityItems.activities != null) {
			viewCount = activityItems.activities.size();
			if (canLoadMore()) {
				viewCount++;
			}
		}

		return viewCount;
	}

	/**
	 * Returns a Fragment to show in the ViewPager.
	 * @return a Fragment to show in the ViewPager.
	 */
	@Override
	protected Fragment getDetailItem(final int position) {
		Fragment pageFragment = null;
		
		if(position < activityItems.activities.size()) {
			final Bundle bundle = new Bundle();
			final ActivityDetail detailObject = activityItems.activities.get(position);
			bundle.putSerializable(BankExtraKeys.DATA_LIST_ITEM, detailObject);
			bundle.putBoolean(BankExtraKeys.CATEGORY_SELECTED, getArguments().getBoolean(BankExtraKeys.CATEGORY_SELECTED));
			pageFragment = new ActivityDetailFragment();
			pageFragment.setArguments(bundle);
		}else {
			pageFragment = new SpinnerFragment();
		}

		return pageFragment;
	}

	/**
	 * Instead of simply trashing the current Fragment onBackPressed, we need to pass the current Bundle back to
	 * the AccountActivityPage so that if we loaded any new information, it will be shown in the AccountActivityPage.
	 */
	@Override
	public void onPause() {
		super.onPause();
		initialViewPosition = getViewPager().getCurrentItem();
	}

	/**
	 * What to do after we load more data from the server.
	 */
	@Override
	public void handleReceivedData(final Bundle bundle) {
		setIsLoadingMore(false);
		final ListActivityDetail list = (ListActivityDetail)bundle.getSerializable(BankExtraKeys.PRIMARY_LIST);
		activityItems.activities.addAll( list.activities);
		updateNavigationButtons();
		resetViewPagerAdapter();
	}

	/**
	 * Return the title for the current fragment.
	 */
	@Override
	protected int getTitleForFragment(final int position) {
		int title = R.string.transaction;
		//If the fragment is a spinner fragment, which would mean we are loadig more, return no title.
		if(activityItems.activities.size() <= position){
			title = R.string.empty;
		}else {
			final String transactionType = activityItems.activities.get(position).type;
			
			//Decide what kind of transaction we have, and return an applicable title.
			if(!ActivityDetail.POSTED.equalsIgnoreCase(transactionType)){
				if(ActivityDetail.TYPE_DEPOSIT.equalsIgnoreCase(transactionType)){
					title = R.string.check_deposit;
				}else if(ActivityDetail.TYPE_PAYMENT.equalsIgnoreCase(transactionType)){
					title = R.string.bill_pay;
				}else if(ActivityDetail.TYPE_TRANSFER.equalsIgnoreCase(transactionType)) {
					final String frequency = activityItems.activities.get(position).frequency;
					if (!Strings.isNullOrEmpty(frequency) && !frequency.equalsIgnoreCase(TransferDetail.ONE_TIME_TRANSFER)) {
						title = R.string.repeating_funds_transfer;
					}else{
						title = R.string.funds_transfer;
					}
				}
			}
		}
		return title;
	}

	/**
	 * If we reach the end of the list of elements, load more if possible.
	 * @param currentPosition
	 */
	@Override
	protected void loadMoreIfNeeded(final int currentPosition) {
		final int loadMorePosition = getViewCount() - 2;

		if(loadMorePosition <= currentPosition && canLoadMore()){
			loadMore(activityItems.links.get(ListActivityDetail.NEXT).url);
		}
	}

	/**
	 * 
	 * @return if the currently displayed data set has more data that can be retrieved from the server.
	 */
	private boolean canLoadMore() {
		return activityItems != null && activityItems.links != null &&
				activityItems.links.get(ListActivityDetail.NEXT) != null;
	}

	/**
	 * Submit the load more service call to load more data.
	 */
	@Override
	protected void loadMore(final String url) {
		if(!getIsLoadingMore()) {
			setIsLoadingMore(true);

			BankServiceCallFactory.createGetActivityServerCall(url, activityItems.type).submit();		
		}
	}

	/**
	 * Returns if the current use is the primary account holder.
	 */
	@Override
	protected boolean isUserPrimaryHolder(final int position) {
		// FIXME Need to know how to map object details to this value.
		return true;
	}

	/**
	 * Returns if the current fragment can be edited through user action, such as an 
	 * edit or delete button.
	 */
	@Override
	protected boolean isFragmentEditable(final int position) {
		return false;
	}

	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.ACCOUNT_SUMMARY_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.ACCOUNT_SUMMARY_SECTION;
	}

	@Override
	public void onBackPressed() {
		final BankNavigationRootActivity activity = 
				(BankNavigationRootActivity)DiscoverActivityManager.getActiveActivity();
		activity.getSupportFragmentManager().popBackStackImmediate();
		final BaseFragment fragment = activity.getCurrentContentFragment();
		if(fragment instanceof BankAccountActivityTable){
			((BankAccountActivityTable) fragment).loadDataFromBundle(getCurrentFragmentBundle());
		}
	}

	@Override
	public boolean isBackPressDisabled() {
		return true;
	}

}
