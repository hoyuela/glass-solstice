/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.transfer;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.ui.widgets.AmountValidatedEditField;

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
	private final EditText transactionAmount;

	/**Text view holding the date*/
	private final TextView dateValue;

	/**View of the layout*/
	private final View view;

	public BankFrequencyDetailView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		view = LayoutInflater.from(context).inflate(R.layout.bank_frequency_detail_view, null);

		cancelled = (RadioButton) view.findViewById(R.id.cancelled_button);
		date = (RadioButton) view.findViewById(R.id.date_button);
		transaction = (RadioButton) view.findViewById(R.id.transactions_button);
		dollar = (RadioButton) view.findViewById(R.id.dollar_button);
		dollarAmount = (AmountValidatedEditField) view.findViewById(R.id.amount_edit);
		transactionAmount = (EditText) view.findViewById(R.id.transaction_amount);
		dateValue = (TextView) view.findViewById(R.id.date_value);

		cancelled.setOnCheckedChangeListener(getCheckedListener());
		date.setOnCheckedChangeListener(getCheckedListener());
		transaction.setOnCheckedChangeListener(getCheckedListener());
		dollar.setOnCheckedChangeListener(getCheckedListener());

		dollarAmount.setEnabled(false);
		transactionAmount.setEnabled(false);

		addView(view);
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
			enableAmount();
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

	}

	private void disableDate(){

	}

	private void disableTransaction(){

	}

	private void disableAmount(){

	}

	private void enableCancelled(){

	}

	private void enableDate(){

	}

	private void enableTransaction(){

	}

	private void enableAmount(){

	}

	private OnCheckedChangeListener getCheckedListener(){
		return new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
				if(isChecked){
					if(cancelled.getId() != buttonView.getId()){
						cancelled.setChecked(false);
					}else{
						index = CANCELLED;
					}
					if(date.getId() != buttonView.getId()){
						date.setChecked(false);
					}else{
						index = DATE;
					}
					if(transaction.getId() != buttonView.getId()){
						transaction.setChecked(false);
					}else{
						index = TRANSACTION;
					}
					if(dollar.getId() != buttonView.getId()){
						dollar.setChecked(false);
					}else{
						index = AMOUNT;
					}
				}
			}
		};	
	}

}
