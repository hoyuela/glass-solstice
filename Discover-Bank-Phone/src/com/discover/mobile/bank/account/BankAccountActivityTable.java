package com.discover.mobile.bank.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.framework.BankUserListener;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.account.activity.ActivityDetail;
import com.discover.mobile.bank.services.account.activity.ActivityDetailType;
import com.discover.mobile.bank.services.account.activity.ListActivityDetail;
import com.discover.mobile.bank.services.json.ReceivedUrl;
import com.discover.mobile.bank.services.transfer.TransferType;
import com.discover.mobile.bank.ui.table.BaseTable;
import com.discover.mobile.common.ui.table.TableHeaderButton;
import com.discover.mobile.common.utils.StringUtility;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;

/**
 * View that allows the user to view posted and scheduled activities for an account.
 * 
 * This class implements the OnClickListener so it can be notified that the buttons
 * in the header were clicked.  When the fragment is paused it should make sure to
 * have the buttons no longer reference it.  With that being said, when resuming the
 * fragment the fragment should make sure that it registers itself with the buttons.
 * 
 * @author jthornton
 *
 */
public class BankAccountActivityTable extends BaseTable implements BankUserListener, OnClickListener {

	/** Holds strings that will be used to set the account in the arguments of this fragment */
	public static final String ACCOUNT = "acct-info";

	/**List of posted activities in the table*/
	private ListActivityDetail posted;

	/**List of scheduled activities in the table*/
	private ListActivityDetail scheduled;

	/**Adapter used to display data*/
	private BankListAdapter adapter;

	/**Title view of the page*/
	private AccountActivityHeader header;


