package com.discover.mobile.bank.ui.table;

import java.text.NumberFormat;
import java.util.Locale;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.R;

/**
 * Simple item that will be placed in a table for the user to view a list of
 * some type.
 * @author jthornton
 *
 */
public class BankTableItem extends RelativeLayout {

	/**Index the item is in the list*/
	private final int location;

	/**Date associated with the item*/
	private final TextView date;

	/**Description of the transaction*/
	private final TextView description;

	/**Amount of the transactions*/
	private final TextView amount;

	/**String meaning the amount was a debit*/
	private static final String NEGATIVE = "-";

	/**

	/**
	 * Constructor of the class
	 * @param context - activity context
	 * @param attrs - attributes to give to the layout
	 */
	public BankTableItem(final Context context, final AttributeSet attrs, final int location) {
		super(context, attrs);

		final RelativeLayout mainView = (RelativeLayout) LayoutInflater.from(context)
				.inflate(R.layout.transaction_item, null);

		this.date = (TextView) mainView.findViewById(R.id.transaction_date);
		this.description = (TextView) mainView.findViewById(R.id.transaction_description);
		this.amount = (TextView) mainView.findViewById(R.id.transaction_amount);
		this.location = location;
		addView(mainView);
	}

	/**
	 * Set the date to be displayed in the item
	 * @param date - date to be displayed
	 */
	public void setDate(final String date){
		this.date.setText(date);
	}

	/**
	 * Set the description to be displayed in the item
	 * @param description - description to be set
	 */
	public void setDescription(final String description){
		this.description.setText(description);
	}

	/**
	 * Set the amount to be shown in the item, note this will handle color change.
	 * @param amount - amount to set
	 */
	public void setAmount(final String amount){
		if(!amount.contains(NEGATIVE)){
			this.amount.setTextColor(this.getResources().getColor(R.color.string_indicator));
		}
		this.amount.setText(convertToDollars(amount));
	}

	/**
	 * 
	 */
	public String convertToDollars(final String amount){
		final boolean isNegative = amount.contains(NEGATIVE);
		double dollars;
		if(null == amount){return "";}
		if(isNegative){
			dollars = Double.parseDouble(amount.substring(1));
			dollars = dollars/100;
			return "-"+NumberFormat.getCurrencyInstance(Locale.US).format(dollars);
		}else{
			dollars = Double.parseDouble(amount);
			dollars = dollars/100;
			return NumberFormat.getCurrencyInstance(Locale.US).format(dollars);
		}
	}

	/**
	 * Get the location of the item
	 * @return the location of the item
	 */
	public int getLocaiton() {
		return this.location;
	}
}