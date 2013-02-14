package com.discover.mobile.bank.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.discover.mobile.bank.BankNavigator;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.urlmanager.BankUrlManager;

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

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.bank_open_account_view, null);
		final Button openAccountBtn = (Button)view.findViewById(R.id.openAccount);
		
		/**Set the fragment activity as the handler for the button click event*/
		openAccountBtn.setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public int getActionBarTitle() {
		return R.string.bank_open_account;
	}

	@Override
	public void onClick(final View arg0) {
		BankNavigator.navigateToBrowser(BankUrlManager.getOpenAccountUrl());	
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		/**Disable Sliding Navigation Menu and Hide Menu Button in Acction Bar**/
		final BankNavigationRootActivity activity = (BankNavigationRootActivity)DiscoverActivityManager.getActiveActivity();	
		activity.enableSlidingMenu(false);
		activity.showNavigationMenuButton(false);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		
		/**Enable Sliding Navigation Menu and Show Menu Button in Acction Bar**/
		final BankNavigationRootActivity activity = (BankNavigationRootActivity)DiscoverActivityManager.getActiveActivity();	
		activity.enableSlidingMenu(true);
		activity.showNavigationMenuButton(true);
	}
	
	

}
