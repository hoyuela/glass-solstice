package com.discover.mobile.navigation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.CurrentSessionDetails;
import com.discover.mobile.common.auth.AccountDetails;

/**
 * Status bar fragment that appears under the action bar. Used to display the account name 
 * as well as a link to the help section.
 * @author ajleeds
 *
 */
public class StatusBarFragment extends Fragment {
	
	/**Text view holding the account name*/
	private TextView accountName;

	/**
	 * Create the fragment view
	 * @param inflater - inflater that will inflate the layout
	 * @param container - container holding the fragment
	 * @return the view 
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		
		final View view = inflater.inflate(R.layout.status_bar_layout, container);
		accountName = (TextView) view.findViewById(R.id.account_name);
		setAccountName();
		return view;
	}
	
	/**
	 * Used for setting the account name title in the status bar. 
	 * @param account
	 */
	private void setAccountName(){
		//TODO This is temporary and used just to populate something in the account Name
		final AccountDetails accountDetails = CurrentSessionDetails.getCurrentSessionDetails().getAccountDetails();
		
		setStatusBarText(accountDetails.primaryCardMember.nameOnCard);
	}
	
	/**
	 * Set the text in the status bar
	 * @param text - text to set in the status bar
	 */
	public void setStatusBarText(final String text){
		accountName.setText(text);
	}
}
