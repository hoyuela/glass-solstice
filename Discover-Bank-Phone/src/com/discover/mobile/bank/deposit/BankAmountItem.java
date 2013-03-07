package com.discover.mobile.bank.deposit;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.discover.mobile.bank.R;

public class BankAmountItem extends RelativeLayout {
	/**
	 * Reference to layout used for this view
	 */
	private final RelativeLayout layout;
	
	public BankAmountItem(final Context context) {
		super(context);
		
		layout = (RelativeLayout)LayoutInflater.from(context).inflate(R.layout.amount_list_item, null);
		
		addView(layout);
	}

}
