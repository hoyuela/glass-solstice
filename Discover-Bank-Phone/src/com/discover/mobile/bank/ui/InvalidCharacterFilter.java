package com.discover.mobile.bank.ui;

import android.text.InputFilter;
import android.text.Spanned;

public class InvalidCharacterFilter implements InputFilter {

	char[] invalidCharacters = { '<', '>', '(', ')', '&', ';', '\'', '"', '[',
			']', '{', '}' };

	@Override
	public CharSequence filter(final CharSequence source, final int start, final int end,
			final Spanned dest, final int dstart, final int dend) {
		if (end > start) {
			for (int index = start; index < end; index++) {
				if (new String(invalidCharacters).contains(String
						.valueOf(source.charAt(index)))) {
					return "";
				}
			}
		}
		return null;
	}

}