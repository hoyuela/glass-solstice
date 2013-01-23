package com.discover.mobile.section.account.recent;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.common.account.recent.TransactionDetail;

public class TransactionTable extends RelativeLayout{
	
	private final TextView title;
	
	private final TextView dateFilter;
	
	private final TextView descriptionFilter;
	
	private final TextView amountFilter;
	
	private final TextView message;
	
	private final LinearLayout transactionsList;
	
	private List<TransactionDetail> transactions;

	public TransactionTable(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		final RelativeLayout mainView = (RelativeLayout) LayoutInflater.from(context)
                .inflate(R.layout.transaction_table, null);
		
		title = (TextView) mainView.findViewById(R.id.table_title);
		dateFilter = (TextView) mainView.findViewById(R.id.date_filter);
		descriptionFilter = (TextView) mainView.findViewById(R.id.description_filter);
		amountFilter = (TextView) mainView.findViewById(R.id.amount_filter);
		transactionsList = (LinearLayout) mainView.findViewById(R.id.transactions);
		message = (TextView) mainView.findViewById(R.id.no_transactions);	
		
		addView(mainView);
	
	}
	
	public void showTransactions(final List<TransactionDetail> transactions){
		if(null == transactions || transactions.isEmpty()){
			showNoTransactionMessage();
		} else {
			for(TransactionDetail transaction : transactions){
				TransactionItem item = new TransactionItem(this.getContext(), null, transaction);
				transactionsList.addView(item);
			}
		}
	}
	
	private void showNoTransactionMessage() {
		transactionsList.setVisibility(View.GONE);
		message.setVisibility(View.VISIBLE);
		
	}

	public void clearList(){
		transactionsList.removeAllViews();
	}
	
	public void setTitle(final String title){
		this.title.setText(title);
	}
	
	public void setTransactions(final List<TransactionDetail> transactions){
		this.transactions = transactions;
	}
}
