package com.discover.mobile.bank.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.util.BankNeedHelpFooter;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.DiscoverActivityManager;

/**
 * Fragment class used to display the Open Account page to the user when they do not have any accounts.
 * The application check if a user has any accounts via the BankUser object on successful Customer download.
 * If user has accounts then the BankAccountSummaryFragment is displayed otherwise BankOpenAccountFragment
 * is shown.
 * 
 * @author henryoyuela
 *
 */
public class BankOpenAccountFragment extends BaseFragment implements OnClickListener {
	private BankNeedHelpFooter helpFooter;
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.bank_open_account_view, null);
		final Button openAccountBtn = (Button)view.findViewById(R.id.openAccount);

		final TextView salutation = (TextView) view.findViewById(R.id.account_name);
		salutation.setText(setFirstName());
		
		/**Set the fragment activity as the handler for the button click event*/
		openAccountBtn.setOnClickListener(this);

		/**Create footer that will listen when user taps on Need Help Number to dial*/
		helpFooter = new BankNeedHelpFooter((ViewGroup)view);
		helpFooter.setToDialNumberOnClick(com.discover.mobile.bank.R.string.bank_need_help_number_text);

		return view;
	}

	/** Set the first name in the status bar. The first name can sometimes be 
	 * returned in all caps and only the first letter should be capitalized. 
	 */
	private String setFirstName() {
		final String firstName = BankUser.instance().getCustomerInfo().name.type;
		final String name = firstName.toLowerCase();
		final String upperString = name.substring(0,1).toUpperCase() + name.substring(1);
		return "Hi, " + upperString;
	}
	
	@Override
	public int getActionBarTitle() {
		return BaseFragment.NO_TITLE;
	}

	@Override
	public void onClick(final View arg0) {
		BankConductor.navigateToBrowser(BankUrlManager.getOpenAccountUrl());	
	}

	@Override
	public void onResume() {
		super.onResume();

		/**Disable Sliding Navigation Menu and Hide Menu Button in Action Bar**/
		final BankNavigationRootActivity activity = (BankNavigationRootActivity)DiscoverActivityManager.getActiveActivity();	
		activity.enableSlidingMenu(false);
		activity.showNavigationMenuButton(false);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.ACCOUNT_SUMMARY_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.OPEN_NEW_ACCOUNT_SECTION;
	}


}
