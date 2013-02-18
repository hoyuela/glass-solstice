package com.discover.mobile.bank.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.BankNavigator;
import com.discover.mobile.bank.DynamicDataFragment;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.payee.PayeeDetail;
import com.discover.mobile.bank.services.payment.ListPaymentDetail;
import com.discover.mobile.bank.services.payment.PaymentAccountDetail;
import com.discover.mobile.bank.services.payment.PaymentDateDetail;
import com.discover.mobile.bank.services.payment.PaymentDetail;

public class PaymentDetailsViewPager extends DetailViewPager implements FragmentOnBackPressed, DynamicDataFragment {
	private ListPaymentDetail detailList = new ListPaymentDetail();
	private int initialViewPosition = 0;
	
	@Override
	public int getActionBarTitle() {
		return R.string.payment_detail;
	}
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		loadBundleArgs(getArguments());
		
		if(savedInstanceState != null) {
			detailList = (ListPaymentDetail)savedInstanceState.getSerializable(BankExtraKeys.DATA_LIST);
		}
		

		if(detailList.payments == null)
			detailList.payments = new ArrayList<PaymentDetail>();

		if(!(detailList.payments.size() > 0)){

			for(int i = 0; i < 10; ++i){
				final PaymentDetail payment = new PaymentDetail();
				final Random rand = new Random();
				payment.payee = new PayeeDetail();
				payment.payee.name = "Someone who wants your money";
				payment.id = ""+rand.nextInt();
				payment.amount = rand.nextInt() % 99999;
				payment.description = "Some description";
				if(rand.nextInt() % 2 == 0)
					payment.status = "SCHEDULED";
				else
					payment.status = "COMPLETED";
				
				payment.paymentAccount = new PaymentAccountDetail();
				payment.paymentAccount.ending = "" + rand.nextInt() % 9999;
				payment.paymentAccount.nickName = "Cool Account Nickname";
				payment.dates = new HashMap<String, PaymentDateDetail>();
				PaymentDateDetail deliveredOn = new PaymentDateDetail();
				deliveredOn = getFormattedDate();
				
				payment.dates.put("deliveredOn", deliveredOn);
				payment.dates.put("deliverBy", deliveredOn);
				
				payment.confirmationNumber = "" + new Random().nextInt();
				
				if(new Random().nextInt() % 2 == 0)
					payment.memo = "A really cool memo duh!";
				
				detailList.payments.add(payment);
			}
		}
	}
	
	@Override
	public void onSaveInstanceState(final Bundle outState) {
		outState.putAll(getCurrentFragmentBundle());
	}
	
	private PaymentDateDetail getFormattedDate() {
		final Random rand = new Random();
		final PaymentDateDetail date = new PaymentDateDetail();
		date.formattedDate = (rand.nextInt() & 12) + "/" + (rand.nextInt() & 31) + "/" + (rand.nextInt() & 2000);
		return date;
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
	
	private Bundle getCurrentFragmentBundle() {
		Bundle currentBundle = getArguments();
		if(currentBundle == null)
			currentBundle = new Bundle();
		
		currentBundle.putInt(BankExtraKeys.DATA_SELECTED_INDEX, initialViewPosition);
		currentBundle.putSerializable(BankExtraKeys.DATA_LIST, detailList);
		return currentBundle;
	}
	
	@Override
	protected Fragment getDetailItem(final int position) {
		final PaymentDetailFragment paymentFragment = new PaymentDetailFragment();
		final PaymentDetail payment = detailList.payments.get(position);
		final Bundle bundle = new Bundle();
		bundle.putSerializable(BankExtraKeys.DATA_LIST_ITEM, payment);
		paymentFragment.setArguments(bundle);
		
		return paymentFragment;
	}

	@Override
	protected int getViewCount() {
		return detailList.payments.size();
	}

	@Override
	protected int getInitialViewPosition() {
		return initialViewPosition;
	}

	@Override
	protected int getTitleForFragment(final int position) {
		
		//Scheduled Payment or Completed Payment
		final String paymentStatus = detailList.payments.get(position).status;
		
		if("SCHEDULED".equals(paymentStatus))
			return R.string.scheduled_payment;
		else if ("COMPLETED".equals(paymentStatus))
			return R.string.completed_payment;
		else
			return R.string.payment_detail;
	}

	@Override
	public void handleReceivedData(final Bundle bundle) {
		final ListPaymentDetail newDetails = (ListPaymentDetail)bundle.getSerializable(BankExtraKeys.DATA_LIST);
		detailList.payments.addAll(newDetails.payments);
		updateNavigationButtons(getViewPager().getCurrentItem());
	}

	@Override
	public void onBackPressed() {
		BankNavigator.navigateToReviewPayments(getCurrentFragmentBundle(), true);
	}

	@Override
	protected void loadMore() {
		// TODO Auto-generated method stub
		
	}
	
}
