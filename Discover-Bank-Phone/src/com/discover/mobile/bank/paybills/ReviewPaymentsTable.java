package com.discover.mobile.bank.paybills;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.DynamicDataFragment;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.services.account.activity.ListActivityDetail;
import com.discover.mobile.bank.services.json.ReceivedUrl;
import com.discover.mobile.bank.services.payment.GetPaymentsServiceCall;
import com.discover.mobile.bank.services.payment.ListPaymentDetail;
import com.discover.mobile.bank.services.payment.PaymentDetail;
import com.discover.mobile.bank.services.payment.PaymentQueryType;
import com.discover.mobile.bank.ui.table.BaseTable;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;

/**
 * Table holding payments that the user can review.  It has the possible 
 * categories scheduled, completed and canceled.  When an items is clicked 
 * be sent the detail fragment. 
 * 
 * @author jthornton
 *
 */
public class ReviewPaymentsTable extends BaseTable implements DynamicDataFragment{

	/**List of completed payment details*/
	private ListPaymentDetail completed;

	/**List of scheduled payment details*/
	private ListPaymentDetail scheduled;

	/**List of canceled payment details*/
	private ListPaymentDetail canceled;

	/**Header for the list*/
	private ReviewPaymentsHeader header;

	/**Adapter to show data*/
	private ReviewPaymentsAdapter adapter;

	/**Default Key if it is used the toggle of buttons wont happen*/
	private static final int NO_CHANGE = -1;

	/**
	 * Handle the received data. Can be called to referesh the list data or in order to load more data.
	 * The keys read from the bundle are the following:
	 * 
	 * BankExtraKeys.CONFIRM_DELETE - Holds a boolean flag which is set to true if data has been deleted and list
	 *                                needs to be udpated with the data in PRIMARY_LIST.
	 * BankExtraKeys.PRIMARY_LIST - Holds the new data downloaded from server. This data will either be appended or
	 * 							    replace the existing data. The criteria used to determine whether to update or replace
	 * 								is the CONFIRM_DELETE passed in the bundle and whether the view currently has any data.
	 * @param bundle - bundle of received data
	 */
	@Override
	public void handleReceivedData(final Bundle bundle) {
		setIsLoadingMore(false);
		super.refreshListener();
		getLoadMoreFooter().showDone();
		int category = bundle.getInt(BankExtraKeys.CATEGORY_SELECTED, ReviewPaymentsHeader.SCHEDULED_PAYMENTS);
		final boolean isLoadingMore = bundle.getBoolean(BankExtraKeys.IS_LOADING_MORE);

		if(NO_CHANGE == category){
			category = header.getCurrentCategory();
		}else{
			header.setCurrentCategory(category);
		}

		final boolean dataDeleted = bundle.getBoolean(BankExtraKeys.CONFIRM_DELETE);
		ListPaymentDetail list = (ListPaymentDetail) bundle.getSerializable(BankExtraKeys.PRIMARY_LIST);
		if (list == null) {
			list = new ListPaymentDetail();
			list.payments = new ArrayList<PaymentDetail>();
			list.links = new HashMap<String, ReceivedUrl>();
		} 
		
		//Check what view the user is currently is in Scheduled, Completed or Cancelled.
		//The corresponding view list will be updated based on whether this method was called 
		//to load more data or refresh the list because of a deleted item.
		if (category == ReviewPaymentsHeader.SCHEDULED_PAYMENTS) {
			// Check whether the data has to be refreshed
			if (null == scheduled || dataDeleted || !isLoadingMore) {
				scheduled = list;
			} else {
				scheduled = handleReceivedData(scheduled, list);
			}

			updateAdapter(scheduled);
		} else if (category == ReviewPaymentsHeader.COMPLETED_PAYMENTS) {
			// Check whether the data has to be refreshed
			if (null == completed || dataDeleted || !isLoadingMore) {
				completed = list;
			} else {
				completed = handleReceivedData(completed, list);
			}
			updateAdapter(completed);
		} else {
			// Check whether the data has to be refreshed
			if (null == canceled || dataDeleted || !isLoadingMore) {
				canceled = list;
			} else {
				canceled = handleReceivedData(canceled, list);
			}
			updateAdapter(canceled);
		}

		//Show indication that a payment has been deleted under header
		if( dataDeleted ) {
			header.showStatusMessage();
		}

		//Check if after the new update there is any more payments transactions that can be loaded
		//otherwise disable the load more feature
		final ReceivedUrl url = getLoadMoreUrl();
		if(null == url){
			showNothingToLoad();
		}
	}

