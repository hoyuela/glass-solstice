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
import com.discover.mobile.bank.services.transfer.TransferDetail;
import com.discover.mobile.bank.ui.widgets.AmountValidatedEditField;
import com.discover.mobile.common.ui.widgets.SsnEditText;

/**
 * View for the reocurring transfer widget
 * @author jthornton
 *
 */
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

	/**
	 * Constructor for the view
	 * @param context - activity context
	 * @param attrs - attributes to apply to the layout
	 */
	public BankFrequencyDetailView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		view = LayoutInflater.from(context).inflate(R.layout.bank_frequency_detail_view, null);
		res = context.getResources();
		cancelled = (RadioButton) view.findViewById(R.id.cancelled_button);
		cancelled.setEnabled(false);
		date = (RadioButton) view.findViewById(R.id.date_button);
		date.setEnabled(false);
		transaction = (RadioButton) view.findViewById(R.id.transactions_button);
		transaction.setEnabled(false);
		dollar = (RadioButton) view.findViewById(R.id.dollar_button);
		dollar.setEnabled(false);
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

	/**
	 * Get the listener for when layouts are clicked
	 * @param index
	 * @return
	 */
	private OnClickListener getLayoutListener(final int index) {
		return new OnClickListener(){
			@Override
			public void onClick(final View v) {
				enableCell(index);				
			}
		};
	}

	/**
	 * Save the state of the view
	 * @param outState - the bundle to put the data in
	 * @return the bundle of data
	 */
	public Bundle saveState(final Bundle outState){
		outState.putInt(RADIO, index);
		outState.putString(DATE_VALUE, dateValue.getText().toString());
		outState.putString(TRANS_VALUE, transactionAmount.getText().toString());
		outState.putString(AMOUNT_VALUE, dollarAmount.getText().toString());

		return outState;
	}

	/**
	 * Resume the sate of the view
	 * @param bundle - bundle containing the data
	 */
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

	/**
	 * Get the duration type for the service call
	 * @return the duration type for the service call
	 */
	public String getDurationType(){
		String duration;
		switch(index){
		case CANCELLED:
			duration = TransferDetail.UNTIL_CANCELLED;
			break;
		case DATE:
			duration = TransferDetail.UNTIL_DATE;
			break;
		case TRANSACTION:
			duration = TransferDetail.UNTIL_COUNT;
			break;
		case AMOUNT:
			duration = TransferDetail.UNTIL_AMOUNT;
			break;
		default:
			duration = TransferDetail.UNTIL_CANCELLED;
			break;
		}
		return duration;
	}

	/**
	 * Get the duration value for the service call
	 * @return the duration value for the service call
	 */
	public String getDurationValue(){
		final String value;
		switch(index){
		case DATE:
			value = dateValue.getText().toString();
			break;
		case TRANSACTION:
			value = transactionAmount.getText().toString();
			break;
		case AMOUNT:
			value = dollarAmount.getText().toString().replaceAll("[^0-9]", "");
			break;
		default:
			value = "";
			break;
		}
		return value;
	}

	/**
	 * Enable the correct cells based on the radio button selected
	 * @param selected - radio button selected
	 */
	private void enableCell(final int selected) {
		switch(selected){
		case CANCELLED:
			enableCancelled();
			disableDate();
			disableTransaction();
			disableAmount();
			hideKeyboard();
			break;
		case DATE:
			disableCancelled();
			enableDate();
			disableTransaction();
			disableAmount();
			hideKeyboard();
			break;
		case TRANSACTION:
			disableCancelled();
			disableDate();
			enableTransaction();
			disableAmount();
			showKeyboard();
			break;
		case AMOUNT:
			disableCancelled();
			disableDate();
			disableTransaction();
			enableAmount();
			showKeyboard();
			break;
		}

	}

	/**
	 * Disable the cancelled cell
	 */
	private void disableCancelled(){
		cancelled.setChecked(false);
		cancelled.setButtonDrawable(R.drawable.make_payment_radio_button);
		((TextView)view.findViewById(R.id.canceled_label)).setTextColor(res.getColor(R.color.field_copy));
	}

	/**
	 * Disable the date cell
	 */
	private void disableDate(){
		date.setChecked(false);
		date.setButtonDrawable(R.drawable.make_payment_radio_button);
		((TextView)view.findViewById(R.id.date_label)).setTextColor(res.getColor(R.color.field_copy));
		((TextView)view.findViewById(R.id.date_value)).setTextColor(res.getColor(R.color.field_copy));
	}

	/**
	 * Disable the transaction cell
	 */
	private void disableTransaction(){
		transactionAmount.clearFocus();
		transaction.setChecked(false);
		transaction.setButtonDrawable(R.drawable.make_payment_radio_button);
		((TextView)view.findViewById(R.id.transactions_label)).setTextColor(res.getColor(R.color.field_copy));
		transactionAmount.clearErrors();
		transactionAmount.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
		transactionAmount.setEnabled(false);
	}

	/**
	 * Disable the amount cell
	 */
	private void disableAmount(){
		dollar.setChecked(false);
		dollar.setButtonDrawable(R.drawable.make_payment_radio_button);
		((TextView)view.findViewById(R.id.dollar_label)).setTextColor(res.getColor(R.color.field_copy));
		((TextView)view.findViewById(R.id.dollar)).setTextColor(res.getColor(R.color.field_copy));
		dollarAmount.setEnabled(false);
		dollarAmount.clearFocus();
		dollarAmount.setupDefaultAppearance();
		dollarAmount.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
	}

	/**
	 * Enable the cancelled cell
	 */
	private void enableCancelled(){
		cancelled.setChecked(true);
		cancelled.setButtonDrawable(R.drawable.make_payment_radio_button_ds);
		((TextView)view.findViewById(R.id.canceled_label)).setTextColor(res.getColor(R.color.body_copy));
	}

	/**
	 * Enable the date cell
	 */
	private void enableDate(){
		date.setChecked(true);
		date.setButtonDrawable(R.drawable.make_payment_radio_button_ds);
		((TextView)view.findViewById(R.id.date_value)).setTextColor(res.getColor(R.color.body_copy));
	}

	/**
	 * Enable the transaction cell
	 */
	private void enableTransaction(){
		transaction.setChecked(true);
		transaction.setButtonDrawable(R.drawable.make_payment_radio_button_ds);
		((TextView)view.findViewById(R.id.transactions_label)).setTextColor(res.getColor(R.color.body_copy));
		transactionAmount.setEnabled(true);
		transactionAmount.requestFocus();
	}

	/**
	 * Enable the amount cell
	 */
	private void enableAmount(){
		dollar.setChecked(true);
		dollar.setButtonDrawable(R.drawable.make_payment_radio_button_ds);
		((TextView)view.findViewById(R.id.dollar)).setTextColor(res.getColor(R.color.body_copy));
		dollarAmount.setEnabled(true);
		dollarAmount.requestFocus();
	}

	/**
	 * Show the keyboard
	 */
	private void showKeyboard(){
		final InputMethodManager imm = (InputMethodManager) this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
	}

	/**
	 * Hide the keyboard
	 */
	private void hideKeyboard(){
		final InputMethodManager imm = (InputMethodManager) this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(),0); 
	}
}
