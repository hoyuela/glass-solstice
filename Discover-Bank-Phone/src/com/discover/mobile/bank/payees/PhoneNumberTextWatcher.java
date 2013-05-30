package com.discover.mobile.bank.payees;

import java.util.Locale;

import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.widget.TextView;


/**
 * Watches a {@link TextView} and if a phone number is entered will format it using
 * {@link PhoneNumberUtils#formatNumber(Editable, int)}. The formatting is based on
 * the current system locale when this object is created and future locale changes
 * may not take effect on this instance.
 */
public class PhoneNumberTextWatcher implements TextWatcher {

	static private int sFormatType;
    static private Locale sCachedLocale;
    private boolean mFormatting;
    private boolean mDeletingHyphen;
    private int mHyphenStart;
    private boolean mDeletingBackward;

    public PhoneNumberTextWatcher() {
        if (sCachedLocale == null || sCachedLocale != Locale.getDefault()) {
            sCachedLocale = Locale.getDefault();
            sFormatType = PhoneNumberUtils.getFormatTypeForLocale(sCachedLocale);
        }
        
        if (sFormatType == PhoneNumberUtils.FORMAT_UNKNOWN) {
        		// If the format is unknown, the phone number will not auto-format
        		// Defaulting to US (NANP) Format DDD-DDD-DDDD
        		sFormatType = PhoneNumberUtils.FORMAT_NANP;
        }
    }

    @Override
	public synchronized void afterTextChanged(final Editable text) {
        // Make sure to ignore calls to afterTextChanged caused by the work done below
        if (!mFormatting) {
            mFormatting = true;

            // If deleting the hyphen, also delete the char before or after that
            if (mDeletingHyphen && mHyphenStart > 0) {
                if (mDeletingBackward) {
                    if (mHyphenStart - 1 < text.length()) {
                        text.delete(mHyphenStart - 1, mHyphenStart);
                    }
                } else if (mHyphenStart < text.length()) {
                    text.delete(mHyphenStart, mHyphenStart + 1);
                }
            }

            PhoneNumberUtils.formatNumber(text, sFormatType);

            mFormatting = false;
        }
    }

    @Override
	public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
        // Check if the user is deleting a hyphen
        if (!mFormatting) {
            // Make sure user is deleting one char, without a selection
            final int selStart = Selection.getSelectionStart(s);
            final int selEnd = Selection.getSelectionEnd(s);
            
            //Check if the function can delete another character and if it is a hyphen and also if the beginning and ending
            //selection matches.
            if (s.length() > 1 && count == 1 && after == 0 && s.charAt(start) == '-' && selStart == selEnd) { 
            	// no selection
                mDeletingHyphen = true;
                mHyphenStart = start;
                // Check if the user is deleting forward or backward
                if (selStart == start + 1) {
                    mDeletingBackward = true;
                } else {
                    mDeletingBackward = false;
                }
            } else {
                mDeletingHyphen = false;
            }
        }
    }

    @Override
	public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
        // Does nothing
    }
}