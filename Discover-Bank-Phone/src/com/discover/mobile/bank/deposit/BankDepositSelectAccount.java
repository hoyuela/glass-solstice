package com.discover.mobile.bank.deposit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankConductor;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.help.HelpMenuListFactory;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.ui.modals.HowItWorksModalTop;
import com.discover.mobile.bank.ui.table.ViewPagerListItem;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.help.HelpWidget;
import com.discover.mobile.common.ui.modals.ModalDefaultOneButtonBottomView;

/**
 * Fragment used to display the Check Deposit - Select Account page. This is the first step in the
 * Check Deposit work-flow. Displays a list of accounts the user can select from for depositing a check.
 * 
 * @author henryoyuela
 *
 */
public class BankDepositSelectAccount extends BankDepositBaseFragment {
	/**
	 * Used to log into Android logcat
	 */
	private final String TAG = BankDepositSelectAccount.class.getSimpleName();

	/**
	 * Boolean flag to detect if the user just accepted the terms and conditions,
	 * so that the how it works modal can be shown.
	 */
	private boolean acceptedTerms = false;
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, 
			final Bundle savedInstanceState) {
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		
		/**Hide controls that are not needed*/
		actionButton.setVisibility(View.GONE);
		actionLink.setVisibility(View.GONE);
		noteTitle.setVisibility(View.GONE);
		noteTextMsg.setVisibility(View.GONE);

		//Load the terms boolean from the arguments bundle, then clear it
		clearTermsBoolean(loadTermsBoolean(getArguments()));
		
		if(acceptedTerms)
			showHowItWorksModal();
		
		/**Hide top note as it is not needed for this view**/
		final TextView topNote = (TextView)view.findViewById(R.id.top_note_text);
		topNote.setVisibility(View.GONE);

		return view;
	}
	
	@Override
	protected List<ViewPagerListItem> getViewPagerListContent() {
		//Return null so the base class knows to use getRelativeLayoutListContent instead.
		return null;
	}

	/**
	 * Method returns a list of RelativeLayout objects displaying account information. This method is called by the super
	 * class in onCreateView method.
	 * 
	 * @return Returns a list of RelativeLayout objects 
	 */
	@Override
	protected List<RelativeLayout> getRelativeLayoutListContent() {
		/**Get list of accounts downloaded at login*/
		final List<Account> accounts = BankUser.instance().getAccounts().accounts;
		final List<RelativeLayout> items = new ArrayList<RelativeLayout>();
				
		final Context context = getActivity();
		BankSelectAccountItem item = null;
		
		Collections.sort(accounts, new BankSelectAccountComparable());
		for( int i = 0; i < accounts.size(); i++) {
			final Account account = accounts.get(i);
			
			if( account.isDepositEligible() ) {		
				item = new BankSelectAccountItem(context, account, this);	
				
				if( items.size() > 0 ) {
					item.drawTopStroke(context);
				} 
				
				items.add(item);
			}
		}
		
		if( item != null ){
			item.drawBottomStroke(context);
		}
		
		return items;													
	}

	/**
	 * Method called by base class in onCreateView to determine what the title of the page should be.
	 */
	@Override
	protected String getPageTitle() {
		return getActivity().getResources().getString( R.string.bank_deposit_select_account );
	}
	
	@Override
	protected void onActionButtonClick() {
		//Nothing to do here	
	}

	@Override
	protected void onActionLinkClick() {
		//Nothing to do here		
	}
	
	/**
	 * Click handler for when an item in the list displayed is selected by the user.
	 */
	@Override
	public void onClick(final View sender) {
		super.onClick(sender);
		/**Verify that a BankSelectAccountItem generated the click event*/
		if( sender instanceof BankSelectAccountItem) {
			/**Fetch reference to account object associated with the BankSelectAccountItem that generated the click event*/
			final Account account = ((BankSelectAccountItem)sender).getAccount();
			
			/**Verify account reference is not null*/
			if( null != account ) {
				final Bundle args = getArguments();
				int depositAmount = 0;
				boolean reviewDepositOnFinish = false;
				if(args != null) {
					depositAmount = args.getInt(BankExtraKeys.AMOUNT);
					reviewDepositOnFinish = args.getBoolean(BankExtraKeys.RESELECT_ACCOUNT);
				}
				
				if(reviewDepositOnFinish && account.limits != null){
					//Reset the review deposit bundle boolean to prevent odd navigation issues later.
					args.putBoolean(BankExtraKeys.RESELECT_ACCOUNT, false);
					args.putSerializable(BankExtraKeys.DATA_LIST_ITEM, account);
					BankConductor.navigateToCheckDepositWorkFlow(args, BankDepositWorkFlowStep.ReviewDeposit);
				}
				/**See if the limits for the account have already been downloaded and cached*/
				else if( null != account.limits && !reviewDepositOnFinish) {
					/**Navigate to Check Deposit - Select Amount Page*/
					final Bundle bundle = new Bundle();
					bundle.putSerializable(BankExtraKeys.DATA_LIST_ITEM, account);
					BankConductor.navigateToCheckDepositWorkFlow(bundle, BankDepositWorkFlowStep.SelectAmount);
				} else {
					if(reviewDepositOnFinish){
						final BankNavigationRootActivity current = (BankNavigationRootActivity)DiscoverActivityManager.getActiveActivity();
						Bundle bundle = current.getIntent().getExtras();
						if(bundle == null)
							bundle = new Bundle();
						bundle.putBoolean(BankExtraKeys.RESELECT_ACCOUNT, true);
						bundle.putSerializable(BankExtraKeys.DATA_LIST_ITEM, account);
						bundle.putInt(BankExtraKeys.AMOUNT, depositAmount);
						current.getIntent().putExtras(bundle);
					}
					/**
					 * Send a request to download account limits for the selected account. If successful
					 * it will navigate to select account step 2 in check deposit work flow and send selected account
					 * */
					BankServiceCallFactory.createGetAccountLimits(account, false).submit();
				}
			} else {
				if( Log.isLoggable(TAG, Log.ERROR)) {
					Log.e(TAG, "Unable to retreive account limits");
				}
			}
		}
	}
	
	@Override
	protected int getProgressIndicatorStep() {
		return 0;
	}

	@Override
	public boolean isBackPressDisabled() {
		return false;
	}

	@Override
	public void onBackPressed() {
		//Nothing To Do Here
	}
	
	/**
	 * Show the how it works modal if the acceptedTerms boolean is true. It will be true if the have just accepted 
	 * the terms and have not closed the modal before.
	 */
	public static void showHowItWorksModal() {
			final BankNavigationRootActivity currentActivity = 
					(BankNavigationRootActivity)DiscoverActivityManager.getActiveActivity();
			final HowItWorksModalTop top = new HowItWorksModalTop(currentActivity, null);
			final ModalDefaultOneButtonBottomView bottom = new ModalDefaultOneButtonBottomView(currentActivity, null);
			final HowItWorksModal modal = new HowItWorksModal(currentActivity, top, bottom);
			
			bottom.setButtonText(R.string.ok);
			bottom.getButton().setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View v) {
					modal.dismiss();
				}
			});

			currentActivity.showCustomAlert(modal);

	}
	
	/**
	 * Loads the terms boolean value from a Bundle.
	 * @param arguments a Bundle that was supplied from this fragment.
	 */
	private Bundle loadTermsBoolean(final Bundle bundle) {
		if(bundle != null)
			acceptedTerms = bundle.getBoolean(BankExtraKeys.ACCEPTED_TERMS);
		return bundle;
	}
	
	/**
	 * Clears the boolean flag that is used to show the modal dialog on create if the user has just accepted the terms.
	 * This is used so that on rotation change, when onCreate is called again, that the modal does not get shonw again.
	 * @param bundle
	 */
	private void clearTermsBoolean(final Bundle bundle) {
		if(bundle != null) 
			bundle.putBoolean(BankExtraKeys.ACCEPTED_TERMS, false);
	}

	@Override
	protected void helpMenuOnClick(final HelpWidget help) {
		help.showHelpItems(HelpMenuListFactory.instance().getCheckDepositHelpItems());
	}
}
