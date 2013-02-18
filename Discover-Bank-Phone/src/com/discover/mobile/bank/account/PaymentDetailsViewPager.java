package com.discover.mobile.bank.account;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.BankNavigator;
import com.discover.mobile.bank.DynamicDataFragment;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.payment.ListPaymentDetail;
import com.discover.mobile.bank.services.payment.PaymentDetail;

public class PaymentDetailsViewPager extends DetailViewPager implements FragmentOnBackPressed, DynamicDataFragment {
	private ListPaymentDetail detailList = new ListPaymentDetail();
	private int initialViewPosition = 0;
	
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
			detailList = (ListPaymentDetail)savedInstanceState.getSerializable(BankExtraKeys.DATA_LIST);
		}
		
		//Make sure the list is not null so that the Fragment will not crash upon getting no data.
		if(detailList.payments == null)
			detailList.payments = new ArrayList<PaymentDetail>();
		
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
			detailList = (ListPaymentDetail)bundle.getSerializable(BankExtraKeys.DATA_LIST);
			initialViewPosition = bundle.getInt(BankExtraKeys.DATA_SELECTED_INDEX);
		}
	}
	
	/**
	 * Get the current Bundle for this Fragment, then update the data list and index fields on it
	 * and return the updated Bundle
	 * @return an up to date Bundle with the current information of the ViewPager.
	 */
	private Bundle getCurrentFragmentBundle() {
		Bundle currentBundle = getArguments();
		if(currentBundle == null)
			currentBundle = new Bundle();
		
		currentBundle.putInt(BankExtraKeys.DATA_SELECTED_INDEX, initialViewPosition);
		currentBundle.putSerializable(BankExtraKeys.DATA_LIST, detailList);
		return currentBundle;
	}
	
	/**
	 * Called by the DetailViewPager class to retrieve a constructed PaymentDetail Fragment.
	 * This method returns a payment Fragment that is ready to be shown in the DetailViewPager.
	 */
	@Override
	protected Fragment getDetailItem(final int position) {
		final PaymentDetailFragment paymentFragment = new PaymentDetailFragment();
		final PaymentDetail payment = detailList.payments.get(position);
		final Bundle bundle = new Bundle();
		bundle.putSerializable(BankExtraKeys.DATA_LIST_ITEM, payment);
		paymentFragment.setArguments(bundle);
		
		return paymentFragment;
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
		//Scheduled Payment or Completed Payment
			final String paymentStatus = detailList.payments.get(position).status;
			
			if("SCHEDULED".equals(paymentStatus))
				return R.string.scheduled_payment;
			else if ("COMPLETED".equals(paymentStatus))
				return R.string.completed_payment;
			else if ("CANCELLED".equals(paymentStatus))
				return R.string.cancelled_payment;
			else
				return R.string.payment_detail;
		}
		else 
			return R.string.no_data_found;
	}
	
// FIXME need to have services to determine if the current user is the primary account holder
	@Override
	protected boolean isUserPrimaryHolder() {
		return true;
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
		final ListPaymentDetail newDetails = (ListPaymentDetail)bundle.getSerializable(BankExtraKeys.DATA_LIST);
		detailList.payments.addAll(newDetails.payments);
		updateNavigationButtons(getViewPager().getCurrentItem());
	}

	/**
	 * This method is called when the back button is pressed on this Fragment.
	 */
	// FIXME need to have a navigator method defined that allows navigating back to the view payments Fragment.
	@Override
	public void onBackPressed() {
		BankNavigator.navigateToReviewPayments(getCurrentFragmentBundle(), true);
	}

	/**
	 * This method is responsible for initiating a service call to load more payments.
	 */
	@Override
	protected void loadMore() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * This method determines if the current Fragment is editable, and will show or hide the
	 * delete and edit buttons based on that.
	 * If the payment is not scheduled or the current user is not the account holder then it cannot be edited.
	 */
	@Override
	protected boolean isFragmentEditable(final int position) {
		boolean paymentIsEditable = false;
		if(detailList.payments.size() > 0)
			 paymentIsEditable = "SCHEDULED".equals(detailList.payments.get(position).status);

		return paymentIsEditable;
	}
	
}
