package com.discover.mobile.bank.payees;

import android.text.InputType;
import android.text.Spanned;
import android.text.method.NumberKeyListener;

/**
 * Filter used to prevent user from entering any non-numeric or dash values into an
 * EditText field. In addition, it doesn't allow the user to start an input with a
 * 1. This class allows to enforce a user to follow a phone number format.
 * 
 * @author henryoyuela
 *
 */
public class PhoneNumberFilter extends NumberKeyListener {

    @Override
    public int getInputType() {
        return InputType.TYPE_CLASS_PHONE;
    }

    @Override
    protected char[] getAcceptedChars() {
        return new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-' };
    }

    @Override
    public CharSequence filter(final CharSequence source, final int start, final int end,
            final Spanned dest, final int dstart, final int dend) {

        // Don't let phone numbers start with 1
        if (dstart == 0 && source.equals("1")) 
            return "";

        return super.filter(source, start, end, dest, dstart, dend);
    }
}