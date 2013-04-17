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
	/**
	 * Reference to a TextView that describes with amountField is for.
	 */
	private TextView topLabel = null;
	/**
	 * Reference to EditText where the user can enter a dollar amount to submit for check deposit.
	 */
	private BankAmountLimitValidatedField amountField = null;
	/**
	 * Reference to TextView that shows inline errors beneath amountField. The text is set by amountField via its isValid() method.
	 */
	private TextView errorLabel = null;
	/**
	 * Reference to layout used for this view
	 */
	private final RelativeLayout layout;
	
	public BankAmountItem(final Context context) {
		super(context);
		
		layout = (RelativeLayout)LayoutInflater.from(context).inflate(R.layout.amount_list_item, null);
		
		/**UI Controls for this view*/
		topLabel = (TextView)layout.findViewById(R.id.top_label);
		amountField = (BankAmountLimitValidatedField)layout.findViewById(R.id.editable_field);
		errorLabel = (TextView)layout.findViewById(R.id.error_label);
		
		/**Associate the error label with the amount field. 
		 * We also ensure that the label will be INVISIBLE when hidden (as opposed to "GONE").*/
		amountField.setHiddenErrorVisibility(false);
		amountField.attachErrorLabel(errorLabel);
		
		addView(layout);
	}
	
	/**
	 * 
	 * @return Returns a reference to the editable field in this view.
	 */
	public BankAmountLimitValidatedField getEditableField() {
		return this.amountField;
	}
	
	

}