	/**
	 * Handle the received data from the service call
	 * @param list - list to update
	 * @param newList - newList of data
	 */
	public ListPaymentDetail handleReceivedData(final ListPaymentDetail list, final ListPaymentDetail newList){
		list.payments.addAll(newList.payments);
		list.links.clear();
		list.links.putAll(newList.links);
		return list;
	}



	/**
	 * @return the action bar title resource
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.review_payments_title;
	}

	/**
	 * Set up the adapter for this list
	 */
	@Override
	public void setupAdapter() {
		adapter = new ReviewPaymentsAdapter(this.getActivity(), R.layout.bank_table_item, this);
	}

	/**
	 * Setup the lists of details that are not already created
	 */
	@Override
	public void createDefaultLists() {
		//This does not need to be implemented with the current design of this class
	}


	/**
	 * Get the adapter that needs to be attached to the fragment.
	 * @param adatper - adapter to be attached to the list
	 */
	@Override
	public ArrayAdapter<?> getAdapter() {
		return adapter;
	}

	/**
	 * Method that is called when the adapter gets to the bottom of the list.  
	 * This will show the go to top or show the loading bar for most fragments.
	 */
	@Override
	public void maybeLoadMore() {
		final ReceivedUrl url = getLoadMoreUrl();
		if(null == url){
			showNothingToLoad();
			getLoadMoreFooter().showDone();
		}else{
			getLoadMoreFooter().showLoading();
			loadMore(url.url);
		}
	}

	/**
	 * Get the load more URL
	 * @return get the load more URL from the correct object
	 */
	private ReceivedUrl getLoadMoreUrl(){
		final int category = header.getCurrentCategory();
		if(category == ReviewPaymentsHeader.SCHEDULED_PAYMENTS && scheduled != null){
			return scheduled.links.get(ListActivityDetail.NEXT);
		}else if(category == ReviewPaymentsHeader.COMPLETED_PAYMENTS  && completed != null){
			return completed.links.get(ListActivityDetail.NEXT);
		}else if (canceled != null){
			return canceled.links.get(ListActivityDetail.NEXT);
		}
		return null;
	}

	/**
	 * Load more activities
	 */
	public void loadMore(final String url){
		setIsLoadingMore(true);
		final Bundle bundle = new Bundle();
		bundle.putInt(BankExtraKeys.CATEGORY_SELECTED, NO_CHANGE);
		bundle.putBoolean(BankExtraKeys.IS_LOADING_MORE, true);
		final GetPaymentsServiceCall call = BankServiceCallFactory.createGetPaymentsServerCall(url);
		call.setExtras(bundle);
		call.submit();
	}

