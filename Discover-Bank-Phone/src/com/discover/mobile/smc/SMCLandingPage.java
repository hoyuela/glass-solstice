package com.discover.mobile.smc;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.ui.table.BaseTable;
import com.discover.mobile.common.DiscoverActivityManager;

/**
 * Fragment used for display and controlling the smc landing page.
 * @author juliandale
 *
 */
public class SMCLandingPage extends BaseTable {

	/**header that contains buttons to switch inboxes and compose a new message*/
	private SMCLandingHeaderView header;
	/**Adapter used to hold and display list of messages*/
	private MessageListAdapter adapter;
	
	/**static key to represent the inbox*/
	public static final String INBOX = "inbox";
	/**static key to represent the sent mailbox*/
	public static final String SENTBOX = "sentbox";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = super.onCreateView(inflater, container, savedInstanceState);
		//we do not want the load more functionality for now
		showNothingToLoad();
		return view;
	}
	
	@Override
	public void handleReceivedData(Bundle bundle) {
		adapter.notifyDataSetChanged();
	}

	/**
	 * Construct a new MessageListAdapter for listview to use
	 */
	@Override
	public void setupAdapter() {
		Activity activity = DiscoverActivityManager.getActiveActivity();
		adapter = new MessageListAdapter(activity, R.layout.bank_smc_list_item);
	}

	@Override
	public void createDefaultLists() {
	}

	/**
	 * Getter for adapter
	 */
	@Override
	public ArrayAdapter<?> getAdapter() {
		return adapter;
	}

	@Override
	public void maybeLoadMore() {}

	@Override
	public void setupHeader() {
		header = new SMCLandingHeaderView(getActivity(), null);
	}

	@Override
	public void setupFooter() {}

	/**
	 * getter for the header view
	 */
	@Override
	public View getHeader() {
		return header;
	}

	@Override
	public View getFooter() {
		//no footer is being used
		return null;
	}

	@Override
	public void goToDetailsScreen(int index) {
		
	}

	@Override
	public Bundle saveDataInBundle() {
		//no caching so this method is unnecessary 
		return null;
	}

	@Override
	public void loadDataFromBundle(Bundle bundle) {
		//retrieve the list from the bundle
		MessageList list = (MessageList) bundle.getSerializable(BankExtraKeys.PRIMARY_LIST);
		//set the data in the adapter
		adapter.setData(list.messages);
		//refresh the ui so updates to adapter's data is shown
		adapter.notifyDataSetChanged();
		
	}

	@Override
	public void showFooterMessage() {}

	@Override
	protected boolean isDataUpdateRequired() {
		return false;
	}

	@Override
	protected void updateData() {}

	@Override
	public int getActionBarTitle() {
		return R.string.smc_nav_title;
	}

	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.CUSTOMER_SERVICE_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.SECURE_MEASSAGE_CENTER;
	}



}
