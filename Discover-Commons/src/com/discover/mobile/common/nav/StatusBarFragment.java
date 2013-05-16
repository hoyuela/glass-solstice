package com.discover.mobile.common.nav;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.discover.mobile.common.R;

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
		
		final View view = inflater.inflate(R.layout.common_status_bar_layout, container);
		accountName = (TextView) view.findViewById(R.id.account_name);
		setAccountName();
		return view;
	}
	
	/**
	 * Used for setting the account name title in the status bar. 
	 * @param account
	 */
	private void setAccountName(){
		//FIXME This is temporary and used just to populate something in the account Name
		setStatusBarText("John Doe");
	}
	
	/**
	 * Set the text in the status bar
	 * @param text - text to set in the status bar
	 */
	public void setStatusBarText(final String text){
		accountName.setText(text);
	}
}
