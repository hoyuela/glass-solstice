package com.discover.mobile.bank.payees;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.BankServiceCallFactory;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.payee.AddPayeeDetail;
import com.discover.mobile.bank.services.payee.SearchPayeeResult;
import com.discover.mobile.bank.ui.fragments.BankOneButtonFragment;
import com.discover.mobile.bank.ui.table.ViewPagerListItem;

/**
 * Fragment class used to display the Add Payee - Payee Details Page Step 4 of the Add Payee workflow. 
 * Details Page Step 4 of the Add Payee workflow. 
 * 
 * The layout of this fragment will change depending on whether the user selected to add a 
 * Verfied Managed Payee or an Un-verified Managed Payee. 
 *
 * If the user select a verified payee, then a SearchPayeeResult is passed to this fragment via a bundle.
 * The fragment can read the SearchPayeeResult using BankExtraKey.DATA_LIST_ITEM. The fragment will also
 * display a message to the user indicating that they have selected a verified payee. The layout for this 
 * case will consists of the following input fields:
 * 
 * 		Payee Name
 * 		Nickname
 * 		Account#
 * 		Re-Enter Account#
 * 		Zip Code
 * 
 * If the user selected to Enter Payee Details then they have chosen to enter a potentially unverified Payee.
 * In this case the fields are:
 * 
 * 		Payee Name
 * 		Nickname
 * 		Phone Number
 * 		Address Line 1
 * 		Address Line 2 
 * 		City
 * 		State
 * 		Zip Code
 * 		Account# / Memo
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
	/**
	 * Reference to a AddPayeeDetail object used to hold the information of the Payee that will be added.
	 */
	final AddPayeeDetail detail = new AddPayeeDetail();
	
	private enum ManagedPayeeFields {
		PayeeName,
		PayeeNickName,
		PayeeAccountNumber,
		PayeeAccountNumberConfirmed,
		PayeeZipCode
	}
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		
		/**Check if an Unverified Managed Payee was passed from Add Payee - Step 3 BankSearchSelectPayeeFragment*/
		final Bundle bundle = this.getArguments();
		if( null != bundle &&  null != bundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM)) {
			payeeSearchResult =  (SearchPayeeResult)bundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM);	
			detail.name = payeeSearchResult.name;
			detail.verified = true;
			detail.nickName = payeeSearchResult.nickName;
			detail.merchantNumber = payeeSearchResult.merchantNumber;
		} 
		
		final View view = super.onCreateView(inflater, container, savedInstanceState);
				
		/**Setup Progress Indicator to show Payment Details and Payment Scheduled, on step 1, and hide step 2 **/
		this.progressIndicator.initChangePasswordHeader(0);
		this.progressIndicator.hideStepTwo();
		this.progressIndicator.setTitle(R.string.bank_payee_details, R.string.bank_payee_added, R.string.bank_payee_added);
			
		/**Make the note title/text visible to the user if Verified Payee*/
		if( null != payeeSearchResult ) {
			this.noteTitle.setText(R.string.bank_verified_payees_address);
			this.noteTitle.setVisibility(View.VISIBLE);
			this.noteTextMsg.setText(R.string.bank_verified_payees_address_msg);
			this.noteTextMsg.setVisibility(View.VISIBLE);
		}
		
		this.actionButton.setText(R.string.bank_add_payee);
		
		this.actionLink.setText(R.string.bank_add_cancel);
		
		return view;
	}
	
	@Override
	public int getActionBarTitle() {
		return R.string.bank_manage_payees;
	}
	
	/**
	 * Method used to validate all input fields to make sure they meet the 
	 * criteria associated with each at creation. Refer to PayeeDetailListGenerator
	 * for the criteria associated with each BankEditDetail object.
	 * 
	 * @return True if all fields validate correctly, false otherwise.
	 */
	public boolean canProceed() {
		boolean ret = true;
		
		/**Iterate through each BankEditDetail object and make sure their editable field validates correctly*/
		if( content != null){
			for(final Object element : content) {
				if( element instanceof BankEditDetail) {
					ret = ((BankEditDetail)element).getEditableField().isValid();
					
					if( !ret ) {					
						break;
					}
				}
			}
		}
		
		/**Make sure account number matches re-enter account number*/
		if( ret ) {
			ret = doAcctNumbersMatch();
		}
		
		return ret;
	}
	
	/**
	 * Method used to check if values entered by the user in account # and re-entered account #  match.
	 * 
	 * @return Returns true account numbers match, false otherwise.
	 */
	private boolean doAcctNumbersMatch() {
		final BankEditDetail acctNum = ((BankEditDetail)content.get(ManagedPayeeFields.PayeeAccountNumber.ordinal()));
		final BankEditDetail acctConfirm = ((BankEditDetail)content.get(ManagedPayeeFields.PayeeAccountNumberConfirmed.ordinal()));
		
		final String accountNum =  acctNum.getEditableField().getText().toString();;
		final String accountMatch =  acctConfirm.getEditableField().getText().toString();
		
		return accountNum.equals(accountMatch); 
	}
	
	/**
	 * Shows inline errors for all BankEditDetail objects if fields do not validate correctly.
	 */
	public void updateFieldsAppearance() {
		/**Iterate through each BankEditDetail and ensure it validates correctly otherwise show inline errors*/
		if( content != null){
			for(final Object element : content) {
				if( element instanceof BankEditDetail) {
					((BankEditDetail)element).setEditMode(false);
					((BankEditDetail)element).getEditableField().updateAppearanceForInput();
				}
			}
		}
		
		/**Verify if account numbers entered by the user's match otherwise show inline error*/
		if( !doAcctNumbersMatch() ) {
			final BankEditDetail acctConfirm = ((BankEditDetail)content.get(ManagedPayeeFields.PayeeAccountNumberConfirmed.ordinal()));
			
			/**Show non-matching acct# error inline*/
			acctConfirm.showErrorLabel(R.string.bank_nonmatching_acct);
		}
		
	}
	
	/**
	 * Generates an AddPayeeDetail object using the text values stored in each BankEditDetail that is
	 * part of the content list.
	 * 
	 * @return Reference to an AddPayeeDetail object with information of the Payee that is to be added.
	 */
	public AddPayeeDetail getPayeeDetail() {
		if( content != null ) {
			final BankEditDetail nickName =  ((BankEditDetail)content.get(ManagedPayeeFields.PayeeNickName.ordinal())); 
			final BankEditDetail acctNum = ((BankEditDetail)content.get(ManagedPayeeFields.PayeeAccountNumber.ordinal()));
			final BankEditDetail zip = ((BankEditDetail)content.get(ManagedPayeeFields.PayeeZipCode.ordinal()));
			
			detail.nickName = nickName.getEditableField().getText().toString();
			detail.accountNumber =  acctNum.getEditableField().getText().toString();
			detail.zip =  zip.getEditableField().getText().toString();
		}
		
		return detail;
	}

	/**
	 * Action Button onClick() Handler, which triggers the request to Add a Payee using
	 * the information returned by getPayeeDetail method. All fields must validate correctly
	 * in order for the service call to be made, otherwise inline errors are shown for
	 * each field with invalid content.
	 */
	@Override
	protected void onActionButtonClick() {
		if( canProceed() ) {
			BankServiceCallFactory.createAddPayeeRequest(getPayeeDetail()).submit();
		} else {
			updateFieldsAppearance();
		}
	}

	@Override
	protected void onActionLinkClick() {
		//TO BE IMPLEMENTED LATER
	}

	/**
	 * Method Not Used
	 */
	@Override
	protected List<ViewPagerListItem> getViewPagerListContent() {
		return null;
	}

	/**
	 * Method used to generate a list of RelativeLayouts that display the information
	 * stored in the detail data member on the layout hosted by this fragment.
	 */
	@Override
	protected List<RelativeLayout> getRelativeLayoutListContent() {
		return PayeeDetailListGenerator.getPayeeDetailList(getActivity(), detail);
	}

}
