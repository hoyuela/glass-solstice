package com.discover.mobile.bank.payees;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.RelativeLayout;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.payee.AddPayeeDetail;
import com.discover.mobile.bank.services.payee.AddUnmanagedPayee;
import com.discover.mobile.bank.services.payee.PayeeDetail;
import com.google.common.base.Strings;

/**
 * Utility class used to generate a list of BankEditDetail objects that are to be displayed in a view.
 * Consists of multiple static methods used to create BankEditDetail objects that will present a
 * Managed Payee's information in a view.
 * 
 * @author henryoyuela
 *
 */
final public class PayeeDetailListGenerator  {
	private PayeeDetailListGenerator() {
	}
	
	/**
	 * 
	 * @param context
	 * @param topLabelResource
	 * @param middleLabelText
	 * @return
	 */
	public static BankEditDetail createBankEditDetail(final Context context, 
											  final int topLabelResource, 
											  final String text) {
		final BankEditDetail item = new BankEditDetail(context);
		
		/**Turn of text validation by default*/
		item.enableValidation(false);
		
		item.getTopLabel().setText(topLabelResource);
		
		if(text != null) {		
			item.setText(text);
		}
		return item;
	}
	
	/**
	 * 
	 * @param context
	 * @param text
	 * @param isVerified
	 * @return
	 */
	public static BankEditDetail createName(final Context context, final String text, final boolean isVerified, final boolean isEditable) {
		
		final BankEditDetail name = createBankEditDetail(context,  R.string.bank_payee_name, text);
		name.enableEditing(!isVerified && isEditable);
		name.getDividerLine().setVisibility(View.INVISIBLE);
		name.getEditableField().setImeOptions(EditorInfo.IME_ACTION_NEXT);
		name.getEditableField().setMinimum(2);
		final InputFilter[] inputFilters = { new InputFilter.LengthFilter(32) };
		name.getEditableField().setFilters(inputFilters);
		return name;
	}
	
