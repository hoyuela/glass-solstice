package com.discover.mobile.bank.payees;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.services.error.BankError;
import com.discover.mobile.bank.services.error.BankErrorResponse;
import com.discover.mobile.bank.services.payee.AddPayeeDetail;
import com.discover.mobile.bank.services.payee.AddUnmanagedPayee;
import com.discover.mobile.bank.services.payee.SearchPayeeResult;
import com.google.common.base.Strings;


/**
 * Fragment class used to display the Add Payee - Payee Details Page Step 4 of the Add Payee workflow. 
 * Details Page Step 4 of the Add Payee workflow. 
 * 
 * When the user select to Enter Payee Details from Step 3 then they have chosen to enter an unverified Payee.
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
public class BankAddUnmanagedPayeeFragment extends BankAddPayeeFragment {

	/**Enum used to fetch BankEditDetail objects from the layout hosted by this fragment*/
	private enum UnmanagedPayeeFields {
		PayeeName,
		PayeeNickName,
		PhoneNumber,
		AddressLine1,
		AddressLine2,
		City,
		State,
		ZipCode,
		Memo,
		Last
	}
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		
		if( null == savedInstanceState ) {
			final BankEditDetail name = getFieldDetail(UnmanagedPayeeFields.PayeeName.ordinal());
			if( name != null) {
				final String key = name.getTopLabel().getText().toString();
				if( bundle != null ) {
					bundle.putBoolean(key, true);
				}
			}
		}
		
		return view;
	}
	
	/**
	 * Callback method for displaying inline errors.
	 */
	@Override
	public boolean handleError(final BankErrorResponse msgErrResponse) {
		super.handleError(msgErrResponse);
		
		for( final BankError error : msgErrResponse.errors ) {
			if( !Strings.isNullOrEmpty(error.name) ) {
				/**Check if error is for Payee field*/
				if( error.name.equals(AddUnmanagedPayee.NAME_FIELD) ) {
					setErrorString(UnmanagedPayeeFields.PayeeName.ordinal(),error.message);
				}
				/**Check if error is for Nick name field*/
				else if( error.name.equals(AddUnmanagedPayee.NICKNAME_FIELD)) {
					setErrorString(UnmanagedPayeeFields.PayeeNickName.ordinal(), error.message);
				}
				/**Check if error is for address line 1 field*/
				else if( error.name.equals(AddUnmanagedPayee.NAME_PHONE)) {
					setErrorString(UnmanagedPayeeFields.PayeeNickName.ordinal(), error.message);
				}
				/**Check if error is for address line 1 field*/
				else if( error.name.equals(AddUnmanagedPayee.NAME_ADDRESS_LINE1)) {
					setErrorString(UnmanagedPayeeFields.AddressLine1.ordinal(), error.message);
				}
				/**Check if error is for address line 1 field*/
				else if( error.name.equals(AddUnmanagedPayee.NAME_ADDRESS_LINE2)) {
					setErrorString(UnmanagedPayeeFields.AddressLine2.ordinal(), error.message);
				}
				/**Check if error is for address line 1 field*/
				else if( error.name.equals(AddUnmanagedPayee.NAME_ADDRESS_CITY)) {
					setErrorString(UnmanagedPayeeFields.City.ordinal(), error.message);
				}
				/**Check if error is for address line 1 field*/
				else if( error.name.equals(AddUnmanagedPayee.NAME_ADDRESS_STATE)) {
					setErrorString(UnmanagedPayeeFields.State.ordinal(), error.message);
				}
				/**Check if error is for address line 1 field*/
				else if( error.name.equals(AddUnmanagedPayee.NAME_ADDRESS_ZIP)) {
					setErrorString(UnmanagedPayeeFields.ZipCode.ordinal(), error.message);
				}
				/**Check if error is for address line 1 field*/
				else if( error.name.equals(AddUnmanagedPayee.NAME_MEMO)) {
					setErrorString(UnmanagedPayeeFields.Memo.ordinal(), error.message);
				}
				/**Show error at the top of the screen */
				else {
					showGeneralError(error.message);
				}
			}
		}
		return true;
	}

	/**
	 * This method is called by the onCreateView of the base class only when the fragment is created for the 
	 * first time for the instance of the object.
	 */
	@Override
	protected void initializeData(final Bundle bundle) {
		detail = new AddUnmanagedPayee();
		
		if( null != bundle &&  null != bundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM)) {		
			payeeSearchResult =  (SearchPayeeResult)bundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM);	
			detail.name = payeeSearchResult.name;
			detail.nickName = detail.name;
		} 
	}

	/**
	 * Generates an AddUnmanagedPayee object using the text values stored in each BankEditDetail that is
	 * part of the content list.
	 * 
	 * @return Reference to an AddUnmanagedPayee object with information of the Payee that is to be added.
	 */
	@Override
	protected AddPayeeDetail getPayeeDetail() {
		if( content != null ) {			
			final AddUnmanagedPayee payee = (AddUnmanagedPayee)detail;
			
			payee.name = getFieldText(UnmanagedPayeeFields.PayeeName.ordinal());
			payee.nickName = getFieldText(UnmanagedPayeeFields.PayeeNickName.ordinal());
			payee.phone = getFieldText(UnmanagedPayeeFields.PhoneNumber.ordinal()); 
			payee.addressLine1 = getFieldText(UnmanagedPayeeFields.AddressLine1.ordinal());
			payee.addressLine2 = getFieldText(UnmanagedPayeeFields.AddressLine2.ordinal());
			payee.addressCity = getFieldText(UnmanagedPayeeFields.City.ordinal());
			payee.addressState = getFieldText(UnmanagedPayeeFields.State.ordinal());
			payee.addressZip = getFieldText(UnmanagedPayeeFields.ZipCode.ordinal());
			payee.accountNumber = getFieldText(UnmanagedPayeeFields.Memo.ordinal());
		}

		return detail;
	}


	/**
	 * Method called by onResume() to resume the state of the UI.
	 */
	@Override
	protected void restoreState(final Bundle bundle) {
		if( detail != null ) {
			final AddUnmanagedPayee payee = (AddUnmanagedPayee)detail;
			
			setFieldText(UnmanagedPayeeFields.PayeeName.ordinal(), detail.name);
			setFieldText(UnmanagedPayeeFields.PayeeNickName.ordinal(), detail.nickName);
			setFieldText(UnmanagedPayeeFields.PhoneNumber.ordinal(), payee.phone);
			setFieldText(UnmanagedPayeeFields.AddressLine1.ordinal(), payee.addressLine1);
			setFieldText(UnmanagedPayeeFields.AddressLine2.ordinal(), payee.addressLine2);
			setFieldText(UnmanagedPayeeFields.City.ordinal(), payee.addressCity);
			setFieldText(UnmanagedPayeeFields.State.ordinal(), payee.addressState);
			setFieldText(UnmanagedPayeeFields.ZipCode.ordinal(), payee.addressZip);	
			setFieldText(UnmanagedPayeeFields.Memo.ordinal(), payee.accountNumber);
		} 	
	}

	/**
	 * Method called by onCreateView of the base class to populate the layout with BankEditFields that displays
	 * the Payee information in a linear layout.
	 */
	@Override
	protected List<RelativeLayout> getRelativeLayoutListContent() {
		return PayeeDetailListGenerator.getUnmanagedPayeeDetailList(getActivity(), (AddUnmanagedPayee)detail);
	}
	
}
