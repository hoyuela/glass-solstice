package com.discover.mobile.bank.payees;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.discover.mobile.BankMenuItemLocationIndex;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.help.HelpMenuListFactory;
import com.discover.mobile.bank.services.payee.PayeeDetail;
import com.discover.mobile.bank.ui.fragments.BankOneButtonFragment;
import com.discover.mobile.bank.ui.table.ViewPagerListItem;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.help.HelpWidget;

/**
 * Fragment used to display the Payee Confirmation Modal. User will be allowed to confirm
 * whether they in fact want to delete a payee or not.
 * 
 * @author henryoyuela
 *
 */
public class BankDeletePayeeModal extends BankOneButtonFragment {
	/**
	 * Reference to payee that will be deleted when action button is pressed
	 */
	private PayeeDetail payee;
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		
		Bundle bundle = (savedInstanceState != null) ? savedInstanceState : getArguments();
		
		if( bundle != null ) {
			payee = (PayeeDetail)bundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM);
		}
		
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		
		/**Update screen with text strings for delete payee*/
		noteTextMsg.setText(R.string.bank_payee_delete_body);
		actionButton.setText(R.string.bank_payee_delete_action);
		actionLink.setText(R.string.bank_payee_delete_link);
		noteTextMsg.setVisibility(View.VISIBLE);
		
		/**Set padding for body text*/
		final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)noteTextMsg.getLayoutParams();
		params.rightMargin = (int) (this.getResources().getDimension(R.dimen.help_widget_width)/2);
		noteTextMsg.setLayoutParams(params);
		
		/**Hide controls that are not needed*/
		contentTable.setVisibility(View.GONE);
		noteTitle.setVisibility(View.GONE);
		actionLink.setVisibility(View.GONE);
			
		return view;
	}
	
	/**
	 * Method to be overridden by sub-class if a title is required for the layout. Otherwise
	 * the title will be hidden.
	 * 
	 * @return Return String that holds the text to display in the page title. 
	 * 		   Return null if page title should be hidden.
	 */
	@Override
	protected String getPageTitle() {
		return getResources().getString(R.string.bank_payee_delete_title);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

	}

	@Override
	protected List<ViewPagerListItem> getViewPagerListContent() {
		return null;
	}

	@Override
	protected List<RelativeLayout> getRelativeLayoutListContent() {

		return null;
	}

	@Override
	protected void onActionButtonClick() {
		BankServiceCallFactory.createDeletePayeeServiceCall(payee).submit();
	}

	@Override
	protected void onActionLinkClick() {
		/*Navigate back to the screen that brought this page to the foreground*/
		final Activity curActivity = DiscoverActivityManager.getActiveActivity();
		curActivity.onBackPressed();
	}

	@Override
	protected void helpMenuOnClick(final HelpWidget help) {
		help.showHelpItems(HelpMenuListFactory.instance().getPayBillsHelpItems());
	}

	@Override
	public int getActionBarTitle() {
		return R.string.bank_manage_payees;
	}

	@Override
	public int getGroupMenuLocation() {
		return BankMenuItemLocationIndex.PAY_BILLS_GROUP;
	}

	@Override
	public int getSectionMenuLocation() {
		return BankMenuItemLocationIndex.MANAGE_PAYEES_SECTION;
	}

	@Override
	public void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putAll(getArguments());
	
	}
}
