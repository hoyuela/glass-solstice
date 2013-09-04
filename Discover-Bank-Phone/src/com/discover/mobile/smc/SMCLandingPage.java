package com.discover.mobile.smc;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.ui.table.BaseTable;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.ui.table.TableHeaderButton;
import com.discover.mobile.common.utils.StringUtility;

/**
 * Fragment used for display and controlling the smc landing page.
 * @author juliandale
 *
 */
public class SMCLandingPage extends BaseTable 
implements OnClickListener {

	/**header that contains buttons to switch inboxes and compose a new message*/
	private SMCLandingHeaderView header;
	/**Adapter used to hold and display list of messages*/
	private MessageListAdapter adapter;

	/**static key to represent the inbox*/
	public static final String INBOX = "inbox";
	/**static key to represent the sent mailbox*/
	public static final String SENTBOX = "sentbox";
	/**Static key used to identify what type of list is contained in bundle*/
	public static final String LIST_BOX_TYPE = "listBoxType";
	/**message list for inbox*/
	private List<MessageListItem> inboxList;
	/**message list for sent messages*/
	private List<MessageListItem> sentList;
	
	/**following are keys to allow saving the state on rotation*/
	public static final String CURRENT_LIST = "current_list";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = super.onCreateView(inflater, container, savedInstanceState);
		//we do not want the load more functionality for now
		showNothingToLoad();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		header.setGroupObserver(this);
	}

	@Override
	public void onPause() {
		header.removeListeners();
		super.onPause();
	}
	
	@Override
	public void handleReceivedData(Bundle bundle) {
		String mailBoxType = bundle.getString(SMCLandingPage.LIST_BOX_TYPE, StringUtility.EMPTY);
		if(mailBoxType.endsWith(SMCLandingPage.INBOX)) {
			inboxList = ((MessageList) bundle.getSerializable(BankExtraKeys.PRIMARY_LIST)).messages;
			loadInboxMessages();
		} else {
			sentList = ((MessageList) bundle.getSerializable(BankExtraKeys.SECOND_DATA_LIST)).messages;
			loadSentMessages();
		}
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
	public View getFooter() { return null; }

	@Override
	public void goToDetailsScreen(int index) {

	}

	@Override
	public Bundle saveDataInBundle() {
		//save the current state
		Bundle args = new Bundle();
		String selected = (header.getInboxSelected()) ? INBOX: SENTBOX;
		args.putString(LIST_BOX_TYPE, selected);
		MessageList inbox = new MessageList();
		inbox.messages = inboxList;
		args.putSerializable(BankExtraKeys.PRIMARY_LIST, inbox);
		MessageList sent = new MessageList();
		sent.messages = sentList;
		args.putSerializable(BankExtraKeys.SECOND_DATA_LIST, sent);
		return args;
	}

	@Override
	public void loadDataFromBundle(Bundle bundle) {
		//retreive inbox
		MessageList inbox = (MessageList) bundle.getSerializable(BankExtraKeys.PRIMARY_LIST);
		//retrieve the sent box
		MessageList sent = (MessageList) bundle.getSerializable(BankExtraKeys.SECOND_DATA_LIST);
		if( null != inbox) {
			inboxList = inbox.messages;
		}
		if( null != sent){
			sentList = sent.messages;
		}
		//determine which list to show to user
		String mailboxType = bundle.getString(LIST_BOX_TYPE, INBOX);
		if(mailboxType.equals(INBOX)){
			loadInboxMessages();
		}else {
			loadSentMessages();
		}
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

	/**switch the inbox to sent messages.  
	 *if sent messages haven't been loaded yet
	 *make service call to perform action
	 */	
	public void loadSentMessages(){		
		if(null != sentList) {
			//update the list
			adapter.setData(sentList);
			adapter.notifyDataSetChanged();
		} else {
			sentList = new ArrayList<MessageListItem>();
			BankServiceCallFactory.createMessageListCall(SMCLandingPage.SENTBOX).submit();
		}
		header.setSentSelected();
	}
	
	/**switch too the inbox*/
	public void loadInboxMessages() {
		if (null != inboxList) {
			adapter.setData(inboxList);
			adapter.notifyDataSetChanged();
		} else {
			inboxList = new ArrayList<MessageListItem>();
			BankServiceCallFactory.createMessageListCall(INBOX).submit();
		}
		header.setInboxSelected();
	}

	/**
	 * On click listener for the header button group.
	 * Allows user to switch between the inbox and sent messages.
	 */
	@Override
	public void onClick(View v) {
		TableHeaderButton hitButton = (TableHeaderButton) v;
		if(header.getInboxButton().getText().equals(hitButton.getText())) {
			//load the inbox
			loadInboxMessages();
		} else {
			//must be loading the sent box
			loadSentMessages();
		}
	}



}
