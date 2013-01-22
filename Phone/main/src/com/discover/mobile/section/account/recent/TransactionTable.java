package com.discover.mobile.section.account.recent;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.discover.mobile.R;

public class TransactionTable extends LinearLayout{
	
	private final TextView title;
	
	private final TextView dateFilter;
	
	private final TextView descriptionFilter;
	
	private final TextView amountFilter;
	
	private final LinearLayout transactions;

	
	public TransactionTable(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		final LinearLayout mainView = (LinearLayout) LayoutInflater.from(context)
                .inflate(R.layout.transaction_table, null);
		
		title = (TextView) mainView.findViewById(R.id.table_title);
		dateFilter = (TextView) mainView.findViewById(R.id.date_filter);
		descriptionFilter = (TextView) mainView.findViewById(R.id.description_filter);
		amountFilter = (TextView) mainView.findViewById(R.id.amount_filter);
		transactions = (LinearLayout) mainView.findViewById(R.id.transactions);
		
		dateFilter.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				sortTransactionsByDate();
				
			}
		});
		
		descriptionFilter.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				sortTransactionsByDescription();
				
			}
		});
		
		amountFilter.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(final View v) {
				sortTransactionsByAmount();
				
			}
		});
		
		
		addView(mainView);
	
	}
	
	public void showTransactions(){
		
	}
	
	public void setTransactions(){
		
	}
	
	public void setTitle(final String title){
		this.title.setText(title);
	}
	
	protected void sortTransactionsByDate(){
		
	}
	
	protected void sortTransactionsByAmount(){
		
	}

	protected void sortTransactionsByDescription(){
		
	}

}
