package com.discover.mobile.section.account.recent;

import java.util.ArrayList;
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

/**
 * Table that will hold transactions
 * 
 * @author jthornton
 * 
 */
public class TransactionTable extends RelativeLayout {

	/** Title for the table */
	private final TextView title;

	/** No transactions message */
	private final TextView message;

	/** Layout holding the transactions */
	private final LinearLayout transactionsList;

	/** List of transactions that the layout contains */
	private List<TransactionDetail> transactions;

	/** Boolean used to determine if the white background should be shown */
	private boolean isWhiteBackground = false;

	/** No message bottom line */
	private View noMessage;

	/**
	 * Constructor for the class
	 * 
	 * @param context
	 *            - activity context
	 * @param attrs
	 *            - attributes to apply to the layout
	 */
	public TransactionTable(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		final RelativeLayout mainView = (RelativeLayout) LayoutInflater.from(
				context).inflate(R.layout.transaction_table, null);

		transactions = new ArrayList<TransactionDetail>();
		title = (TextView) mainView.findViewById(R.id.table_title);
		transactionsList = (LinearLayout) mainView
				.findViewById(R.id.transactions);
		message = (TextView) mainView.findViewById(R.id.no_transactions);
		noMessage = (View) mainView.findViewById(R.id.bottom_no_message);

		addView(mainView);

	}

	/**
	 * Show the transactions in the linear layout
	 * 
	 * @param transactions
	 *            - transactions to show in the linear layout
	 */
	public void showTransactions(final List<TransactionDetail> transactions) {
		if (null == transactions || transactions.isEmpty()) {
			showNoTransactionMessage();
			isWhiteBackground = false;
		} else {
			transactionsList.setVisibility(View.VISIBLE);
			message.setVisibility(View.GONE);
			noMessage.setVisibility(View.GONE);
			for (TransactionDetail transaction : transactions) {
				TransactionItem item = new TransactionItem(this.getContext(),
						null, transaction);
				transactionsList.addView(item);
				item.setBackgroundResource((isWhiteBackground) ? R.color.white
						: R.color.transaction_table_stripe);
				isWhiteBackground = (isWhiteBackground) ? false : true;
			}
		}
	}

	/**
	 * Show the no transactions view
	 */
	private void showNoTransactionMessage() {
		transactionsList.setVisibility(View.GONE);
		message.setVisibility(View.VISIBLE);
		noMessage.setVisibility(View.VISIBLE);
	}

	/**
	 * Clear the list
	 */
	public void clearList() {
		transactionsList.removeAllViews();
		if (transactions != null) {
			this.transactions.clear();
		}
	}

	/**
	 * Set the title of the table
	 * 
	 * @param title
	 *            - string that should be shown in the title
	 */
	public void setTitle(final String title) {
		this.title.setText(title);
	}

	/**
	 * Set the transactions being displayed in the linear layout
	 * 
	 * @param transactions
	 *            - the transactions being displayed in the linear layout
	 */
	public void setTransactions(final List<TransactionDetail> transactions) {
		if (null == this.transactions || this.transactions.isEmpty()) {
			this.transactions = transactions;
		} else {
			this.transactions.addAll(transactions);
		}
	}

	/**
	 * Set the message that should be shown if there are no transactions
	 * 
	 * @param message
	 *            - message that should be shown if there are no transactions
	 */
	public void setNoTransactionsMessage(final String message) {
		this.message.setText(message);
	}

	/**
	 * Get the transactions the table has
	 */
	public List<TransactionDetail> getTransactions() {
		return transactions;
	}
}
