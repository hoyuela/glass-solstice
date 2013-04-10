/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.transfer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.discover.mobile.bank.R;

public class BankFrequencyDetailView extends RelativeLayout{

	public BankFrequencyDetailView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		final View view = LayoutInflater.from(context).inflate(R.layout.bank_frequency_detail_view, null);
		addView(view);
	}

}
