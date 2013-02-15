package com.discover.mobile.bank.account;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.BankRotationHelper;
import com.discover.mobile.bank.BankServiceCallFactory;
import com.discover.mobile.bank.DynamicDataFragment;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.account.activity.ActivityDetail;
import com.discover.mobile.bank.services.account.activity.ListActivityDetail;

/**
 * This is a subclass of the DetailView pager.
 * The purpose of any subclass of the DetailViewPager is to handle any functionality that
 * is specific to where the ViewPager is needed. This class specifically will handle
 * passing data to and from the previous Fragment (account transactions list) and providing
 * assembled Fragments to the ViewPager to show.
 * 
 * @author scottseward
 */
public class AccountActivityViewPager extends DetailViewPager implements DynamicDataFragment, FragmentOnBackPressed{
	private ListActivityDetail activityItems = null;
	private int initialViewPosition = 0;

	/**
	 * Save the state of the ViewPager for rotation.
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState) {
		outState.putAll(getCurrentFragmentBundle());
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

		currentBundle.putInt(BankExtraKeys.DATA_SELECTED_INDEX, initialViewPosition);
		currentBundle.putSerializable(BankExtraKeys.DATA_LIST, activityItems);
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
			activityItems = (ListActivityDetail)savedInstanceState.getSerializable(BankExtraKeys.DATA_LIST);
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
			activityItems = (ListActivityDetail)bundle.getSerializable(BankExtraKeys.DATA_LIST);
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
		}

		return viewCount;
	}

	/**
	 * Returns a Fragment to show in the ViewPager.
	 * @return a Fragment to show in the ViewPager.
	 */
	@Override
	protected Fragment getDetailItem(final int position) {
		final ActivityDetailFragment temp = new ActivityDetailFragment();
		final ActivityDetail detailObject = activityItems.activities.get(position);

		final Bundle bundle = new Bundle();
		bundle.putSerializable(BankExtraKeys.DATA_LIST_ITEM, detailObject);
		temp.setArguments(bundle);

		return temp;
	}

	/**
	 * Instead of simply trashing the current Fragment onBackPressed, we need to pass the current Bundle back to
	 * the AccountActivityPage so that if we loaded any new information, it will be shown in the AccountActivityPage.
	 */
	@Override
	public void onBackPressed() {
		BankRotationHelper.getHelper().setBundle(getCurrentFragmentBundle());
	}

	/**
	 * What to do after we load more data from the server.
	 */
	@Override
	public void handleReceivedData(final Bundle bundle) {
		final ListActivityDetail list = (ListActivityDetail)bundle.getSerializable(BankExtraKeys.DATA_LIST);
		activityItems.activities.addAll( list.activities);
		updateNavigationButtons(getViewPager().getCurrentItem());

	}

	@Override
	protected int getTitleForFragment(final int position) {
		return R.string.transaction;
	}

	@Override
	protected void loadMore() {
		BankServiceCallFactory.createGetActivityServerCall("/api/accounts/1/activity?status=posted").submit();		
	}

	@Override
	protected boolean isUserPrimaryHolder() {
		// FIXME Need to know how to map object details to this value.
		return true;
	}

	@Override
	protected boolean isFragmentEditable(final int position) {
		return false;
	}

}
