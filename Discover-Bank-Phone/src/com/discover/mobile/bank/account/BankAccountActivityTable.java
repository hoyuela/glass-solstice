package com.discover.mobile.bank.account;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.account.activity.ActivityDetail;
import com.discover.mobile.bank.services.account.activity.ActivityDetailType;
import com.discover.mobile.bank.services.account.activity.ListActivityDetail;
import com.discover.mobile.bank.services.json.ReceivedUrl;
import com.discover.mobile.bank.ui.table.BaseTable;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;

/**
 * View that allows the user to view posted and scheduled activities for an account
 * @author jthornton
 *
 */
public class BankAccountActivityTable extends BaseTable{

	/**List of posted activities in the table*/
	private ListActivityDetail posted;

	/**List of scheduled activities in the table*/
	private ListActivityDetail scheduled;

	/**Adapter used to display data*/
	private BankListAdapter adapter;

	/**Title view of the page*/
	private AccountActivityHeader header;

	/**
	 * Handle the received data from the service call
	 * @param bundle - bundle received from the service call
	 */
	@Override
	public void handleReceivedData(final Bundle bundle) {
		setIsLoadingMore(false);
		super.refreshListener();
		getLoadMoreFooter().showDone();
		final ListActivityDetail list = (ListActivityDetail) bundle.getSerializable(BankExtraKeys.PRIMARY_LIST);

		// Toggle between scheduled/posted if incoming list type does not match view
		if ((list.type == ActivityDetailType.Posted && !header.isPosted())
				|| (list.type == ActivityDetailType.Scheduled && header.isPosted())) {
			header.setSelectedCategory(!header.isPosted());
		}

		if(header.isPosted()){
			updateListForPostedData(list);
		}else{
			updateListForScheduledData(list);
		}
		final ReceivedUrl url = getLoadMoreUrl();
		if(null == url){
			showNothingToLoad();
		}
		
	}
	
	/**
	 * Update the list to show scheduled activity.
	 * @param list
	 */
	private void updateListForPostedData(final ListActivityDetail list) {
		if(null == posted){
			posted = new ListActivityDetail();
			posted.activities  = new ArrayList<ActivityDetail>();
			posted.links = new HashMap<String, ReceivedUrl>();
		}
		handleReceivedData(posted, list);
	}
	
	/**
	 * Update the list to show scheduled activity.
	 * @param list
	 */
	private void updateListForScheduledData(final ListActivityDetail list) {
		if(null == scheduled){
			scheduled = new ListActivityDetail();
			scheduled.activities  = new ArrayList<ActivityDetail>();
			scheduled.links = new HashMap<String, ReceivedUrl>();
		}
		handleReceivedData(scheduled, list);
	}

	/**
	 * Handle the received data from the service call
	 * @param list - list to update
	 * @param newList - newList of data
	 */
	public void handleReceivedData(final ListActivityDetail list, final ListActivityDetail newList){
		list.activities.addAll(newList.activities);
		list.links.clear();
		list.links.putAll(newList.links);
		updateAdapter(list);
	}

	/**
	 * Update the adapter
	 * @param current - activities to update the adapter with
	 */
	public void updateAdapter(final ListActivityDetail current){
		if(current != null && current.activities != null) {
			adapter.setData(current.activities);
			
			updateBankUserCache(current);
			
			if(adapter.getCount() < 1){
				header.setMessage(this.getEmptyStringText());
				showNothingToLoad();
				getLoadMoreFooter().hideAll();
			} else {
				getTable().setMode(Mode.PULL_FROM_END);
				header.clearMessage();
				getLoadMoreFooter().showDone();
			}
			adapter.notifyDataSetChanged();
			final ReceivedUrl url = getLoadMoreUrl();
			if(null == url){
				showNothingToLoad();
			}
		}
	}
	
	/**
	 * Sets the bank user cache for posted or scheduled activity based on the state of the header.
	 * @param list a ListActivityDetail object to save to the BankUser cache.
	 */
	private void updateBankUserCache(final ListActivityDetail list) {
		if(null == BankUser.instance().getCurrentAccount().posted && header.isPosted()){
			BankUser.instance().getCurrentAccount().posted = list;
		} else if(null == BankUser.instance().getCurrentAccount().scheduled && !header.isPosted()){
			BankUser.instance().getCurrentAccount().scheduled = list;
		}
	}