	/**
	 * Set up the header
	 */
	@Override
	public void setupHeader() {
		header = new ReviewPaymentsHeader(this.getActivity(), null);

		header.getScheduled().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				setupScheduledList();
			}
		});

		header.getCompleted().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				setupCompletedList();
			}
		});

		header.getCanceled().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				setupCancelledList();
			}
		});
	}

	private void setupScheduledList() {
		if (scheduled != null) {
			header.setCurrentCategory(ReviewPaymentsHeader.SCHEDULED_PAYMENTS);
			updateAdapter(scheduled);
		} else if (BankUser.instance().getScheduled() != null) {
			header.setCurrentCategory(ReviewPaymentsHeader.SCHEDULED_PAYMENTS);
			scheduled = BankUser.instance().getScheduled();
			updateAdapter(scheduled);
		} else {
			//Generate a url to download schedule payments
			final Bundle bundle = new Bundle();
			bundle.putInt(BankExtraKeys.CATEGORY_SELECTED,  ReviewPaymentsHeader.SCHEDULED_PAYMENTS);
			final String url = BankUrlManager.generateGetPaymentsUrl(PaymentQueryType.SCHEDULED);
			final GetPaymentsServiceCall call = BankServiceCallFactory.createGetPaymentsServerCall(url);
			call.setExtras(bundle);
			call.submit();
		}
	}
	
	private void setupCompletedList() {
		if (completed != null) {
			header.setCurrentCategory(ReviewPaymentsHeader.COMPLETED_PAYMENTS);	
			updateAdapter(completed);
		} else if (BankUser.instance().getCompleted() != null) {
			header.setCurrentCategory(ReviewPaymentsHeader.COMPLETED_PAYMENTS);	
			completed = BankUser.instance().getCompleted();
			updateAdapter(completed);
		} else {
			//Generate a url to download schedule payments
			final Bundle bundle = new Bundle();
			bundle.putInt(BankExtraKeys.CATEGORY_SELECTED,  ReviewPaymentsHeader.COMPLETED_PAYMENTS);
			final String url = BankUrlManager.generateGetPaymentsUrl(PaymentQueryType.COMPLETED);
			final GetPaymentsServiceCall call = BankServiceCallFactory.createGetPaymentsServerCall(url);
			call.setExtras(bundle);
			call.submit();
		}
	}
	
	private void setupCancelledList() {
		if (canceled != null) {
			header.setCurrentCategory(ReviewPaymentsHeader.CANCELED_PAYMENTS);
			updateAdapter(canceled);
		} else if (BankUser.instance().getCancelled() != null) {
			header.setCurrentCategory(ReviewPaymentsHeader.CANCELED_PAYMENTS);
			canceled = BankUser.instance().getCancelled();
			updateAdapter(canceled);
		} else {
			//Generate a url to download schedule payments
			final Bundle bundle = new Bundle();
			bundle.putInt(BankExtraKeys.CATEGORY_SELECTED,  ReviewPaymentsHeader.CANCELED_PAYMENTS);
			final String url = BankUrlManager.generateGetPaymentsUrl(PaymentQueryType.CANCELLED);
			final GetPaymentsServiceCall call = BankServiceCallFactory.createGetPaymentsServerCall(url);
			call.setExtras(bundle);
			call.submit();
		}
	}
	@Override
	public void onResume(){
		super.onResume();

		header.requestLayout();
	}

	/**
	 * Method used to see if a data update is required. This method will check to see what category (Scheduled,
	 * Cancelled, or Completed) is currently being displayed to the user and based on whether or not the current cache
	 * has values will decide whether a new data download is required.
	 * 
	 * @return True if cache manager has cached data for the current category displayed, false otherwise.
	 */
	@Override
	protected boolean isDataUpdateRequired() {
		final int category = header.getCurrentCategory();
		boolean isUpdateRequired = false;
		
		/**Set the resource identifier that should be displayed in the details screen*/
		switch( category ) {
		case ReviewPaymentsHeader.SCHEDULED_PAYMENTS:
			isUpdateRequired = (BankUser.instance().getScheduled() == null || scheduled == null);
			break;
		case ReviewPaymentsHeader.COMPLETED_PAYMENTS:
			isUpdateRequired = (BankUser.instance().getCompleted() == null || completed == null);
			break;
		case ReviewPaymentsHeader.CANCELED_PAYMENTS:
			isUpdateRequired = (BankUser.instance().getCancelled() == null || canceled == null);
			break;
		}
		
		return isUpdateRequired;
	}
	
	/**
	 * Method used to send a request to update the current data being displayed. The request made will depend on whether
	 * Scheduled, Completed or Cancelled category is being displayed.
	 */
	@Override
	protected void updateData() {
		final int category = header.getCurrentCategory();
				
		/**Set the resource identifier that should be displayed in the details screen*/
		switch( category ) {
		case ReviewPaymentsHeader.SCHEDULED_PAYMENTS:
			scheduled = null;
			setupScheduledList();
			break;
		case ReviewPaymentsHeader.COMPLETED_PAYMENTS:
			completed = null;
			setupCompletedList();
			break;
		case ReviewPaymentsHeader.CANCELED_PAYMENTS:
			canceled = null;
			setupCancelledList();
			break;
		}		
	}

	/**
	 * Set up the footer
	 */
	@Override
	public void setupFooter() {
		getLoadMoreFooter().getGo().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v){
				scrollToTop();
			}
		});
		getLoadMoreFooter().showDone();
	}

	/**
	 * Get the header that should be shown at the top of the list.
	 */
	@Override
	public View getHeader() {
		return header;
	}

	/**
	 * Get the footer that should be shown at the top of the list.
	 */
	@Override
	public View getFooter() {
		return getLoadMoreFooter();
	}

	/**
	 * Go to the details screen associated with this view
	 * @param index - index to pass to the detail screen
	 */
	@Override
	public void goToDetailsScreen(final int index) {
		final Bundle bundle = new Bundle();
		final int category = header.getCurrentCategory();
		bundle.putInt(BankExtraKeys.CATEGORY_SELECTED, category);

		/**Set the resource identifier that should be displayed in the details screen*/
		switch( category ) {
		case ReviewPaymentsHeader.SCHEDULED_PAYMENTS:
			bundle.putInt(BankExtraKeys.TITLE_TEXT, R.string.scheduled_payment);
			break;
		case ReviewPaymentsHeader.COMPLETED_PAYMENTS:
			bundle.putInt(BankExtraKeys.TITLE_TEXT,R.string.completed_payment);
			break;
		case ReviewPaymentsHeader.CANCELED_PAYMENTS:
			bundle.putInt(BankExtraKeys.TITLE_TEXT,R.string.cancelled_payment);
			break;
		}

		bundle.putSerializable(getScheduleKey(category), scheduled);
		bundle.putSerializable(getCompletedKey(category), completed);
		bundle.putSerializable(getCanceledKey(category), canceled);
		bundle.putSerializable(BankExtraKeys.DATA_SELECTED_INDEX, index);
		bundle.putBoolean(BankExtraKeys.IS_LOADING_MORE, getIsLoadingMore());
		BankConductor.navigateToPaymentDetailScreen(bundle);
	}

	/**
	 * Save all the data on the screen in a bundle
	 * @return bundle containing all the data
	 */
	@Override
	public Bundle saveDataInBundle() {
		final Bundle bundle  = new Bundle();
		if(null == header){return bundle;}
		final int category = header.getCurrentCategory();
		bundle.putInt(BankExtraKeys.CATEGORY_SELECTED, category);
		bundle.putSerializable(getScheduleKey(category), scheduled);
		bundle.putSerializable(getCompletedKey(category), completed);
		bundle.putSerializable(getCanceledKey(category), canceled);

		return bundle;
	}

	/**
	 * Load the data from the bundle
	 * @param bundle - bundle to load the data from
	 */
	@Override
	public void loadDataFromBundle(final Bundle bundle) {
		if(null == bundle){return;}
		final int category = bundle.getInt(BankExtraKeys.CATEGORY_SELECTED, ReviewPaymentsHeader.SCHEDULED_PAYMENTS);
		header.setCurrentCategory(category);
		scheduled = (ListPaymentDetail)bundle.getSerializable(getScheduleKey(category));
		completed = (ListPaymentDetail)bundle.getSerializable(getCompletedKey(category));
		canceled = (ListPaymentDetail)bundle.getSerializable(getCanceledKey(category));
		createDefaultLists();	

		if(category == ReviewPaymentsHeader.SCHEDULED_PAYMENTS && scheduled != null){
			this.updateAdapter(scheduled);
		}else if(category == ReviewPaymentsHeader.COMPLETED_PAYMENTS && completed != null){
			this.updateAdapter(completed);
		}else if (canceled != null){
			this.updateAdapter(canceled);
		}
	}

	/**
	 * @return the schedule key that will be used in the bundle
	 */
	private String getScheduleKey(final int category){
		return (category == ReviewPaymentsHeader.SCHEDULED_PAYMENTS) ? 
				BankExtraKeys.PRIMARY_LIST : BankExtraKeys.SCHEDULED_LIST;
	}

	/**
	 * @return the completed key that will be used in the bundle
	 */
	private String getCompletedKey(final int category){
		return (category == ReviewPaymentsHeader.COMPLETED_PAYMENTS) ? 
				BankExtraKeys.PRIMARY_LIST : BankExtraKeys.COMPLETED_LIST;
	}

	/**
	 * @return the canceled key that will be used in the bundle
	 */
	private String getCanceledKey(final int category){
		return (category == ReviewPaymentsHeader.CANCELED_PAYMENTS) ? 
				BankExtraKeys.PRIMARY_LIST : BankExtraKeys.CANCELED_LIST;
	}

	/**
	 * Update the adapter
	 * @param list - activities to update the adapter with
	 */
	public void updateAdapter(final ListPaymentDetail list){
		adapter.clear();
		adapter.setData(list.payments);
		if(adapter.getCount() < 1){
			header.setMessage(this.getEmptyStringText());
			showNothingToLoad();
			getLoadMoreFooter().hideAll();
		}else{
			getTable().setMode(Mode.PULL_FROM_END);
			header.clearMessage();
			getLoadMoreFooter().showDone();
		}

		final ReceivedUrl url = getLoadMoreUrl();
		if(null == url){
			showNothingToLoad();
		}
		adapter.notifyDataSetChanged();
	}

	/**
	 * Get the string that should be shown in the empty list view
	 * @return the string that should be show in the empty list view
	 */
	public String getEmptyStringText(){
		final int category = header.getCurrentCategory();
		if(category == ReviewPaymentsHeader.SCHEDULED_PAYMENTS){
			return getResources().getString(R.string.review_payments_scheduled_payments_empty);
		}else if(category == ReviewPaymentsHeader.COMPLETED_PAYMENTS){
			return getResources().getString(R.string.review_payments_completed_payments_empty);
		}else{
			return getResources().getString(R.string.review_payments_canceled_payments_empty);
		}
	}

	/**
	 * Show the empty message in the footer
	 * @return 
	 */
	@Override
	public void showFooterMessage() {
		header.setMessage(getEmptyStringText());
	}

	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.PAY_BILLS_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.REVIEW_PAYEMENTS_SECTION;
	}
}
