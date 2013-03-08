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
import com.discover.mobile.bank.services.payee.PayeeDetail;

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
		
		if(text != null) {
			item.getTopLabel().setText(topLabelResource);
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
		name.getDividerLine().setVisibility(View.GONE);
		name.getEditableField().setImeOptions(EditorInfo.IME_ACTION_NEXT);
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
		nickName.getEditableField().setInvalidPattern(PayeeValidatedEditField.INVALID_CHARACTERS);
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
		/**Add Account #, Account# Validation 1 char min./32 char max and Invalid characters <>;"[]{} */
		final BankEditDetail account = createBankEditDetail(context, R.string.bank_payee_account, text);
		account.getEditableField().setMinimum(1);
		final InputFilter[] inputFilters = { new InputFilter.LengthFilter(30) };
		account.getEditableField().setFilters(inputFilters);
		account.getEditableField().setInvalidPattern(PayeeValidatedEditField.INVALID_CHARACTERS);
		account.getEditableField().setInputType(InputType.TYPE_CLASS_NUMBER);
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
		final InputFilter[] inputFilters = { new InputFilter.LengthFilter(30) };
		reenterAccount.getEditableField().setFilters(inputFilters);
		reenterAccount.getEditableField().setInvalidPattern(PayeeValidatedEditField.INVALID_CHARACTERS);
		reenterAccount.getEditableField().setInputType(InputType.TYPE_CLASS_NUMBER);
		
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
	public static BankEditDetail createZipCode(final Context context, final String text, final boolean isEditable) {
		/**Add Zip Code,  5 digit numeric - validation after you leave the field*/
		final BankEditDetail zipCode = createBankEditDetail(context, R.string.bank_payee_zip, text);
		zipCode.getEditableField().setMinimum(5);
		final InputFilter[] inputFilters = { new InputFilter.LengthFilter(5) };
		zipCode.getEditableField().setFilters(inputFilters);
		zipCode.getEditableField().setInvalidPattern(PayeeValidatedEditField.INVALID_CHARACTERS);
		zipCode.getEditableField().setInputType(InputType.TYPE_CLASS_NUMBER);
		zipCode.getEditableField().setError(R.string.bank_invalid_zip);
		zipCode.enableEditing(isEditable);
		zipCode.getEditableField().setImeOptions(EditorInfo.IME_ACTION_DONE|EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		return zipCode;
	}
	
	
	/**
	 * Get a payee detail list from an AddPayeeDetail object used for the Add Payee Page.
	 * 
	 * @param item a PayeeDetail object.
	 * @return an appropritate list for a PayeeDetail object.
	 */
	public static List<RelativeLayout> getPayeeDetailList(final Context context, final AddPayeeDetail item) {
		final List<RelativeLayout> items = new ArrayList<RelativeLayout>();

		/**Add Payee Name*/
		items.add(createName(context, item.name, item.verified, true));
		items.add(createNickName(context, item.nickName, true));
		items.add(createAccount(context, item.accountNumber, true));
		
		/**Only add an editable field to enter zip code if required*/
		if( item.isZipRequired) {
			items.add(createReenterAccount(context, item.accountNumberConfirmed, false));
			items.add(createZipCode(context, item.zip, true));
		} else {
			items.add(createReenterAccount(context, item.accountNumberConfirmed, true));
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

		/**Add Payee Name*/
		items.add(createName(context, item.name, item.verified,false));
		items.add(createNickName(context, item.nickName, false));
		items.add(createAccount(context, item.account.formatted, false));
		
		/**Only add an item for zip code if required*/
		if( !item.address.postalCode.isEmpty()) {
			items.add(createZipCode(context, item.address.postalCode, false));
		}
		
		return items;
	}
}
