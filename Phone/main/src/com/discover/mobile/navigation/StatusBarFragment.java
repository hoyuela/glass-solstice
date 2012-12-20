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
	
	private TextView accountName;
	public final static String TAG = "StatusBarFragment";
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		
		final View view = inflater.inflate(R.layout.status_bar_layout, container);
		accountName = (TextView) view.findViewById(R.id.account_name);
		setAccountName(null);
		return view;
	}
	
	/**
	 * Used for setting the account name title in the status bar. 
	 * @param account
	 */
	public void setAccountName(String account){
		//TODO THis is temporary and used just to populate something in the account Name
		final AccountDetails accountDetails = CurrentSessionDetails.getCurrentSessionDetails().getAccountDetails();
		
		accountName.setText(accountDetails.primaryCardMember.nameOnCard);
	}

}
