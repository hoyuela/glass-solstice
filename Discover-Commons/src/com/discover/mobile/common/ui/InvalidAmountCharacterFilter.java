package com.discover.mobile.common.ui;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * 
 * @author Samuel Frank Smith
 * 
 */
public class InvalidAmountCharacterFilter implements InputFilter {

	char[] validCharacters = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', '.', ',' };

	@Override
	public CharSequence filter(final CharSequence source, final int start,
			final int end, final Spanned dest, final int dstart, final int dend) {
		if (end > start) {
			for (int index = start; index < end; index++) {
				if (!new String(validCharacters).contains(String.valueOf(source
						.charAt(index)))) {
					return "";
				}
			}
		}
		return null;
	}

}