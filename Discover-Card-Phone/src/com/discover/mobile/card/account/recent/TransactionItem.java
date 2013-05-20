package com.discover.mobile.card.account.recent;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.card.R;
import com.discover.mobile.card.services.account.recent.TransactionDetail;

/**
 * Item shown in the transaction table
 * 
 * @author jthornton
 * 
 */
public class TransactionItem extends RelativeLayout {

    /** Date associated with the item */
    private final TextView date;

    /** Description of the transaction */
    private final TextView description;

    /** Amount of the transactions */
    private final TextView amount;

    /** Transaction being displayed in the layout */
    private final TransactionDetail transaction;

    /** String meaning the amount was a debit */
    private static final String DEBIT = "-";

    /**
     * Constructor of the class
     * 
     * @param context
     *            - activity context
     * @param attrs
     *            - attributes to give to the layout
     */
    public TransactionItem(final Context context, final AttributeSet attrs,
            final TransactionDetail transaction) {
        super(context, attrs);

        final RelativeLayout mainView = (RelativeLayout) LayoutInflater.from(
                context).inflate(R.layout.transaction_item, null);

        date = (TextView) mainView.findViewById(R.id.transaction_date);
        description = (TextView) mainView
                .findViewById(R.id.transaction_description);
        amount = (TextView) mainView.findViewById(R.id.transaction_amount);
        this.transaction = transaction;

        if (transaction.amount.contains(DEBIT)) {
            amount.setTextColor(context.getResources().getColor(
                    R.color.string_indicator));
        }

        showTransaction();
        addView(mainView);
    }

    /**
     * Show the transaction information
     */
    private void showTransaction() {
        date.setText(transaction.date);
        description.setText(transaction.description);
        amount.setText(transaction.amount);
    }
}