	@Override
	public void onResume() {
		/** Verify this fragment is being displayed */
		if (isViewCreated()) {
			/** Update the account information */
			getAccountInfo(true);
		}

		/** Subscribe for event raised by the bank user for when data changes */
		BankUser.instance().addListener(this);

		super.onResume();

		/** Attach the listener to the buttons */
		header.setGroupObserver(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		/** Subscribe for event raised by the bank user for when data changes */
		BankUser.instance().removeListener(this);

		/** Remove the listeners from the buttons */
		header.removeListeners();
	}

	/**
	 * Method used to update the account information used to display in the header and reference to the accont object
	 * used to fetch posted/scheduled activity data.
	 */
	public Account getAccountInfo(final boolean refresh) {
		Account account = null;

		if (refresh) {
			final Bundle bundle = getArguments();
			if (bundle != null && getArguments().containsKey(ACCOUNT)) {
				account = (Account) getArguments().getSerializable(ACCOUNT);

				/** Get latest account information from cache */
				if (account != null) {
					account = BankUser.instance().getAccount(account.id);
				}

				/** Update reference in header */
				header.setAccount(account);

				/** Set current account */
				BankUser.instance().setCurrentAccount(account);
			}
		} else {
			account = header.getAccount();
		}

		return account;
	}

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

		//If the item was deleted clear out the scheduled list
		if(bundle.containsKey(BankExtraKeys.CONFIRM_DELETE)){
			scheduled = null;
		}

		// Toggle between scheduled/posted if incoming list type does not match view
		if (list.type == ActivityDetailType.Posted && !header.isPosted()
				|| list.type == ActivityDetailType.Scheduled && header.isPosted()) {
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
		header.setSelectedCategory(true);
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
		header.setSelectedCategory(false);
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

			if(adapter.getCount() < 1){
				header.setMessage(getEmptyStringText());
				showNothingToLoad();
				getLoadMoreFooter().hideAll();
				getLoadMoreFooter().centerPrivacyText();
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
		return getResources().getString(header.isPosted() ? R.string.activity_no_posted : R.string.activity_no_scheduled); 
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
			BankConductor.navigateToActivityDetailScreen(bundle, true);
		}
	}

	/**
	 * Load the posted data.  This method determines if we need to load data and which data set to load.
	 */
	private void loadPostedData() {
		final Account account = getAccountInfo(false);

		if (posted != null) {
			header.setSelectedCategory(true);
			updateAdapter(posted);
		} else if (account.posted != null) {
			header.setSelectedCategory(true);
			posted = account.posted;
			updateAdapter(posted);
		} else {
			// Both posted lists are null -- Generate service call
			BankServiceCallFactory.createGetActivityServerCall(
					account.getLink(Account.LINKS_POSTED_ACTIVITY), ActivityDetailType.Posted).submit();
		}
	}

	/**
	 * Load the scheduled data.  This method determines if we need to load data and which data set to load.
	 */
	private void loadScheduledData() {
		final Account account = getAccountInfo(false);

		if (scheduled != null) {
			header.setSelectedCategory(false);
			updateAdapter(scheduled);
		} else if (account.scheduled != null) {
			header.setSelectedCategory(false);
			posted = account.scheduled;
			updateAdapter(scheduled);
		} else {
			getScheduledActivityServiceCall();
		}
	}

	/**
	 * Load the list adapter with data that is stored in the member variable for scheduled
	 * activity.
	 */
	private void loadLocalActivity() {
		header.setSelectedCategory(false);
		updateAdapter(scheduled);
	}

	/**
	 * Load scheduled activity from the BankUser instance.
	 */
	private void loadCachedActivity() {
		header.setSelectedCategory(false);
		scheduled = getAccountInfo(false).scheduled;
		updateAdapter(scheduled);
	}

	/**
	 * Retrieve scheduled activity from the server and cache it in the BankUser instance.
	 */
	private void getScheduledActivityServiceCall() {
		final Account account = getAccountInfo(false);

		// Both scheduled lists are null -- Generate service call
		BankServiceCallFactory.createGetActivityServerCall(
				account.getLink(Account.LINKS_SCHEDULED_ACTIVITY), ActivityDetailType.Scheduled)
				.submit();
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
		return header.isPosted() ? 
				posted.links.get(ListActivityDetail.NEXT) : scheduled.links.get(ListActivityDetail.NEXT);
	}

	/**
	 * Load more activities
	 */
	public void loadMore(final String url){
		setIsLoadingMore(true);

		if( header.isPosted() ) {
			BankServiceCallFactory.createGetActivityServerCall(url, ActivityDetailType.Posted).submit();
		} else {
			BankServiceCallFactory.createGetActivityServerCall(url, ActivityDetailType.Scheduled).submit();
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
			bundle.putSerializable(BankExtraKeys.REVIEW_TRANSFERS_TYPE, TransferType.Scheduled);
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
		final boolean isPostedList = current.type == ActivityDetailType.Posted;

		/**Set Header to Posted or Scheduled depending on the type of list*/
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

		/** Verify if header was expanded before rotation */
		final boolean expandHeader = bundle.getBoolean(BankExtraKeys.TITLE_EXPANDED, false);
		header.post(new Runnable() {
			@Override
			public void run() {
				if (expandHeader) {
					header.expand(false);
				} else {
					header.collapse(false);
				}
			}

		});

		if(null != current){
			updateAdapter(current);

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
		header = new AccountActivityHeader(getActivity(), null);
	}

	/**
	 * This is called when one of the buttons is pressed in the header.
	 * The buttons in the header need to have this attached, to do that call header.setGroupObserver(this).
	 * @param view - view that is triggering this on click event 
	 */
	@Override
	public void onClick(final View view) {
		final TableHeaderButton clicked = (TableHeaderButton)view;

		if(header.getPostedButton().getText().equals(clicked.getText()) && !header.isPostedSelected()){
			loadPostedData();
		}else if(header.getScheduledButton().getText().equals(clicked.getText()) && header.isPostedSelected()){
			loadScheduledActivity();
		}
	}

	/**
	 * Load the scheduled activity
	 */
	private void loadScheduledActivity(){
		if (scheduled != null) {
			loadLocalActivity();
		} else if (getAccountInfo(false).scheduled != null) {
			loadCachedActivity();
		} else {
			getScheduledActivityServiceCall();
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
	 * Setup the adapter
	 */
	@Override
	public void setupAdapter() {
		adapter = new BankListAdapter(getActivity(), header.getAccount(), R.layout.bank_table_item, this);

	}

	/**
	 * Setup the lists of details that are not already created
	 */
	@Override
	public void createDefaultLists() {
		//This does not need to be done
	}

	/**
	 * Shows a Transfer deleted message on the table header for a given
	 * TransferDeletionType
	 * 
	 * @param deletionType the type of transfer that was deleted.
	 */
	public void showTransferDeletedMessage(final TransferDeletionType deletionType) {
		final String rawText = getResources().getString(R.string.account_activity_scheduled_transfer_deleted);
		StringBuilder transfer = new StringBuilder(getResources().getString(R.string.transfer));
		String messageToDisplay = StringUtility.EMPTY;

		//Pluralizes the transfer type.
		switch(deletionType) {
		case DELETE_ALL_TRANSFERS:
			transfer = transfer.append("s");
			break;
		default:
			break;
		}

		messageToDisplay = String.format(rawText, transfer.toString());
		header.showStatusMessage(messageToDisplay);
	}

	/**
	 * Display the delete payment message in the table's header.
	 * The message will fade out after some amount of time.
	 */
	public final void showDeletePaymentMessage() {
		header.showStatusMessage(getResources().getString(R.string.review_payments_scheduled_deleted));
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

	@Override
	protected void updateData() {
		/** Update the account information */
		getAccountInfo(true);

		// Empty current referenced data so when user toggles
		// to either of the tabs the data is downloaded again
		scheduled = null;
		posted = null;

		// Check whether the user is currently on the posted or scheduled view
		// to make the respective service call
		if (header.isPosted()) {
			loadPostedData();
		} else {
			loadScheduledData();
		}
	}

	@Override
	protected boolean isDataUpdateRequired() {
		boolean isActivityDownloadRequired = false;

		/** Check if view is currently being displayed */
		if (isViewCreated()) {
			/** Get reference to account object whose activity details are being displayed currently */
			final Account account = header.getAccount();

			if (account != null) {
				// Check whether the user is currently on the posted or scheduled view
				// to make the respective service call
				if (header.isPosted()) {
					isActivityDownloadRequired = account.posted == null || posted == null;
				} else {
					isActivityDownloadRequired = account.scheduled == null || scheduled == null;
				}
			}
		}

		return isActivityDownloadRequired;
	}

	@Override
	public void onAccountsUpdate(final BankUser sender, final List<Account> accounts) {
		/** Check if view is currently being displayed */
		if (isViewCreated()) {
			updateData();
		} else {
			/** Mark referenced activity data dirty so it is refreshed in onResume() */
			scheduled = null;
			posted = null;
		}
	}

	@Override
	public void onCurrentAccountUpdate(final BankUser sender, final Account account) {
		// This Event is not required to be handled
	}
}
