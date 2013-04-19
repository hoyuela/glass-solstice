package com.discover.mobile.bank.payees;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.error.BankError;
import com.discover.mobile.bank.services.error.BankErrorResponse;
import com.discover.mobile.bank.services.payee.AddPayeeDetail;
import com.discover.mobile.bank.services.payee.PayeeDetail;
import com.discover.mobile.bank.services.payee.SearchPayeeResult;
import com.google.common.base.Strings;

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
 * 
 * The user will have the option to click on a help button, feedback button, an Add Payee Button, 
 * and a cancel button.
 * 
 * @author henryoyuela
 *
 */
public class BankAddManagedPayeeFragment extends BankAddPayeeFragment {
	
	/**Enum used to fetch BankEditDetail objects from the layout hosted by this fragment*/
	private enum ManagedPayeeFields {
		PayeeName,
		PayeeNickName,
		PayeeAccountNumber,
		PayeeAccountNumberConfirmed,
		PayeeZipCode,
		Last
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		
		/**Check if this is the first time this fragment is launched*/
		if( null == savedInstanceState ) {
			/**Set the first field to have focus at start-up, this flag is checked at onResume*/
			final BankEditDetail name = getFieldDetail(ManagedPayeeFields.PayeeNickName.ordinal());
			if( name != null) {
				final String key = name.getTopLabel().getText().toString();
				final Bundle bundle = getArguments();
				if( bundle != null ) {
					bundle.putBoolean(key, true);
				}
			}
		}
		
		return view;
	}
	
	/**
	 * This method is called by the onCreateView of the base class only when the fragment is created for the 
	 * first time for the instance of the object.
	 */
	@Override
	protected void initializeData( final Bundle bundle ) {
		final Object item = bundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM);
		
