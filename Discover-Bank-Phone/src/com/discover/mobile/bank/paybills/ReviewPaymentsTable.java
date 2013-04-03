package com.discover.mobile.bank.paybills;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.BankRotationHelper;
import com.discover.mobile.bank.DynamicDataFragment;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.help.HelpMenuListFactory;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.services.account.activity.ListActivityDetail;
import com.discover.mobile.bank.services.payment.ListPaymentDetail;
import com.discover.mobile.bank.services.payment.PaymentDetail;
import com.discover.mobile.bank.services.payment.PaymentQueryType;
import com.discover.mobile.bank.ui.table.BaseTable;
import com.discover.mobile.common.net.json.bank.ReceivedUrl;
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

	/**
	 * Handle the received data.
	 * @param bundle - bundle of received data
	 */
	@Override
	public void handleReceivedData(final Bundle bundle) {
		setIsLoadingMore(false);
		super.refreshListener();
		footer.showDone();
		final int category = header.getCurrentCategory();
		final ListPaymentDetail list = (ListPaymentDetail) bundle.getSerializable(BankExtraKeys.PRIMARY_LIST);
		if(category == ReviewPaymentsHeader.SCHEDULED_PAYMENTS){
			scheduled = (null == scheduled) ? list : handleReceivedData(scheduled, list);
			updateAdapter(scheduled.payments);
		}else if(category == ReviewPaymentsHeader.COMPLETED_PAYMENTS){
			completed = (null == completed) ? list : handleReceivedData(completed, list);
			updateAdapter(completed.payments);
		}else{
			canceled = (null == canceled) ? list : handleReceivedData(canceled, list);
			updateAdapter(canceled.payments);
		}
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
			footer.showDone();
		}else{
			footer.showLoading();
			loadMore(url.url);
		}
	}

	/**
	 * Get the load more URL
	 * @return get the load more URL from the correct object
	 */
	private ReceivedUrl getLoadMoreUrl(){
		final int category = header.getCurrentCategory();
		if(category == ReviewPaymentsHeader.SCHEDULED_PAYMENTS){
			return scheduled.links.get(ListActivityDetail.NEXT);
		}else if(category == ReviewPaymentsHeader.COMPLETED_PAYMENTS){
			return completed.links.get(ListActivityDetail.NEXT);
		}else{
			return canceled.links.get(ListActivityDetail.NEXT);
		}
	}

	/**
	 * Load more activities
	 */
	public void loadMore(final String url){
		setIsLoadingMore(true);
		BankServiceCallFactory.createGetPaymentsServerCall(url).submit();
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
				header.setCurrentCategory(ReviewPaymentsHeader.SCHEDULED_PAYMENTS);
				if(null == scheduled){
					//Generate a url to download schedule payments
					final String url = BankUrlManager.generateGetPaymentsUrl(PaymentQueryType.SCHEDULED);
					BankServiceCallFactory.createGetPaymentsServerCall(url).submit();
				}else{
					updateAdapter(scheduled.payments);
				}
			}
		});

		header.getCompleted().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				header.setCurrentCategory(ReviewPaymentsHeader.COMPLETED_PAYMENTS);	
				if(null == completed){
					//Generate a url to download completed payments
					final String url = BankUrlManager.generateGetPaymentsUrl(PaymentQueryType.COMPLETED);
					BankServiceCallFactory.createGetPaymentsServerCall(url).submit();
				}else{
					updateAdapter(completed.payments);
				}
			}
		});

		header.getCanceled().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				header.setCurrentCategory(ReviewPaymentsHeader.CANCELED_PAYMENTS);
				if(null == canceled){
					//Generate a url to download cancelled payments
					final String url = BankUrlManager.generateGetPaymentsUrl(PaymentQueryType.CANCELLED);
					BankServiceCallFactory.createGetPaymentsServerCall(url).submit();
				}else{
					updateAdapter(canceled.payments);
				}
			}
		});
	}

	@Override
	public void onResume(){
		super.onResume();
		header.getHelp().showHelpItems(HelpMenuListFactory.instance().getPayBillsHelpItems());
		header.requestLayout();
	}

	/**
	 * Set up the footer
	 */
	@Override
	public void setupFooter() {
		footer.getGo().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v){
				scrollToTop();
			}
		});
		footer.showDone();
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
		return footer;
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
		final String scheduleKey = 
				(category == ReviewPaymentsHeader.SCHEDULED_PAYMENTS) ? BankExtraKeys.PRIMARY_LIST : BankExtraKeys.SCHEDULED_LIST;
		final String completedKey =
				(category == ReviewPaymentsHeader.COMPLETED_PAYMENTS) ? BankExtraKeys.PRIMARY_LIST : BankExtraKeys.COMPLETED_LIST;
		final String canceledKey =
				(category == ReviewPaymentsHeader.CANCELED_PAYMENTS) ? BankExtraKeys.PRIMARY_LIST : BankExtraKeys.CANCELED_LIST;

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

		bundle.putSerializable(scheduleKey, scheduled);
		bundle.putSerializable(completedKey, completed);
		bundle.putSerializable(canceledKey, canceled);
		bundle.putSerializable(BankExtraKeys.DATA_SELECTED_INDEX, index);
		bundle.putBoolean(BankExtraKeys.IS_LOADING_MORE, getIsLoadingMore());
		BankRotationHelper.getHelper().setBundle(bundle);
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
		final String scheduleKey = 
				(category == ReviewPaymentsHeader.SCHEDULED_PAYMENTS) ? BankExtraKeys.PRIMARY_LIST : BankExtraKeys.SCHEDULED_LIST;
		final String completedKey =
				(category == ReviewPaymentsHeader.COMPLETED_PAYMENTS) ? BankExtraKeys.PRIMARY_LIST : BankExtraKeys.COMPLETED_LIST;
		final String canceledKey =
				(category == ReviewPaymentsHeader.CANCELED_PAYMENTS) ? BankExtraKeys.PRIMARY_LIST : BankExtraKeys.CANCELED_LIST;

		bundle.putSerializable(scheduleKey, scheduled);
		bundle.putSerializable(completedKey, completed);
		bundle.putSerializable(canceledKey, canceled);
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

		final boolean showStatus = bundle.getBoolean(BankExtraKeys.CONFIRM_DELETE, false);
		if(showStatus){
			header.showStatusMessage();
			bundle.putBoolean(BankExtraKeys.COMPLETED_LIST, false);
			scheduled.payments.remove(bundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM));
		}

		if(category == ReviewPaymentsHeader.SCHEDULED_PAYMENTS){
			this.updateAdapter(scheduled.payments);
		}else if(category == ReviewPaymentsHeader.COMPLETED_PAYMENTS){
			this.updateAdapter(completed.payments);
		}else{
			this.updateAdapter(canceled.payments);
		}

		final ReceivedUrl url = getLoadMoreUrl();
		if(null == url){
			showNothingToLoad();
		}
	}

	/**
	 * @return the schedule key that will be used in the bundle
	 */
	private String getScheduleKey(final int category){
		return (category == ReviewPaymentsHeader.SCHEDULED_PAYMENTS) ? BankExtraKeys.PRIMARY_LIST : BankExtraKeys.SCHEDULED_LIST;
	}

	/**
	 * @return the completed key that will be used in the bundle
	 */
	private String getCompletedKey(final int category){
		return (category == ReviewPaymentsHeader.COMPLETED_PAYMENTS) ? BankExtraKeys.PRIMARY_LIST : BankExtraKeys.COMPLETED_LIST;
	}

	/**
	 * @return the canceled key that will be used in the bundle
	 */
	private String getCanceledKey(final int category){
		return (category == ReviewPaymentsHeader.CANCELED_PAYMENTS) ? BankExtraKeys.PRIMARY_LIST : BankExtraKeys.CANCELED_LIST;
	}

	/**
	 * Update the adapter
	 * @param activities - activities to update the adapter with
	 */
	public void updateAdapter(final List<PaymentDetail> activities){

		adapter.clear();
		adapter.setData(activities);
		if(adapter.getCount() < 1){
			header.setMessage(this.getEmptyStringText());
			showNothingToLoad();
			footer.hideAll();
		}else{
			table.setMode(Mode.PULL_FROM_END);
			header.clearMessage();
			footer.showDone();
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
