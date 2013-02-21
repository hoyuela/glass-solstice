package com.discover.mobile.bank.payees;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.payee.SearchPayeeResult;
import com.discover.mobile.bank.ui.fragments.BankOneButtonFragment;
import com.discover.mobile.bank.ui.table.ViewPagerListItem;

/**
 * Fragment class used to display the Add Payee - Payee Details Page Step 4 of the Add Payee workflow. 
 * The layout of this fragment will change depending on whether the user selected to add a 
 * Verfied Managed Payee or an Un-verified Managed Payee. 
 *
 * If the user select a verified payee, then a SearchPayeeResult is passed to this fragment via a bundle.
 * The fragment can read the SearchPayeeResult using BankExtraKey.DATA_LIST_ITEM. The fragment will also
 * display a message to the user indicating that they have selected a verified payee. The layout for this 
 * case will consists of the following input fields:
 * 
 * Payee Name
 * Nickname
 * Account#
 * Re-Enter Account#
 * Zip Code
 * 
 * If the user selected to Enter Payee Details then they have chosen to enter a potentially unverified Payee.
 * In this case the fields are:
 * 
 * Payee Name
 * Nickname
 * Phone Number
 * Address Line 1
 * Address Line 2 
 * City
 * State
 * Zip Code
 * Account# / Memo
 * 
 * The user will have the option to click on a help button, feedback button, an Add Payee Button, 
 * and a cancel button.
 * 
 * @author henryoyuela
 *
 */
public class BankAddPayeeFragment extends BankOneButtonFragment {
	/**
	 * Reference to a PayeeSearchResult passed in via a bundle from BankSearchSelectPayeeFragment.
	 */
	private SearchPayeeResult payeeSearchResult;
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		
		
		/**Setup Progress Indicator to show Payment Details and Payment Scheduled, on step 1, and hide step 2 **/
		this.progressIndicator.initChangePasswordHeader(0);
		this.progressIndicator.hideStepTwo();
		this.progressIndicator.setTitle(R.string.bank_payee_details, R.string.bank_payee_added, R.string.bank_payee_added);
		
		
		/**Check if an Unverified Managed Payee was passed from Add Payee - Step 3 BankSearchSelectPayeeFragment*/
		final Bundle bundle = this.getArguments();
		if( null != bundle &&  null != bundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM)) {
			payeeSearchResult =  (SearchPayeeResult)bundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM);
			
			/**Make the note title/text visible to the user if Verified Payee*/
			this.noteTitle.setText(R.string.bank_verified_payees_address);
			this.noteTitle.setVisibility(View.VISIBLE);
			this.noteTextMsg.setText(R.string.bank_verified_payees_address_msg);
			this.noteTextMsg.setVisibility(View.VISIBLE);		
		} 
		
		return view;
	}
	
	@Override
	public int getActionBarTitle() {
		return R.string.bank_manage_payees;
	}

	@Override
	protected List<ViewPagerListItem> getContent() {
		
		return null;
	}

	@Override
	protected void onActionButtonClick() {
		
	}

	@Override
	protected void onActionLinkClick() {
		
	}

}
