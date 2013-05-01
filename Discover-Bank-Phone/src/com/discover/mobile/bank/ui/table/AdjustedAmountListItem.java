package com.discover.mobile.bank.ui.table;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.discover.mobile.bank.R;

/**
 * This is a very simple class that provides a list item with a formatted input field.
 * @author scottseward
 *
 */
public class AdjustedAmountListItem extends AmountListItem {

	public AdjustedAmountListItem(final Context context) {
		super(context);
		this.removeAllViews();
		addView(getInflatedLayout());
	}
	
	private View getInflatedLayout() {
		return LayoutInflater.from(getContext()).inflate(R.layout.adjusted_formatted_amount_list_item, null);
	}
	
}
