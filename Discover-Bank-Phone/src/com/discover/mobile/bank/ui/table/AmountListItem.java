package com.discover.mobile.bank.ui.table;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.ui.widgets.AmountValidatedEditField;
/**
 * This is a very simple class that provides a list item with a formatted input field.
 * @author scottseward
 *
 */
public class AmountListItem extends RelativeLayout {
	public AmountListItem(final Context context) {
		super(context);
		doSetup(context);
	}
	
	public AmountListItem(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		doSetup(context);
	}
	
	public AmountListItem(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		doSetup(context);
	}
	
	private void doSetup(final Context context) {
		addView(getInflatedLayout(context));
		getEditField().enableBankAmountTextWatcher(true);
	}
	
	public AmountValidatedEditField getEditField() {
		return 	((AmountValidatedEditField)findViewById(R.id.amount_edit));

	}
	
	public String getAmount() {
		final String amount = getEditField().getText().toString();
		
		return amount;
	}
	
	private RelativeLayout getInflatedLayout(final Context context) {
		return (RelativeLayout)LayoutInflater.from(context).inflate(R.layout.formatted_amount_list_item, null);
	}

}
