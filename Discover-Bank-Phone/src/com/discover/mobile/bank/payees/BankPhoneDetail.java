package com.discover.mobile.bank.payees;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;

/**
 * Class sub-class of BankEditDetail which format entered text to follow the phone number
 * format XXX-XXX-XXXX. It allows a minimum/maximum character length of 12 characters, only
 * allows digits or dashes.
 * 
 * @author henryoyuela
 *
 */
public class BankPhoneDetail extends BankEditDetail {

	public BankPhoneDetail(final Context context) {
		super(context);
	}
	
	public BankPhoneDetail(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}
	public BankPhoneDetail(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}
	   
	@Override
	protected void loadViews() { 		
		super.loadViews();
	
		/**Allow only character length of 12 characters, follow format  XXX-XXX-XXXX*/
		final PayeeValidatedEditField phoneNumber = getEditableField();
		phoneNumber.setMinimum(12);
		/**Set listener that will auto format text as user types*/
		phoneNumber.addTextChangedListener(new PhoneNumberTextWatcher());
		/**Set input type to phone to open the dialer soft keyboard*/
		phoneNumber.setInputType(InputType.TYPE_CLASS_PHONE);
		/**Set filter that will enforce only digits and dashes and a length of 12 maximum characters*/
		phoneNumber.setFilters(new InputFilter[] { new PhoneNumberFilter(), new InputFilter.LengthFilter(12) });
	}
}
