package com.discover.mobile.bank.payees;

import java.util.List;

import android.os.Bundle;
import android.widget.RelativeLayout;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.services.payee.AddPayeeDetail;
import com.discover.mobile.bank.services.payee.AddUnmanagedPayee;
import com.discover.mobile.bank.services.payee.SearchPayeeResult;
import com.discover.mobile.common.net.error.bank.BankError;
import com.discover.mobile.common.net.error.bank.BankErrorResponse;
import com.google.common.base.Strings;


public class BankAddUnmanagedPayeeFragment extends BankAddPayeeFragment {

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
	public boolean handleError(final BankErrorResponse msgErrResponse) {
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

	@Override
	protected void initializeData(final Bundle bundle) {
		if( null != bundle &&  null != bundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM)) {
			detail = new AddUnmanagedPayee();
			payeeSearchResult =  (SearchPayeeResult)bundle.getSerializable(BankExtraKeys.DATA_LIST_ITEM);	
			detail.name = payeeSearchResult.name;
			detail.nickName = detail.name;
		} 
	}

	@Override
	protected AddPayeeDetail getPayeeDetail() {
		if( content != null ) {
			final BankEditDetail name = ((BankEditDetail)content.get(UnmanagedPayeeFields.PayeeName.ordinal()));
			final BankEditDetail nickName =  ((BankEditDetail)content.get(UnmanagedPayeeFields.PayeeNickName.ordinal())); 
			final BankEditDetail phoneNumber =  ((BankEditDetail)content.get(UnmanagedPayeeFields.PhoneNumber.ordinal())); 
			final BankEditDetail addressLine1 =  ((BankEditDetail)content.get(UnmanagedPayeeFields.AddressLine1.ordinal()));
			final BankEditDetail addressLine2 =  ((BankEditDetail)content.get(UnmanagedPayeeFields.AddressLine2.ordinal()));
			final BankEditDetail city =  ((BankEditDetail)content.get(UnmanagedPayeeFields.City.ordinal()));
			final BankEditDetail state =  ((BankEditDetail)content.get(UnmanagedPayeeFields.State.ordinal()));
			final BankEditDetail zipCode =  ((BankEditDetail)content.get(UnmanagedPayeeFields.ZipCode.ordinal()));
			final BankEditDetail memo =  ((BankEditDetail)content.get(UnmanagedPayeeFields.Memo.ordinal()));
			
			final AddUnmanagedPayee payee = (AddUnmanagedPayee)detail;
			
			payee.name = name.getText();
			payee.nickName = nickName.getText();
			payee.phone = phoneNumber.getText();
			payee.addressLine1 = addressLine1.getText();
			payee.addressLine2 = addressLine2.getText();
			payee.addressCity = city.getText();
			payee.addressState = state.getText();
			payee.zip = zipCode.getText();
			
			//TODO: Currently do not have a field for memo
			//payee.? = zipCode.getText();
		}

		return detail;
	}

	@Override
	protected void executeServiceCall() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void restoreState(final Bundle bundle) {
		if( detail != null ) {
			final BankEditDetail name = ((BankEditDetail)content.get(UnmanagedPayeeFields.PayeeName.ordinal()));
			final BankEditDetail nickName =  ((BankEditDetail)content.get(UnmanagedPayeeFields.PayeeNickName.ordinal())); 
			final BankEditDetail phoneNumber =  ((BankEditDetail)content.get(UnmanagedPayeeFields.PhoneNumber.ordinal())); 
			final BankEditDetail addressLine1 =  ((BankEditDetail)content.get(UnmanagedPayeeFields.AddressLine1.ordinal()));
			final BankEditDetail addressLine2 =  ((BankEditDetail)content.get(UnmanagedPayeeFields.AddressLine2.ordinal()));
			final BankEditDetail city =  ((BankEditDetail)content.get(UnmanagedPayeeFields.City.ordinal()));
			final BankEditDetail state =  ((BankEditDetail)content.get(UnmanagedPayeeFields.State.ordinal()));
			final BankEditDetail zipCode =  ((BankEditDetail)content.get(UnmanagedPayeeFields.ZipCode.ordinal()));
			final BankEditDetail memo =  ((BankEditDetail)content.get(UnmanagedPayeeFields.Memo.ordinal()));
			
			final AddUnmanagedPayee payee = (AddUnmanagedPayee)detail;
			
			name.setText(detail.name);
			nickName.setText(detail.nickName);
			phoneNumber.setText(payee.phone);
			addressLine1.setText(payee.addressLine1);
			addressLine2.setText(payee.addressLine2);
			city.setText( payee.addressCity);
			state.setText( payee.addressState);
			zipCode.setText(payee.zip);	
		} 	
	}

	@Override
	protected List<RelativeLayout> getRelativeLayoutListContent() {
		return PayeeDetailListGenerator.getUnmanagedPayeeDetailList(getActivity(), (AddUnmanagedPayee)detail);
	}
	
}
