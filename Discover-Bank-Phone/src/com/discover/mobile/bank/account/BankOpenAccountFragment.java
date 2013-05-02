package com.discover.mobile.bank.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.util.FragmentOnBackPressed;
import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.ui.widgets.AccountToggleView;

/**
 * Fragment class used to display the Open Account page to the user when they do not have any accounts.
 * The application check if a user has any accounts via the BankUser object on successful Customer download.
 * If user has accounts then the BankAccountSummaryFragment is displayed otherwise BankOpenAccountFragment
 * is shown.
 * 
 * @author henryoyuela
 *
 */
public class BankOpenAccountFragment extends BaseFragment implements OnClickListener, FragmentOnBackPressed {

	private View view;

	private AccountToggleView toggleView;

	private static final String SHOW_TOGGLE_KEY = "showToggle";

	private ImageView accountToggleIcon;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.bank_open_account_view, null);
		final Button openAccountBtn = (Button)view.findViewById(R.id.openAccount);

		final TextView salutation = (TextView) view.findViewById(R.id.account_name);
		salutation.setText(setFirstName());

		/**Set the fragment activity as the handler for the button click event*/
		openAccountBtn.setOnClickListener(this);

		accountToggleIcon = (ImageView) view.findViewById(R.id.cardBankIcon);
		toggleView = (AccountToggleView) view.findViewById(R.id.acct_toggle);

		//If card and bank are authenticated then show the down arrow, since we are here
		//Bank must be authenticated already so we only need to check to see if the card is 
		//authenticated.
		if(BankUser.instance().isSsoUser()){
			view.findViewById(R.id.downArrow).setVisibility(View.VISIBLE);
			view.findViewById(R.id.cardBankIcon).setVisibility(View.VISIBLE);
		}

		if (savedInstanceState != null
				&& savedInstanceState.getBoolean(SHOW_TOGGLE_KEY, false)) {
			toggleView.toggleVisibility();
		}

		setupAccountToggle();

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
	public void onSaveInstanceState(final Bundle outState) {
		if(toggleView != null && toggleView.getVisibility() == View.VISIBLE) {
			outState.putBoolean(SHOW_TOGGLE_KEY, true);
		}
		super.onSaveInstanceState(outState);
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

	/**
	 * Determines the placement of the icon upon its layout. It's then used to
	 * measure the postion of the indicator. Additionally, this implements the
	 * listeners for the AccountToggle.
	 */
	private void setupAccountToggle() {
		final ViewTreeObserver vto = accountToggleIcon.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				if(!toggleView.hasIndicatorBeenDrawn()) {
					toggleView.setIndicatorPosition(accountToggleIcon.getLeft(),
							accountToggleIcon.getTop(),
							accountToggleIcon.getWidth(),
							accountToggleIcon.getHeight());
				}
			}
		});

		final ImageView accountToggleArrow = (ImageView) view
				.findViewById(R.id.downArrow);
		accountToggleArrow.setOnClickListener(new AccountToggleListener());
		accountToggleIcon.setOnClickListener(new AccountToggleListener());
	}

	/**
	 * Listener associated with items that hide/show the Account Toggle Widget. 
	 */
	private class AccountToggleListener implements OnClickListener {

		@Override
		public void onClick(final View v) {
			toggleView.toggleVisibility();
		}

	}

	@Override
	public void onBackPressed() {
		if(toggleView.getVisibility() == View.VISIBLE) {
			toggleView.setVisibility(View.INVISIBLE);
		}

	}

	@Override
	public boolean isBackPressDisabled() {
		if(toggleView.getVisibility() == View.VISIBLE) {
			return true;
		}

		return false;
	}
}