		if( null != bundle &&  null != item ) {
			if( item instanceof SearchPayeeResult ) {
				payeeSearchResult =  (SearchPayeeResult)item;	
				detail.name = payeeSearchResult.name;
				detail.verified = true;
				detail.merchantNumber = payeeSearchResult.merchantNumber;
				detail.isZipRequired = payeeSearchResult.isZipRequired();
				
				/**Set flag to false so that when service call is called it adds payee*/
				isUpdate = false;
			} else if( item instanceof PayeeDetail ) {
				detail.name = ((PayeeDetail)item).name;
				detail.nickName = ((PayeeDetail)item).nickName;
				detail.verified = true;
				detail.merchantNumber = ((PayeeDetail) item).merchantNumber;
				detail.isZipRequired = ((PayeeDetail) item).isZipRequired;
				detail.accountNumber = "";
				detail.accountNumberConfirmed = "";
				detail.zip = ((PayeeDetail)item).zip;
				
				/**Set flag to true so that when service call is called it updates payee*/
				isUpdate = true;
			}
		} 
	}
	
	/**
	 * This method is called by the onCreateView of the base class every time it is called.
	 */
	@Override
	protected void initializeUi(final View mainView) {
		super.initializeUi(mainView);
		
		/**Hide top note as it is not needed for this view**/
		final TextView topNote = (TextView)mainView.findViewById(R.id.top_note_text);
		topNote.setVisibility(View.GONE);
		
		noteTitle.setText(R.string.bank_verified_payees_address);
		noteTitle.setVisibility(View.VISIBLE);
		noteTextMsg.setText(R.string.bank_verified_payees_address_msg);
		noteTextMsg.setVisibility(View.VISIBLE);
	}
	
	/**
	 * Method used to generate a list of RelativeLayouts that display the information
	 * stored in the detail data member on the layout hosted by this fragment.
	 */
	@Override
	protected List<RelativeLayout> getRelativeLayoutListContent() {
		return PayeeDetailListGenerator.getPayeeDetailList(getActivity(), detail);
	}
	
	/**
	 * Restores the widget's states from before rotation.
	 * 
	 * @param savedInstanceState
	 *            the Bundle from which the data is loaded.
	 */
	@Override
	protected void restoreState(final Bundle bundle) {
		if( detail != null ) {
			final BankEditDetail name = getFieldDetail(ManagedPayeeFields.PayeeName.ordinal());
			final BankEditDetail nickName =  getFieldDetail(ManagedPayeeFields.PayeeNickName.ordinal()); 
			final BankEditDetail acctNum = getFieldDetail(ManagedPayeeFields.PayeeAccountNumber.ordinal());
			final BankEditDetail acctConfirm = getFieldDetail(ManagedPayeeFields.PayeeAccountNumberConfirmed.ordinal());

			name.setText(detail.name);
			nickName.setText(detail.nickName);
			acctNum.setText(detail.accountNumber);
			acctConfirm.setText(detail.accountNumberConfirmed);

			if(detail.isZipRequired){
				final BankEditDetail zip = getFieldDetail(ManagedPayeeFields.PayeeZipCode.ordinal());
				zip.setText(detail.zip);
			}	
		} 
	}
	
	/**
	 * Callback method for displaying inline errors.
	 */
	@Override
	public boolean handleError(final BankErrorResponse msgErrResponse) {
		super.handleError(msgErrResponse);
				
		boolean handled = false;
		
		for( final BankError error : msgErrResponse.errors ) {
			if( !Strings.isNullOrEmpty(error.name) ) {
				/**Check if error is for amount field*/
				if( error.name.equalsIgnoreCase(AddPayeeDetail.NICKNAME_FIELD)) {
					setErrorString(ManagedPayeeFields.PayeeNickName.ordinal(), error.message);
				}
				/**Check if error is for Payment method field*/
				else if( error.name.equalsIgnoreCase(AddPayeeDetail.ACCOUNT_NUMBER_FIELD)) {
					setErrorString(ManagedPayeeFields.PayeeAccountNumber.ordinal(),error.message);
					setErrorString(ManagedPayeeFields.PayeeAccountNumberConfirmed.ordinal(), error.message);
				}
				/**Check if error is for Deliver by field*/
				else if( error.name.equalsIgnoreCase(AddPayeeDetail.BILLING_POSTAL_CODE_FIELD) ) {
					setErrorString(ManagedPayeeFields.PayeeZipCode.ordinal(),error.message);
				}
				/**Show error at the top of the screen */
				else {
					showGeneralError(error.message);
				}
				
				handled = true;
			} else if( !Strings.isNullOrEmpty(error.message)) {				
				showGeneralError(error.message);
				
				handled = true;
				
				scrollToTop();
			}
		}
	
		return handled;
	}
	
	
	/**
	 * Generates an AddPayeeDetail object using the text values stored in each BankEditDetail that is
	 * part of the content list.
	 * 
	 * @return Reference to an AddPayeeDetail object with information of the Payee that is to be added.
	 */
	@Override
	protected AddPayeeDetail getPayeeDetail() {
		if( content != null ) {
			final BankEditDetail nickName =  ((BankEditDetail)content.get(ManagedPayeeFields.PayeeNickName.ordinal())); 
			final BankEditDetail acctNum = ((BankEditDetail)content.get(ManagedPayeeFields.PayeeAccountNumber.ordinal()));
			final BankEditDetail name = ((BankEditDetail)content.get(ManagedPayeeFields.PayeeName.ordinal()));
			final BankEditDetail acctConfirm = ((BankEditDetail)content.get(ManagedPayeeFields.PayeeAccountNumberConfirmed.ordinal()));

			detail.name = name.getText();
			detail.nickName = nickName.getText();
			detail.accountNumber =  acctNum.getText();
			detail.accountNumberConfirmed = acctConfirm.getText();

			/**If Zip is required then set zip for the payee being added*/
			if(payeeSearchResult != null && payeeSearchResult.isZipRequired() ) {
				final BankEditDetail zip = ((BankEditDetail)content.get(ManagedPayeeFields.PayeeZipCode.ordinal()));

				detail.zip =  zip.getText();
				detail.isZipRequired = true;
			} else {
				detail.isZipRequired = false;
			}
		}

		return detail;
	}
	
	/**
	 * Shows inline errors for all BankEditDetail objects if fields do not validate correctly.
	 */
	@Override
	protected void updateFieldsAppearance() {
		super.updateFieldsAppearance();
		
		/**Verify if account numbers entered by the user's match otherwise show inline error*/
		if( !doAcctNumbersMatch() ) {
			final BankEditDetail acctConfirm = ((BankEditDetail)content.get(ManagedPayeeFields.PayeeAccountNumberConfirmed.ordinal()));

			/**Show non-matching acct# error inline*/
			acctConfirm.showErrorLabel(R.string.bank_nonmatching_acct);
		}
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
	 * Method used to validate all input fields to make sure they meet the 
	 * criteria associated with each at creation. Refer to PayeeDetailListGenerator
	 * for the criteria associated with each BankEditDetail object.
	 * 
	 * @return True if all fields validate correctly, false otherwise.
	 */
	@Override
	protected boolean canProceed() {
		boolean ret = super.canProceed();

		/**Make sure account number matches re-enter account number*/
		ret &= doAcctNumbersMatch();
		
		return ret;
	}

	
}
