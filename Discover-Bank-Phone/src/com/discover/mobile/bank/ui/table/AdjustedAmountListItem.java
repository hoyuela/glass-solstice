package com.discover.mobile.bank.ui.table;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.discover.mobile.bank.R;

/**
 * This is a very simple class that provides a list item with a formatted input field.
 * @author scottseward
 *
 */
public class AdjustedAmountListItem extends AmountListItem {

	public AdjustedAmountListItem(final Context context) {
		super(context);
	}
	
	@Override
	protected RelativeLayout getInflatedLayout(final Context context) {
		return (RelativeLayout)LayoutInflater.from(context).inflate(R.layout.adjusted_formatted_amount_list_item, null);
	}
	
}
