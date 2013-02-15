package com.discover.mobile.bank.paybills;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;

import com.discover.mobile.bank.DynamicDataFragment;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.account.TableLoadMoreFooter;
import com.discover.mobile.bank.services.payment.ListPaymentDetail;
import com.discover.mobile.bank.ui.table.BaseTable;

public class ReviewPaymentsTable extends BaseTable implements DynamicDataFragment{

	private ListPaymentDetail completed;

	private ListPaymentDetail scheduled;

	private ListPaymentDetail canceled;

	private ReviewPaymentsHeader header;

	/**Footer to put in the bottom of the list view*/
	private TableLoadMoreFooter footer;

	private ReviewPaymentsAdatper adapter;


	@Override
	public void handleReceivedData(final Bundle bundle) {
		// TODO Auto-generated method stub

	}


	@Override
	public int getActionBarTitle() {
		return R.string.review_payments_title;
	}


	@Override
	public void setupAdapter() {
		adapter = new ReviewPaymentsAdatper(this.getActivity(), R.layout.bank_table_item, scheduled.payments, this);

	}


	@Override
	public void createDefaultLists() {
		// TODO Auto-generated method stub

	}


	@Override
	public ArrayAdapter<?> getAdapter() {
		return adapter;
	}


	@Override
	public void maybeLoadMore() {
		// TODO Auto-generated method stub

	}


	@Override
	public void setupHeader() {
		header = new ReviewPaymentsHeader(this.getActivity(), null);

	}


	@Override
	public void setupFooter() {
		footer = new TableLoadMoreFooter(this.getActivity(), null);
		footer.getGo().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v){
				scrollToTop();
			}
		});
	}


	@Override
	public View getHeader() {
		return header;
	}


	@Override
	public View getFooter() {
		return footer;
	}


	@Override
	public void goToDetailsScreen(final int index) {
		// TODO Auto-generated method stub

	}


	@Override
	public Bundle saveDataInBundle() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void loadDataFromBundle(final Bundle bundle) {
		// TODO Auto-generated method stub

	}
}
