package com.discover.mobile.section.account.recent;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.account.recent.TransactionDetail;

public class TransactionItem extends RelativeLayout{
	
	/**Date associated with the item*/
	private final TextView date;
	
	private final TextView description;
	
	private final TextView amount;
	
	private final TransactionDetail transaction;
	
	/**
	 * Constructor of the class
	 * @param context - activity context
	 * @param attrs - attributes to give to the layout
	 */
	public TransactionItem(final Context context, final AttributeSet attrs, final TransactionDetail transaction) {
		super(context, attrs);
		
		final RelativeLayout mainView = (RelativeLayout) LayoutInflater.from(context)
                .inflate(R.layout.transaction_item, null);
		
		date = (TextView) mainView.findViewById(R.id.transaction_date);
		description = (TextView) mainView.findViewById(R.id.transaction_description);
		amount = (TextView) mainView.findViewById(R.id.transaction_amount);
		this.transaction = transaction; 
		
		showTransaction();
		addView(mainView);
	}
	
	private void showTransaction(){
		date.setText(this.transaction.date);
		description.setText(this.transaction.description);
		amount.setText(this.transaction.amount);
	}
}