	/**
	 * @return the title to be displayed in the action bar
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.activity_screen_title;
	}

	/**
	 * Get the string that should be shown in the empty list view
	 * @return the string that should be show in the empty list view
	 */
	public String getEmptyStringText(){
		return getResources().getString((header.isPosted()) ? R.string.activity_no_posted : R.string.activity_no_scheduled); 
	}


	/**
	 * Gather the data to go to the detail screen and then use the navigator to go there
	 * @param index - index of the selected item
	 */
	@Override
	public void goToDetailsScreen(final int index){
		final Bundle bundle = saveDataInBundle();
		bundle.putInt(BankExtraKeys.DATA_SELECTED_INDEX, index);
		bundle.putBoolean(BankExtraKeys.IS_LOADING_MORE, getIsLoadingMore());

		// Prevent user from accessing account loan detail
		if (!header.getAccount().type.equalsIgnoreCase(Account.ACCOUNT_LOAN)) {
			BankConductor.navigateToActivityDetailScreen(bundle);
		}
	}

	/**
	 * Get the posted button click listener
	 * @return the posted button click listener
	 */
	public OnCheckedChangeListener getPostedListener(){
		return new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
				if (isChecked) {
					loadPostedData();
				}	
			}
		};
	}
	
	private void loadPostedData() {
		if (posted != null) {
			header.toggleButton(header.getPostedButton(), header.getScheduledButton(), true);
			updateAdapter(posted);
		} else if (BankUser.instance().getCurrentAccount().posted != null) {
			header.toggleButton(header.getPostedButton(), header.getScheduledButton(), true);
			posted = BankUser.instance().getCurrentAccount().posted;
			updateAdapter(posted);
		} else {
			// Both posted lists are null -- Generate service call
			BankServiceCallFactory.createGetActivityServerCall(
				BankUser.instance().getCurrentAccount().
						getLink(Account.LINKS_POSTED_ACTIVITY), ActivityDetailType.Posted, false).submit();
		}
	}

	/**
	 * Get the scheduled button click listener
	 * @return the scheduled button click listener
	 */
	public OnCheckedChangeListener getScheduledListener(){
		return new OnCheckedChangeListener(){

			@Override 
			public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
				if (isChecked) {
					if (scheduled != null) {
						loadLocalActivity();
					} else if (BankUser.instance().getCurrentAccount().scheduled != null) {
						loadCachedActivity();
					} else {
						getActivityServiceCall();
					}
				}
			}
		};
	}
	
	/**
	 * Load the list adapter with data that is stored in the member variable for scheduled
	 * activity.
	 */
	private void loadLocalActivity() {
		header.toggleButton(header.getScheduledButton(), header.getPostedButton(), false);
		updateAdapter(scheduled);
	}
	
	/**
	 * Load scheduled activity from the BankUser instance.
	 */
	private void loadCachedActivity() {
		header.toggleButton(header.getScheduledButton(), header.getPostedButton(), false);
		scheduled = BankUser.instance().getCurrentAccount().scheduled;
		updateAdapter(scheduled);
	}
	
	/**
	 * Retrieve scheduled activity from the server and cache it in the BankUser instance.
	 */
	private void getActivityServiceCall() {
		// Both scheduled lists are null -- Generate service call
		BankServiceCallFactory.createGetActivityServerCall(
			BankUser.instance().getCurrentAccount().
						getLink(Account.LINKS_SCHEDULED_ACTIVITY), ActivityDetailType.Scheduled, false).submit();
	}

	/**
	 * Method that is called when the adapter gets to the bottom of the list.  
	 * This will show the go to top or show the loading bar for most fragments.
	 */
	@Override
	public void maybeLoadMore() {
		final ReceivedUrl url = getLoadMoreUrl();
		if(null == url || null == url.url || url.url.isEmpty()){
			showNothingToLoad();
			getLoadMoreFooter().showDone();
		}else{
			getLoadMoreFooter().showLoading();
			loadMore(url.url);
		}
	}

	/**
	 * Get the load more URL
	 */
	private ReceivedUrl getLoadMoreUrl(){
		return (header.isPosted()) ? 
				posted.links.get(ListActivityDetail.NEXT) : scheduled.links.get(ListActivityDetail.NEXT);
	}

	/**
	 * Load more activities
	 */
	public void loadMore(final String url){
		setIsLoadingMore(true);
		
		if( header.isPosted() ) {
			BankServiceCallFactory.createGetActivityServerCall(url, ActivityDetailType.Posted, false).submit();
		} else {
			BankServiceCallFactory.createGetActivityServerCall(url, ActivityDetailType.Scheduled, false).submit();
		}
	}

	/**
	 * Get the adapter that needs to be attached to the fragment.
	 * @param adatper - adapter to be attached to the list
	 */
	@Override
	public ArrayAdapter<?> getAdapter(){
		return adapter;
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
	public View getFooter(){
		return getLoadMoreFooter();
	}

	/**
	 * Save all the data on the screen in a bundle
	 * @return bundle containing all the data
	 */
	@Override
	public Bundle saveDataInBundle() {
		final Bundle bundle = new Bundle();
		if(null == header){return bundle;}
		if(header.isPosted()){
			bundle.putSerializable(BankExtraKeys.PRIMARY_LIST, posted);
			bundle.putSerializable(BankExtraKeys.SECOND_DATA_LIST, scheduled);
		}else{
			bundle.putSerializable(BankExtraKeys.SECOND_DATA_LIST, posted);
			bundle.putSerializable(BankExtraKeys.PRIMARY_LIST, scheduled);
		}
		bundle.putBoolean(BankExtraKeys.CATEGORY_SELECTED, header.getSelectedCategory());
		bundle.putInt(BankExtraKeys.SORT_ORDER, header.getSortOrder());
		bundle.putBoolean(BankExtraKeys.TITLE_EXPANDED, header.isHeaderExpanded());

		return bundle;
	}

	/**
	 * Extract all the data from a bundle
	 * @param bundle - bundle to pull data from
	 */
	@Override
	public void loadDataFromBundle(final Bundle bundle) {
		if(null == bundle){return;}
		
		/**Read Activity list from bundle*/
		final ListActivityDetail current = (ListActivityDetail)bundle.getSerializable(BankExtraKeys.PRIMARY_LIST);	
		
		/**Set flag to true if passed list has Posted Activities*/
		final boolean isPostedList = (current.type == ActivityDetailType.Posted);
		
		/**Set Header to Posted or Scheduled depenting on the type of list*/
		header.setSelectedCategory(bundle.getBoolean(BankExtraKeys.CATEGORY_SELECTED, isPostedList));
		
		final ListActivityDetail other = (ListActivityDetail)bundle.getSerializable(BankExtraKeys.SECOND_DATA_LIST);
		if(header.isPosted()){
			posted = current;
			scheduled = other;
		}else{
			scheduled = current;
			posted = other;
		}
		header.setSortOrder(bundle.getInt(BankExtraKeys.SORT_ORDER, BankExtraKeys.SORT_DATE_DESC));
		header.setHeaderExpanded(bundle.getBoolean(BankExtraKeys.TITLE_EXPANDED, false));
		if(null != current){
			this.updateAdapter(current);

			final ReceivedUrl url = getLoadMoreUrl();
			if(null == url){
				showNothingToLoad();
			}
		}
	}

	/**
	 * Hide the footer
	 */
	public void hideFooter() {
		getLoadMoreFooter().hideAll();
	}

	/**
	 * Set up the header
	 */
	@Override
	public void setupHeader() {
		header = new AccountActivityHeader(this.getActivity(), null);
		header.getPostedButton().setOnCheckedChangeListener(this.getPostedListener());
		header.getScheduledButton().setOnCheckedChangeListener(this.getScheduledListener());
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
	 * Setup the adapter
	 */
	@Override
	public void setupAdapter() {
		adapter = new BankListAdapter(this.getActivity(), header.getAccount(), R.layout.bank_table_item, this);

	}

	/**
	 * Setup the lists of details that are not already created
	 */
	@Override
	public void createDefaultLists() {
		//This does not need to be done
	}
	
	public void showStatusMessage() {
		header.showStatusMessage(R.string.account_activity_scheduled_transfer_deleted);
	}
	
	public final void showDeletePaymentMessage() {
		header.showStatusMessage(R.string.review_payments_scheduled_deleted);
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
		return BankMenuItemLocationIndex.ACCOUNT_SUMMARY_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.ACCOUNT_SUMMARY_SECTION;
	}
}