	/**
	 * 
	 * @param context
	 * @param text
	 * @return
	 */
	public static BankEditDetail createNickName(final Context context, final String text, final boolean isEditable) {
		/**Add Nickname, Validation 2 char min/30 char max and Invalid characters for a payee nickname: <>;"[]{} */
		final BankEditDetail nickName = createBankEditDetail(context, R.string.bank_payee_nickname, text);
		nickName.getEditableField().setMinimum(2);
		final InputFilter[] inputFilters = { new InputFilter.LengthFilter(30) };
		nickName.getEditableField().setFilters(inputFilters);
		nickName.getEditableField().setInvalidPattern(PayeeValidatedEditField.NON_ALPHA_WITH_DASH);
		nickName.getEditableField().setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
		nickName.enableEditing(isEditable);
		nickName.getEditableField().setImeOptions(EditorInfo.IME_ACTION_NEXT|EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		return nickName;
	}
	
	/**
	 * 
	 * @param context
	 * @param text
	 * @return
	 */
	public static BankEditDetail createAccount(final Context context, final String text, final boolean isEditable) {
		/**Add Account #, Account# Validation 1 char min./32 char max */
		final BankEditDetail account = createBankEditDetail(context, R.string.bank_payee_account, text);
		account.getEditableField().setMinimum(1);
		final InputFilter[] inputFilters = { new InputFilter.LengthFilter(32) };
		account.getEditableField().setFilters(inputFilters);
		account.getEditableField().setError(R.string.bank_invalid_acct);
		account.enableEditing(isEditable);
		account.getEditableField().setImeOptions(EditorInfo.IME_ACTION_NEXT|EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		return account;
	}
	
	/**
	 * 
	 * @param context
	 * @param text
	 * @return
	 */
	public static BankEditDetail createReenterAccount(final Context context, final String text, final boolean isLast) {
		/**Add Re-Enter Account #, Validation 1 char min./32 char max, Invalid characters <>;"[]{}, and matches Account #*/
		final BankEditDetail reenterAccount = createBankEditDetail(context, R.string.bank_payee_reenter_account, text);
		reenterAccount.getEditableField().setMinimum(1);
		final InputFilter[] inputFilters = { new InputFilter.LengthFilter(32) };
		reenterAccount.getEditableField().setError(R.string.bank_nonmatching_acct);
		reenterAccount.getEditableField().setFilters(inputFilters);
		
		/**If it is last field then show done button in keyboard*/
		if( isLast ) {
			reenterAccount.getEditableField().setImeOptions(EditorInfo.IME_ACTION_DONE|EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		} else {
			reenterAccount.getEditableField().setImeOptions(EditorInfo.IME_ACTION_NEXT|EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		}
		return reenterAccount;
	}
	
	/**
	 * 
	 * @param context
	 * @param text
	 * @return
	 */
	public static BankEditDetail createZipCode(final Context context, final String text, final boolean isEditable, final boolean isLastField) {
		/**Add Zip Code,  5 digit numeric - validation after you leave the field*/
		final BankEditDetail zipCode = createBankEditDetail(context, R.string.bank_payee_zip, (Strings.isNullOrEmpty(text) ? "" : text));
		zipCode.getEditableField().setMinimum(5);
		final InputFilter[] inputFilters = { new InputFilter.LengthFilter(9) };
		zipCode.getEditableField().setFilters(inputFilters);
		zipCode.getEditableField().setInvalidPattern(PayeeValidatedEditField.NON_ZIPCODE);
		zipCode.getEditableField().setInputType(InputType.TYPE_CLASS_NUMBER);
		zipCode.getEditableField().setError(R.string.bank_invalid_zip);
		zipCode.enableEditing(isEditable);
		
		if( isLastField ) {
			zipCode.getEditableField().setImeOptions(EditorInfo.IME_ACTION_DONE|EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		} else {
			zipCode.getEditableField().setImeOptions(EditorInfo.IME_ACTION_NEXT|EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		}
		return zipCode;
	}
	
	private static BankEditDetail createPhoneNumber(final Context context,
			final String phone, final boolean isEditable) {
	
		/**Add Phone Number, Validation Must be a 10 digit #  and Invalid characters for a payee nickname: <>;"[]{} */
		final BankPhoneDetail phoneNumber =  new BankPhoneDetail(context);
		
		if(phone != null) {	phoneNumber.setText(phone);}
		phoneNumber.getTopLabel().setText(R.string.bank_payee_phone_number);
		phoneNumber.enableEditing(isEditable);
		phoneNumber.getEditableField().setImeOptions(EditorInfo.IME_ACTION_NEXT|EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		phoneNumber.getEditableField().setError(R.string.bank_invalid_phone_number);
		return phoneNumber;
	}
	
	private static BankEditDetail createState(final Context context, final String state, final boolean isEditable) {
		final BankStateDetail item = new BankStateDetail(context);
			
		item.getTopLabel().setText(R.string.bank_payee_state);
		item.setText(state);
		item.getEditableField().setEnabled(false);
		item.getEditableField().setImeOptions(EditorInfo.IME_ACTION_NEXT);
		return item;
	}

	private static BankEditDetail createCity(final Context context,
			final String text, final boolean isEditable) {
		/**Add City, Validation min=?, max=? and Invalid characters for a payee nickname: <>;"[]{} */
		final BankEditDetail city = createBankEditDetail(context, R.string.bank_payee_city, text);
		city.getEditableField().setMinimum(2);
		final InputFilter[] inputFilters = { new InputFilter.LengthFilter(25) };
		city.getEditableField().setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS|InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
		city.getEditableField().setFilters(inputFilters);
		city.getEditableField().setInvalidPattern(PayeeValidatedEditField.NON_ALPHA);
		city.enableEditing(isEditable);
		city.getEditableField().setImeOptions(EditorInfo.IME_ACTION_NEXT|EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		return city;
	}
	
	private static BankEditDetail createAddress(final Context context, final String text) {
		final BankEditDetail address = createBankEditDetail(context, R.string.bank_payee_address, text);
		address.enableEditing(false);
		address.getMiddleLabel().setSingleLine(false);
		return address;
	}
	

	private static BankEditDetail createAddressLine1(final Context context,
			final String text, final boolean isEditable) {
		/**Add City, Validation min=?, max=? and Invalid characters for a payee nickname: <>;"[]{} */
		final BankEditDetail address = createBankEditDetail(context, R.string.bank_payee_address_line1, text);
		address.getEditableField().setMinimum(2);
		final InputFilter[] inputFilters = { new InputFilter.LengthFilter(30) };
		address.getEditableField().setFilters(inputFilters);
		address.getEditableField().setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS|InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
		address.getEditableField().setInvalidPattern(PayeeValidatedEditField.NON_ALPHANUMERIC);
		address.enableEditing(isEditable);
		address.getEditableField().setImeOptions(EditorInfo.IME_ACTION_NEXT|EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		return address;
	}
	
	private static BankEditDetail createAddressLine2(final Context context,
			final String text, final boolean isEditable) {
		/**Add City, Validation min=?, max=? and Invalid characters for a payee nickname: <>;"[]{} */
		final BankEditDetail address = createBankEditDetail(context, R.string.bank_payee_address_line2, text);
		address.getEditableField().setMinimum(0);
		final InputFilter[] inputFilters = { new InputFilter.LengthFilter(30) };
		address.getEditableField().setFilters(inputFilters);
		address.getEditableField().setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS|InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
		address.getEditableField().setInvalidPattern(PayeeValidatedEditField.NON_ALPHANUMERIC);
		address.enableEditing(isEditable);
		address.getEditableField().setImeOptions(EditorInfo.IME_ACTION_NEXT|EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		return address;
	}
	
	private static BankEditDetail createMemo(final Context context,
			final String text, final boolean isEditable) {
		/**Add City, Validation min=?, max=? and Invalid characters for a payee nickname: <>;"[]{} */
		final BankEditDetail memo = createBankEditDetail(context, R.string.bank_payee_memo, text);
		memo.getEditableField().setMinimum(0);
		final InputFilter[] inputFilters = { new InputFilter.LengthFilter(32) };
		memo.getEditableField().setFilters(inputFilters);
		memo.enableEditing(isEditable);
		memo.getEditableField().setImeOptions(EditorInfo.IME_ACTION_DONE|EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		memo.getMiddleLabel().setTextAppearance(context, R.style.sub_copy_big);
		return memo;
	}
	
	/**
	 * Get a payee detail list from an AddPayeeDetail object used for the Add Payee Page.
	 * 
	 * @param item a PayeeDetail object.
	 * @return an appropritate list for a PayeeDetail object.
	 */
	public static List<RelativeLayout> getPayeeDetailList(final Context context, final AddPayeeDetail item) {
		final List<RelativeLayout> items = new ArrayList<RelativeLayout>();

		final BankEditDetail name = createName(context, item.name, item.verified, true);
		final BankEditDetail nickName = createNickName(context, item.nickName, true);
		final BankEditDetail account = createAccount(context, item.accountNumber, true);
		
		/**Set what field should get focus after next is tapped*/
		name.setNextBankEditDetail(nickName);
		nickName.setNextBankEditDetail(account);
		
		/**Add Payee Name*/
		items.add(name);
		items.add(nickName);
		items.add(account);
		
		/**Only add an editable field to enter zip code if required*/
		if( item.isZipRequired) {
			final BankEditDetail reenterAcct = createReenterAccount(context, item.accountNumberConfirmed, false);
			final BankEditDetail zipCode = createZipCode(context, item.zip, true, true);
			
			/**Set what field should get focus after next is tapped*/
			account.setNextBankEditDetail(reenterAcct);
			reenterAcct.setNextBankEditDetail(zipCode);
			
			/**Add items to content list*/
			items.add(reenterAcct);
			items.add(zipCode);
		} else {
			final BankEditDetail reenterAcct = createReenterAccount(context, item.accountNumberConfirmed, true);
			
			/**Set what field should get focus after next is tapped*/
			account.setNextBankEditDetail(reenterAcct);
			
			/**Add item to content list*/
			items.add(reenterAcct);
		}
	
		return items;
	}
	
	/**
	 * Get a payee detail list from an AddPayeeDetail object used for the Add Payee Confirmation Page.
	 * 
	 * @param item a PayeeDetail object.
	 * @return an appropriate list for a PayeeDetail object.
	 */
	public static List<RelativeLayout> getConfirmedPayeeDetailList(final Context context, final PayeeDetail item) {
		final List<RelativeLayout> items = new ArrayList<RelativeLayout>();

		if( item.verified ) {
			/**Add Payee Name*/
			items.add(createName(context, item.name, item.verified,false));
			items.add(createNickName(context, item.nickName, false));
			items.add(createAccount(context, item.account.formatted, false));
			
			/**Only add an item for zip code if required*/
			if ( item.isZipRequired && !Strings.isNullOrEmpty(item.zip)){
				items.add(createZipCode(context, item.zip, false, true));
			}
		} else {
			/**Create Add Unmanaged Payee List*/
			items.add(createName(context, item.name, item.verified, false));
			items.add(createNickName(context, item.nickName, false));
			items.add(createPhoneNumber(context, item.phone.formatted, false));
			items.add(createAddress(context, item.address.formattedAddress));
			items.add(createMemo(context, item.memo, false));
		}
		
		return items;
	}
	
	/**
	 * Get a payee detail list from an AddPayeeDetail object used for the Add Payee Page.
	 * 
	 * @param item a PayeeDetail object.
	 * @return an appropritate list for a PayeeDetail object.
	 */
	public static List<RelativeLayout> getUnmanagedPayeeDetailList(final Context context, final AddUnmanagedPayee item) {
		final List<RelativeLayout> items = new ArrayList<RelativeLayout>();

		final BankEditDetail name = createName(context, item.name, item.verified, false);
		final BankEditDetail nickName = createNickName(context, item.nickName, true);
		final BankEditDetail phoneNumber =  createPhoneNumber(context, item.phone.formatted, true);
		final BankEditDetail addressLine1 =  createAddressLine1(context, item.address.streetAddress, true);
		final BankEditDetail addressLine2 =  createAddressLine2(context, item.address.extendedAddress, true);
		final BankEditDetail city = createCity(context, item.address.locality, true);
		final BankEditDetail state =  createState(context, item.address.region, true);		
		final BankEditDetail zipCode =  createZipCode(context, item.address.postalCode, true, false);
		final BankEditDetail memo =  createMemo(context, item.memo, true);
		
			
		/**Set what field should get focus after next is tapped*/
		name.setNextBankEditDetail(nickName);
		nickName.setNextBankEditDetail(phoneNumber);
		phoneNumber.setNextBankEditDetail(addressLine1);
		addressLine1.setNextBankEditDetail(addressLine2);
		addressLine2.setNextBankEditDetail(city);
		city.setNextBankEditDetail(state);
		state.setNextBankEditDetail(zipCode);
		zipCode.setNextBankEditDetail(memo);
		
		/**Create Add Unmanaged Payee List*/
		items.add(name);
		items.add(nickName);
		items.add(phoneNumber);
		items.add(addressLine1);
		items.add(addressLine2);
		items.add(city);
		items.add(state);
		items.add(zipCode);
		items.add(memo);
		
		return items;
	}
}
