package com.discover.mobile.push;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.RoboSherlockFragment;

/**
 * Fragment that is the push notification manage screen.  Uses the push save header, 
 * push toggle item and push save item.  It will compile them into the list.
 * 
 * @author jthornton
 *
 */
public class PushManageFragment extends RoboSherlockFragment{

	private PushManageHeaderItem manageHeader;
	
	private LinearLayout manageList;
	
	private PushManageHeaderItem monitorHeader;
	
	private LinearLayout monitorList;
	
	private PushManageHeaderItem maximizeHeader;
	
	private LinearLayout maximizeList;
	
	private Resources res;
	
	private Context context;
	
	private List<PushManageToogleItem> views;
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final View mainView = inflater.inflate(R.layout.push_manage_main_layout, null);
		manageHeader = (PushManageHeaderItem)mainView.findViewById(R.id.manage_you_account_header);
		manageList = (LinearLayout)mainView.findViewById(R.id.manage_account_list_preferences);
		monitorHeader = (PushManageHeaderItem)mainView.findViewById(R.id.monitor_spending_list_header);
		monitorList = (LinearLayout)mainView.findViewById(R.id.monitor_spending_list_preferences);
		maximizeHeader = (PushManageHeaderItem)mainView.findViewById(R.id.maximize_rewards_list_header);
		maximizeList = (LinearLayout)mainView.findViewById(R.id.maximize_rewards_list_preferences);
		final TextView termsLaunch = (TextView) mainView.findViewById(R.id.terms_of_use_view_3);
		
		final Button save = (Button) mainView.findViewById(R.id.notification_save_button);
		save.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(final View v) {
				savePreferences();
			}
			
		});
		
		termsLaunch.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(final View v) {
				showTermsAndConditions();
			}
			
		});
		
		context = this.getActivity();
		
		res = context.getResources();
		createHeaders();
		createLists();
		setListsInHeader();
		
		return mainView;
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
			final PushManageToogleItem view = new PushManageToogleItem(context, null);
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
	
	public void savePreferences(){
		
	}
	
	public void showTermsAndConditions(){
		makeFragmentVisible(new PushTermsAndConditionsFragment());
	}
	
	/**
	 * Return the integer value of the string that needs to be displayed in the title
	 */
	@Override
	public int getActionBarTitle() {
		return R.string.manage_push_fragment_title;
	}
}
