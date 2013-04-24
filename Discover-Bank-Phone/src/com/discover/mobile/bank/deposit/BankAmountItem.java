package com.discover.mobile.bank.deposit;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.R;

/**
 * Class used to represent a amount text field with an inline error label to be shown by the
 * AmountValidatedEditField whenever a validation fails. The layout used by this class is defined
 * in res/layout/amount_list_item.xml.
 * 
 * @author henryoyuela
 *
 */
public class BankAmountItem extends RelativeLayout {
	
	public BankAmountItem(final Context context) {
		super(context);
		
		final RelativeLayout layout = (RelativeLayout)LayoutInflater.from(context).inflate(R.layout.amount_list_item, null);

		addView(layout);
		
		/**Associate the error label with the amount field. 
		 * We also ensure that the label will be INVISIBLE when hidden (as opposed to "GONE").*/
		getEditableField().post(new Runnable() {
			@Override
			public void run() {
				final BankAmountLimitValidatedField field = getEditableField();
				if(field != null) {
					field.setHiddenErrorVisibility(false);	 
					field.attachErrorLabel((TextView)layout.findViewById(R.id.error_label));
				}
			}
		});
	}
	
	/**
	 * 
	 * @return Returns a reference to the editable field in this view.
	 */
	public BankAmountLimitValidatedField getEditableField() {
		return ((BankAmountLimitValidatedField)this.findViewById(R.id.editable_field));
	}

}
