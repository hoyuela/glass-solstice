/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.transfer;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.ui.widgets.AmountValidatedEditField;
import com.discover.mobile.common.ui.widgets.SsnEditText;

public class BankFrequencyDetailView extends RelativeLayout{

	/**Rotation Key Values*/
	private static final String RADIO = "radio";
	private static final String DATE_VALUE = "dateValue";
	private static final String TRANS_VALUE = "transValue";
	private static final String AMOUNT_VALUE = "amountValue";
	private static final int CANCELLED = 0;
	private static final int DATE = 1;
	private static final int TRANSACTION = 2;
	private static final int AMOUNT = 3;

	/**Selected Radio Index*/
	private int index;

	/**Cancelled radio button*/
	private final RadioButton cancelled;

	/**Date radio button*/
	private final RadioButton date;

	/**Transactions radio button*/
	private final RadioButton transaction;

	/**Dollar Amount Radio Button*/
	private final RadioButton dollar;

	/**Dollar Amount edit text*/
	private final AmountValidatedEditField dollarAmount;

	/**Transaction Amount field*/
	private final SsnEditText transactionAmount;

	/**Text view holding the date*/
	private final TextView dateValue;

	/**View of the layout*/
	private final View view;

	/**Application Resources*/
	private final Resources res;

	public BankFrequencyDetailView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		view = LayoutInflater.from(context).inflate(R.layout.bank_frequency_detail_view, null);
		res = context.getResources();
		cancelled = (RadioButton) view.findViewById(R.id.cancelled_button);
		date = (RadioButton) view.findViewById(R.id.date_button);
		transaction = (RadioButton) view.findViewById(R.id.transactions_button);
		dollar = (RadioButton) view.findViewById(R.id.dollar_button);
		dollarAmount = (AmountValidatedEditField) view.findViewById(R.id.amount_edit);
		transactionAmount = (SsnEditText) view.findViewById(R.id.transaction_amount);
		dateValue = (TextView) view.findViewById(R.id.date_value);

		((LinearLayout) view.findViewById(R.id.cancelled_layout)).setOnClickListener(getLayoutListener(CANCELLED));
		((LinearLayout) view.findViewById(R.id.date_layout)).setOnClickListener(getLayoutListener(DATE));
		((LinearLayout) view.findViewById(R.id.transaction_layout)).setOnClickListener(getLayoutListener(TRANSACTION));
		((LinearLayout) view.findViewById(R.id.dollar_layout)).setOnClickListener(getLayoutListener(AMOUNT));

		dollarAmount.setEnabled(false);
		transactionAmount.setEnabled(false);

		addView(view);
	}

	private OnClickListener getLayoutListener(final int index) {
		return new OnClickListener(){
			@Override
			public void onClick(final View v) {
				enableCell(index);				
			}
		};
	}

	public Bundle savedDate(final Bundle outState){
		outState.putInt(RADIO, index);
		outState.putString(DATE_VALUE, dateValue.getText().toString());
		outState.putString(TRANS_VALUE, transactionAmount.getText().toString());
		outState.putString(AMOUNT_VALUE, dollarAmount.getText().toString());

		return outState;
	}

	public void resumeState(final Bundle bundle){
		index = bundle.getInt(RADIO, CANCELLED);
		final String date = bundle.getString(DATE_VALUE);
		final String transaction = bundle.getString(TRANS_VALUE);
		final String amount = bundle.getString(AMOUNT_VALUE);
		if(null != date){
			dateValue.setText(date);
			transactionAmount.setText(transaction);
			dollarAmount.setText(amount);
		}
		enableCell(index);
	}

	private void enableCell(final int selected) {
		switch(selected){
		case CANCELLED:
			enableCancelled();
			disableDate();
			disableTransaction();
			disableAmount();
			break;
		case DATE:
			disableCancelled();
			enableDate();
			disableTransaction();
			disableAmount();
			break;
		case TRANSACTION:
			disableCancelled();
			disableDate();
			enableTransaction();
			disableAmount();
			break;
		case AMOUNT:
			disableCancelled();
			disableDate();
			disableTransaction();
			enableAmount();
			break;
		}

	}

	private void disableCancelled(){
		cancelled.setChecked(false);
		((TextView)view.findViewById(R.id.canceled_label)).setTextColor(res.getColor(R.color.field_copy));
	}

	private void disableDate(){
		date.setChecked(false);
		((TextView)view.findViewById(R.id.date_label)).setTextColor(res.getColor(R.color.field_copy));
		((TextView)view.findViewById(R.id.date_value)).setTextColor(res.getColor(R.color.field_copy));
	}

	private void disableTransaction(){
		transaction.setChecked(false);
		((TextView)view.findViewById(R.id.transactions_label)).setTextColor(res.getColor(R.color.field_copy));
		transactionAmount.clearFocus();
		transactionAmount.clearErrors();
		transactionAmount.setEnabled(false);
	}

	private void disableAmount(){
		dollar.setChecked(false);
		((TextView)view.findViewById(R.id.dollar_label)).setTextColor(res.getColor(R.color.field_copy));
		((TextView)view.findViewById(R.id.dollar)).setTextColor(res.getColor(R.color.field_copy));
		dollarAmount.clearFocus();
		dollarAmount.clearErrors();
		dollarAmount.setEnabled(false);
	}

	private void enableCancelled(){
		cancelled.setChecked(true);
		((TextView)view.findViewById(R.id.canceled_label)).setTextColor(res.getColor(R.color.body_copy));
		hideKeyboard();
	}

	private void enableDate(){
		date.setChecked(true);
		((TextView)view.findViewById(R.id.date_value)).setTextColor(res.getColor(R.color.body_copy));
		hideKeyboard();
	}

	private void enableTransaction(){
		transaction.setChecked(true);
		((TextView)view.findViewById(R.id.transactions_label)).setTextColor(res.getColor(R.color.body_copy));
		transactionAmount.setEnabled(true);
		transactionAmount.requestFocus();
		hideKeyboard();
		showKeyboard();
	}

	private void enableAmount(){
		dollar.setChecked(true);
		((TextView)view.findViewById(R.id.dollar)).setTextColor(res.getColor(R.color.body_copy));
		dollarAmount.setEnabled(true);
		dollarAmount.requestFocus();
		hideKeyboard();
		showKeyboard();
	}

	private void showKeyboard(){
		final InputMethodManager imm = (InputMethodManager) this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
	}

	private void hideKeyboard(){
		final InputMethodManager imm = (InputMethodManager) this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(),0); 
	}
}
