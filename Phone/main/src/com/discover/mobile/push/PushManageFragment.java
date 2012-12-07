package com.discover.mobile.push;

import java.util.ArrayList;
import java.util.List;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.discover.mobile.R;

/**
 * Fragment that is the push notification manage screen.  Uses the push save header, 
 * push toggle item and push save item.  It will compile them into the list.
 * 
 * @author jthornton
 *
 */
@ContentView(R.layout.push_manage_main_layout)
public class PushManageFragment extends RoboActivity{

	@InjectView(R.id.manage_you_account_header)
	private PushManageHeaderItem manageHeader;
	
	@InjectView(R.id.manage_account_list_preferences)
	private LinearLayout manageList;
	
	@InjectView(R.id.monitor_spending_list_header)
	private PushManageHeaderItem monitorHeader;
	
	@InjectView(R.id.monitor_spending_list_preferences)
	private LinearLayout monitorList;
	
	@InjectView(R.id.maximize_rewards_list_header)
	private PushManageHeaderItem maximizeHeader;
	
	@InjectView(R.id.maximize_rewards_list_preferences)
	private LinearLayout maximizeList;
	
	private Resources res;
	
	private List<PushManageToogleItem> views;
	
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		res = this.getResources();
		createHeaders();
		createLists();
		setListsInHeader();
	}

	private void setListsInHeader() {
		manageHeader.setList(manageList);
		monitorHeader.setList(monitorList);
		maximizeHeader.setList(maximizeList);	
	}

	private void createLists() {
		views = new ArrayList<PushManageToogleItem>();
		createList(manageList, 
					res.getStringArray(R.array.manage_your_accounts_headers),
					res.getStringArray(R.array.manage_your_accounts_text));
		createList(monitorList, 
					res.getStringArray(R.array.monitor_your_spending_headers),
				 	res.getStringArray(R.array.monitor_your_spending_text));
		createList(maximizeList, 
					res.getStringArray(R.array.maximize_your_rewards_headers),
					res.getStringArray(R.array.maximize_your_rewards_text));
	}
	
	private void createList(final LinearLayout list, final String[] headers, final String[] texts){
		final int lengthOfHeaders = headers.length;
		
		for(int i = 0; i < lengthOfHeaders; i++) {
			final PushManageToogleItem view = new PushManageToogleItem(this, null);
			view.setHeader(headers[i]);
			view.setText(texts[i]);
			view.setBackgroundDrawable(res.getDrawable(R.drawable.notification_list_item));
			//FIXME: Pull these out into a dimensions file
			view.setPadding(14, 28, 14 ,28);
			list.addView(view);
			views.add(view);
		}
	}

	private void createHeaders() {
		manageHeader.setHeader(res.getString(R.string.manage_your_account_title));
		monitorHeader.setHeader(res.getString(R.string.monitor_your_spending_title));
		maximizeHeader.setHeader(res.getString(R.string.maximize_your_rewards_title));	
	}
	
	public void savePreferences(final View v){
		
	}
}
