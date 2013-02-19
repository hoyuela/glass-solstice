package com.discover.mobile.bank.account;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.BankNavigator;
import com.discover.mobile.bank.BankServiceCallFactory;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.account.activity.ActivityDetail;
import com.discover.mobile.bank.services.account.activity.ListActivityDetail;
import com.discover.mobile.bank.ui.table.BaseTable;
import com.discover.mobile.bank.ui.table.TableLoadMoreFooter;
import com.discover.mobile.common.net.json.bank.ReceivedUrl;

/**
 * View that allows the user to view posted and scheduled acvitivies for an account
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

	/**Footer to put in the bottom of the list view*/
	private TableLoadMoreFooter footer;

	/**
	 * Handle the received data from the service call
	 * @param bundle - bundle received from the service call
	 */
	@Override
	public void handleReceivedData(final Bundle bundle) {
		final ListActivityDetail list = (ListActivityDetail) bundle.getSerializable(BankExtraKeys.PRIMARY_LIST);
		if(header.isPosted()){
			handleReceivedData(posted, list);
		}else{
			handleReceivedData(scheduled, list);
		}
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
		updateAdapter(list.activities);

	}

	/**
	 * Update the adapter
	 * @param activities - activities to update the adapter with
	 */
	public void updateAdapter(final List<ActivityDetail> activities){
		adapter.setData(activities);
		if(adapter.getCount() < 1){
			footer.showEmpty(this.getEmptyStringText());
		}
		adapter.notifyDataSetChanged();
	}

	/**
	 * @return the title to be displayed in the action bar
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.pay_a_bill_title;
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
		BankNavigator.navigateToActivityDetailScreen(bundle);
	}

	/**
	 * Get the posted button click listener
	 * @return the posted button click listener
	 */
	public OnCheckedChangeListener getPostedListener(){
		return new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
				if(isChecked){
					header.toggleButton(header.getPostedButton(), header.getScheduledButton(), true);		
					updateAdapter(posted.activities);
				}
			}
		};
	}

	/**
	 * Get the scheduled button click listener
	 * @return the scheduled button click listener
	 */
	public OnCheckedChangeListener getShceduledListener(){
		return new OnCheckedChangeListener(){

			@Override 
			public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
				if(isChecked){
					header.toggleButton(header.getScheduledButton(), header.getPostedButton(), false);	
					updateAdapter(scheduled.activities);
				}
			}
		};
	}

	/**
	 * Method that is called when the adapter gets to the bottom of the list.  
	 * This will show the go to top or show the loading bar for most fragments.
	 */
	@Override
	public void maybeLoadMore() {
		final ReceivedUrl url = getLoadMoreUrl();
		if(null == url){
			footer.showDone();
		}else{
			footer.showLoading();
			loadMore(url.url);
		}
	}

	/**
	 * Get the load more URL
	 */
	private ReceivedUrl getLoadMoreUrl(){
		return (header.isPosted()) ? posted.links.get(ListActivityDetail.NEXT) : scheduled.links.get(ListActivityDetail.NEXT);
	}

	/**
	 * Load more activities
	 */
	public void loadMore(final String url){
		BankServiceCallFactory.createGetActivityServerCall(url).submit();
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
		return footer;
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
		header.setSelectedCategory(bundle.getBoolean(BankExtraKeys.CATEGORY_SELECTED, true));
		final ListActivityDetail current = (ListActivityDetail)bundle.getSerializable(BankExtraKeys.PRIMARY_LIST);
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
		createDefaultLists();
		this.updateAdapter(current.activities);
	}

	/**
	 * Hide the footer
	 */
	public void hideFooter() {
		footer.hideAll();
	}

	/**
	 * Set up the header
	 */
	@Override
	public void setupHeader() {
		header = new AccountActivityHeader(this.getActivity(), null);
		header.getPostedButton().setOnCheckedChangeListener(this.getPostedListener());
		header.getScheduledButton().setOnCheckedChangeListener(this.getShceduledListener());
	}

	/**
	 * Set up the footer
	 */
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

	/**
	 * Setup the adapter
	 */
	@Override
	public void setupAdapter() {
		adapter = new BankListAdapter(this.getActivity(), R.layout.bank_table_item, posted.activities, this);

	}

	/**
	 * Setup the lists of details that are not already created
	 */
	@Override
	public void createDefaultLists() {
		if(null == posted || null == posted.activities){
			posted = new ListActivityDetail();
			posted.activities = new ArrayList<ActivityDetail>();
		}
		if(null == scheduled || null == scheduled.activities){
			scheduled = new ListActivityDetail();
			scheduled.activities = new ArrayList<ActivityDetail>();
		}
	}
}
