package com.discover.mobile.card.navigation;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.discover.mobile.card.common.sharedata.CardShareDataStore;

import com.discover.mobile.card.R;
import com.discover.mobile.card.services.auth.AccountDetails;

/**
 * Status bar fragment that appears under the action bar. Used to display the
 * account name as well as a link to the help section.
 * 
 * @author ajleeds
 * 
 */
public class StatusBarFragment extends Fragment {

	/** Text view holding the account name */
	private TextView accountName;
	private Activity stsActivity;
	AccountDetails accountDetails = null;

	/**
	 * Create the fragment view
	 * 
	 * @param inflater
	 *            - inflater that will inflate the layout
	 * @param container
	 *            - container holding the fragment
	 * @return the view
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {

		final View view = inflater.inflate(R.layout.status_bar_layout,
				container);
		final CardShareDataStore cardShareDataStoreObj = CardShareDataStore
				.getInstance(stsActivity);
		accountDetails = (AccountDetails) cardShareDataStoreObj
				.getValueOfAppCache(stsActivity
						.getString(R.string.account_details));
		accountName = (TextView) view.findViewById(R.id.account_name);
		view.findViewById(R.id.cardBankIcon).setVisibility(view.GONE);
		view.findViewById(R.id.AC_orange_arrow_down).setVisibility(view.GONE);
		setAccountName();
		return view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.stsActivity = activity;
	}

	/**
	 * Used for setting the account name title in the status bar.
	 * 
	 * @param account
	 */
	private void setAccountName() {
		// FIXME This is temporary and used just to populate something in the
		// account Name
		StringBuilder defaultHiText = new StringBuilder(
				stsActivity.getString(R.string.hi_note));
		if (null != accountDetails) {
		    if (null != accountDetails.mailingAddress) {
                if (null != accountDetails.mailingAddress.firstName) {
                    defaultHiText
                            .append(" "+accountDetails.mailingAddress.firstName);
                }
            }
		    else
		    {
		        
		        if (null != accountDetails.primaryCardMember) {
	                if (null != accountDetails.primaryCardMember.nameOnCard) {
	                    defaultHiText
	                            .append(" "+accountDetails.primaryCardMember.nameOnCard);
	                }
	            }
		        
		    }
		    
			
		}
		setStatusBarText(defaultHiText.toString());
	}

	/**
	 * Set the text in the status bar
	 * 
	 * @param text
	 *            - text to set in the status bar
	 */
	public void setStatusBarText(final String text) {
		accountName.setText(text);
	}
}
