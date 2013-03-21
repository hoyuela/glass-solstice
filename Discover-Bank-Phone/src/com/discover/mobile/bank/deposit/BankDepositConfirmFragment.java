package com.discover.mobile.bank.deposit;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.BankRotationHelper;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.account.BankAccountSummaryFragment;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.help.HelpMenuListFactory;
import com.discover.mobile.bank.navigation.BankNavigationRootActivity;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.deposit.DepositDetail;
import com.discover.mobile.common.help.HelpWidget;

/**
 * Fragment used to display the Check Deposit - Confirmation Page after submitting a check deposit
 * successfully. The information displayed to the user is based on the user entered information in
 * the check deposit review page and what is returned from the server after executing the Bank Web Service
 * Create a Deposit. This class inherits from BankDepositBaseFragment which uses the layout defined 
 * in res/layout/bank_one_button_layout.xml.
 * 
 * @author henryoyuela
 *
 */
public class BankDepositConfirmFragment extends BankDepositBaseFragment {
	/**
	 * Reference to bundle provided in onCreateView or via getArguments() depending on what created the fragment.
	 */
	private Bundle bundle = null;
	/**
	 * Reference to a DepositDetail object which is provided via the bundle on creation of fragment.
	 */
	private DepositDetail depositDetail;
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		
		bundle = ( null != savedInstanceState ) ? savedInstanceState : getArguments();
		
		/**Store bundle provided to restore state of fragment onResume*/
		if( null != bundle ) {
			depositDetail = (DepositDetail)bundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM);
		}
		
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		
		/**Hide controls that are not needed*/
		noteTitle.setVisibility(View.GONE);
		noteTextMsg.setVisibility(View.GONE);
		feedbackLink.setVisibility(View.GONE);
		
		/**Hide top note as it is not needed for this view**/
		final TextView topNote = (TextView)view.findViewById(R.id.top_note_text);
		topNote.setVisibility(View.GONE);
		
		/**Show Action Button text in single button on screen*/
		actionButton.setText(R.string.bank_deposit_received_actionbutton);
		
		/**Show Link text in link on screen*/
		actionLink.setText(R.string.bank_deposit_received_actionlink);
			
		return view;
	}
	
	@Override
	public boolean isBackPressDisabled() {
		return true;
	}
	
	@Override
	public void onBackPressed() {
		final BankNavigationRootActivity activity = (BankNavigationRootActivity)this.getActivity();
		activity.popTillFragment(BankDepositSelectAccount.class);
	}
	
	/**
	 * Used to specify the step in the bread crumb displayed above this page.
	 */
	@Override
	protected int getProgressIndicatorStep() {
		return 3;
	}

	@Override
	protected List<RelativeLayout> getRelativeLayoutListContent() {
		return BankDepositListGenerator.getDepositConfirmationList(getActivity(), depositDetail);
	}

	@Override
	protected void onActionButtonClick() {
		final BankNavigationRootActivity activity = (BankNavigationRootActivity)this.getActivity();
		activity.popTillFragment(BankDepositSelectAccount.class);
	}

	@Override
	protected void onActionLinkClick() {
		final BankNavigationRootActivity activity = (BankNavigationRootActivity)this.getActivity();
		activity.popTillFragment(BankAccountSummaryFragment.class);
		
		final Account account = BankUser.instance().getAccount( Integer.toString(depositDetail.account) );
		
		//Navigate to Scheduled Transactions Activity Page
		if( account != null ) {
			final String link = account.getLink(Account.LINKS_SCHEDULED_ACTIVITY);
	
			//Set Current Account to be accessed by other objects in the application
			BankUser.instance().setCurrentAccount(account);
	
			//Clear the rotation helper
			BankRotationHelper.getHelper().setBundle(null);
	
			//Send Request to download the current accounts posted activity
			BankServiceCallFactory.createGetActivityServerCall(link).submit();
		}
	}
	
	/**
	 * Returns a string that is displayed as the title on the fragment layout.
	 */
	@Override
	protected String getPageTitle() {		
		return this.getActivity().getResources().getString(R.string.bank_deposit_received_title);
	}

	@Override
	protected void helpMenuOnClick(final HelpWidget help) {
		help.showHelpItems(HelpMenuListFactory.instance().getCheckDepositHelpItems());
	}

	/**
	 * Method used to store the state of the fragment and support orientation change
	 */
	@Override
	public void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		
		/**Store values stored in each field*/
		outState.putSerializable(BankExtraKeys.DATA_LIST_ITEM, depositDetail);
	}
	
}
