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
import com.discover.mobile.bank.help.HelpMenuListFactory;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.account.activity.ActivityDetail;
import com.discover.mobile.bank.services.account.activity.ListActivityDetail;
import com.discover.mobile.bank.services.json.ReceivedUrl;
import com.discover.mobile.bank.ui.table.BaseTable;
import com.discover.mobile.common.help.HelpWidget;
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
		footer.showDone();
		final ListActivityDetail list = (ListActivityDetail) bundle.getSerializable(BankExtraKeys.PRIMARY_LIST);
		if(header.isPosted()){
			if(null == posted){
				posted = new ListActivityDetail();
				posted.activities  = new ArrayList<ActivityDetail>();
				posted.links = new HashMap<String, ReceivedUrl>();
			}
			handleReceivedData(posted, list);
		}else{
			if(null == scheduled){
				scheduled = new ListActivityDetail();
				scheduled.activities  = new ArrayList<ActivityDetail>();
				scheduled.links = new HashMap<String, ReceivedUrl>();
			}
			handleReceivedData(scheduled, list);
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
		adapter.setData(current.activities);
		if(null == BankUser.instance().getCurrentAccount().posted && header.isPosted()){
			BankUser.instance().getCurrentAccount().posted = current;
		} else if(null == BankUser.instance().getCurrentAccount().scheduled && !header.isPosted()){
			BankUser.instance().getCurrentAccount().scheduled = current;
		}
		if(adapter.getCount() < 1){
			header.setMessage(this.getEmptyStringText());
			showNothingToLoad();
			footer.hideAll();
		} else {
			table.setMode(Mode.PULL_FROM_END);
			header.clearMessage();
			footer.showDone();
		}
		adapter.notifyDataSetChanged();
		final ReceivedUrl url = getLoadMoreUrl();
		if(null == url){
			showNothingToLoad();
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
		BankConductor.navigateToActivityDetailScreen(bundle);
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
					if(null == posted && null == BankUser.instance().getCurrentAccount().posted){
						BankServiceCallFactory.createGetActivityServerCall(
								BankUser.instance().getCurrentAccount().getLink(Account.LINKS_POSTED_ACTIVITY)).submit();
					}else if(null == posted && null != BankUser.instance().getCurrentAccount().posted){
						posted = BankUser.instance().getCurrentAccount().posted;
						updateAdapter(posted);	
					}
					else{
						updateAdapter(posted);	
					}
				}
			}
		};
	}

	/**
	 * Get the scheduled button click listener
	 * @return the scheduled button click listener
	 */
	public OnCheckedChangeListener getScheduledListener(){
		return new OnCheckedChangeListener(){

			@Override 
			public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
				if(isChecked){
					header.toggleButton(header.getScheduledButton(), header.getPostedButton(), false);	
					if(null == scheduled && null == BankUser.instance().getCurrentAccount().scheduled){
						BankServiceCallFactory.createGetActivityServerCall(
								BankUser.instance().getCurrentAccount().getLink(Account.LINKS_SCHEDULED_ACTIVITY)).submit();
					}else if(null == scheduled && null != BankUser.instance().getCurrentAccount().scheduled){
						scheduled = BankUser.instance().getCurrentAccount().scheduled;
						updateAdapter(scheduled);	
					}else{
						updateAdapter(scheduled);	
					}			
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
		if(null == url || null == url.url || url.url.isEmpty()){
			showNothingToLoad();
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
		setIsLoadingMore(true);
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
		footer.hideAll();
	}

	/**
	 * Set up the header
	 */
	@Override
	public void setupHeader() {
		header = new AccountActivityHeader(this.getActivity(), null);
		header.getPostedButton().setOnCheckedChangeListener(this.getPostedListener());
		header.getScheduledButton().setOnCheckedChangeListener(this.getScheduledListener());
		setUpMenu();
	}

	/**
	 * Create the help list item that will be shown in the menu
	 */
	public void setUpMenu(){
		final HelpWidget help = header.getHelp();
		help.showHelpItems(HelpMenuListFactory.instance().getAccountHelpItems());
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
	 * Setup the adapter
	 */
	@Override
	public void setupAdapter() {
		adapter = new BankListAdapter(this.getActivity(), R.layout.bank_table_item, this);

	}

	/**
	 * Setup the lists of details that are not already created
	 */
	@Override
	public void createDefaultLists() {
		//This does not need to be done
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
