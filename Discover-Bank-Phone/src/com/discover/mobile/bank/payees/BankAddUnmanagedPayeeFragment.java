package com.discover.mobile.bank.payees;

import java.util.List;

import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.services.error.BankError;
import com.discover.mobile.bank.services.error.BankErrorResponse;
import com.discover.mobile.bank.services.payee.AddPayeeDetail;
import com.discover.mobile.bank.services.payee.AddUnmanagedPayee;
import com.discover.mobile.bank.services.payee.PayeeDetail;
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
			/**Set focus to nick-name onResume()*/
			final BankEditDetail nickName = getFieldDetail(UnmanagedPayeeFields.PayeeNickName.ordinal());
			if( nickName != null) {
				final String key = nickName.getTopLabel().getText().toString();
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
				/**Check if error is for phone number field*/
				else if( error.name.equals(AddUnmanagedPayee.NAME_PHONE)) {
					setErrorString(UnmanagedPayeeFields.PayeeNickName.ordinal(), error.message);
				}
				/**Check if error is for address line 1 field*/
				else if( error.name.equals(AddUnmanagedPayee.NAME_ADDRESS_LINE1)) {
					setErrorString(UnmanagedPayeeFields.AddressLine1.ordinal(), error.message);
				}
				/**Check if error is for address line 2 field*/
				else if( error.name.equals(AddUnmanagedPayee.NAME_ADDRESS_LINE2)) {
					setErrorString(UnmanagedPayeeFields.AddressLine2.ordinal(), error.message);
				}
				/**Check if error is for address city field*/
				else if( error.name.equals(AddUnmanagedPayee.NAME_ADDRESS_CITY)) {
					setErrorString(UnmanagedPayeeFields.City.ordinal(), error.message);
				}
				/**Check if error is for address state field*/
				else if( error.name.equals(AddUnmanagedPayee.NAME_ADDRESS_STATE)) {
					setErrorString(UnmanagedPayeeFields.State.ordinal(), error.message);
				}
				/**Check if error is for address zip field*/
				else if( error.name.equals(AddUnmanagedPayee.NAME_ADDRESS_ZIP)) {
					setErrorString(UnmanagedPayeeFields.ZipCode.ordinal(), error.message);
				}
				/**Check if error is for address memo field*/
				else if( error.name.equals(AddUnmanagedPayee.NAME_MEMO)) {
					setErrorString(UnmanagedPayeeFields.Memo.ordinal(), error.message);
				}
				/**Show error at the top of the screen */
				else {
					showGeneralError(error.message);
					scrollToTop();
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
				
		if( null != bundle ) {	
			final Object item = bundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM);
			
			/**Check if user navigated to this page from Search Payee or Edit Payee*/
			if( bundle.containsKey(BankSearchSelectPayeeFragment.SEARCH_ITEM) ) { 	
				detail.name =  bundle.getString(BankSearchSelectPayeeFragment.SEARCH_ITEM);
				detail.nickName = detail.name;
				
				/**Set flag to false so that when service call is called it adds payee*/
				isUpdate = false;
			} else if(item != null && item instanceof PayeeDetail ) {
				final AddUnmanagedPayee unmanagedPayee = (AddUnmanagedPayee)detail;
				
				unmanagedPayee.name = ((PayeeDetail)item).name;
				unmanagedPayee.nickName = ((PayeeDetail)item).nickName;
				unmanagedPayee.verified = false;
				unmanagedPayee.phone = ((PayeeDetail)item).phone;
				unmanagedPayee.address = ((PayeeDetail)item).address;
				unmanagedPayee.memo =  ((PayeeDetail)item).memo;
				
				/**Make sure number provided by server is formatted*/
				if(  unmanagedPayee.phone != null && 
					 !Strings.isNullOrEmpty(unmanagedPayee.phone.number))  {				
					unmanagedPayee.phone.formatted = PhoneNumberUtils.formatNumber(unmanagedPayee.phone.formatted);
				}
					
				/**Set flag to true so that when service call is called it updates payee*/
				isUpdate = true;
			}
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
			payee.phone.number = getFieldText(UnmanagedPayeeFields.PhoneNumber.ordinal()); 
			payee.address.streetAddress = getFieldText(UnmanagedPayeeFields.AddressLine1.ordinal());
			payee.address.extendedAddress = getFieldText(UnmanagedPayeeFields.AddressLine2.ordinal());
			payee.address.locality = getFieldText(UnmanagedPayeeFields.City.ordinal());
			payee.address.region = getFieldText(UnmanagedPayeeFields.State.ordinal());
			payee.address.postalCode = getFieldText(UnmanagedPayeeFields.ZipCode.ordinal());
			payee.memo = getFieldText(UnmanagedPayeeFields.Memo.ordinal());
			
			/**Remove dashes from phone number, server only accepts digits*/
			if( !Strings.isNullOrEmpty(payee.phone.number) ) {
				payee.phone.formatted = payee.phone.number;
				payee.phone.number = payee.phone.number.replace("-", "");
			}
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
			setFieldText(UnmanagedPayeeFields.PhoneNumber.ordinal(), payee.phone.formatted);
			setFieldText(UnmanagedPayeeFields.AddressLine1.ordinal(), payee.address.streetAddress);
			setFieldText(UnmanagedPayeeFields.AddressLine2.ordinal(), payee.address.extendedAddress);
			setFieldText(UnmanagedPayeeFields.City.ordinal(), payee.address.locality);
			setFieldText(UnmanagedPayeeFields.State.ordinal(), payee.address.region);
			setFieldText(UnmanagedPayeeFields.ZipCode.ordinal(), payee.address.postalCode);	
			setFieldText(UnmanagedPayeeFields.Memo.ordinal(), payee.memo);
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
