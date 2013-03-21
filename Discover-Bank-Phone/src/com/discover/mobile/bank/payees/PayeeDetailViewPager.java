package com.discover.mobile.bank.payees;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.payee.ListPayeeDetail;
import com.discover.mobile.bank.services.payee.PayeeDetail;
import com.discover.mobile.bank.ui.widgets.DetailViewPager;

public class PayeeDetailViewPager extends DetailViewPager {
	private ListPayeeDetail detailList = new ListPayeeDetail();
	private int initialViewPosition = 0;

	@Override
	public int getActionBarTitle() {
		return R.string.payee_details;
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
			detailList = (ListPayeeDetail)savedInstanceState.getSerializable(BankExtraKeys.PAYEES_LIST);
			initialViewPosition = savedInstanceState.getInt(BankExtraKeys.SELECTED_PAYEE);
		}

	}

	/**
	 * Save the current Bundle state so that we can restore it on rotation change.
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState) {
		outState.putAll(getCurrentFragmentBundle());

	}

	/**
	 * Gets the list of ActivityDetail objects from a bundle along with the current selected position in that list
	 * to show.
	 * @param bundle a Bundle that contains a ListActivityDetail object and an integer representing the current
	 * 			visible item.
	 */
	public void loadBundleArgs(final Bundle bundle) {
		if(bundle != null){
			detailList = (ListPayeeDetail)bundle.getSerializable(BankExtraKeys.PAYEES_LIST);
			initialViewPosition = bundle.getInt(BankExtraKeys.SELECTED_PAYEE);
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

		currentBundle.putInt(BankExtraKeys.SELECTED_PAYEE, initialViewPosition);
		currentBundle.putSerializable(BankExtraKeys.PAYEES_LIST, detailList);
		return currentBundle;
	}

	/**
	 * The DetailViewPager will call this method to setup the first visible Fragment.
	 */
	@Override
	protected int getInitialViewPosition() {
		return initialViewPosition;
	}


	// FIXME need to have services to determine if the current user is the primary account holder
	@Override
	protected boolean isUserPrimaryHolder(final int position) {
		return true;
	}

	@Override
	public void handleReceivedData(final Bundle bundle) {
		// does not support load more.
	}

	/**
	 * Get a PayeeDetailFragment to show in the ViewPager.
	 */
	@Override
	protected Fragment getDetailItem(final int position) {
		final PayeeDetailFragment payeeDetail = new PayeeDetailFragment();
		final PayeeDetail detailObject = detailList.payees.get(position);

		final Bundle bundle = new Bundle();
		bundle.putSerializable(BankExtraKeys.SELECTED_PAYEE, detailObject);
		payeeDetail.setArguments(bundle);

		return payeeDetail;
	}

	@Override
	protected int getViewCount() {
		return detailList.payees.size();
	}

	@Override
	protected int getTitleForFragment(final int position) {
		return 0;
	}

	@Override
	protected boolean isFragmentEditable(final int position) {
		return false;
	}

	@Override
	protected void loadMore(final String url) {
		//unsupported for payees

	}

	@Override
	protected void loadMoreIfNeeded(final int position) {
		//unsupported for payees
	}

	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.PAY_BILLS_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.MANAGE_PAYEES_SECTION;
	}

	@Override
	public boolean isBackPressDisabled() {
		return false;
	}
}
