/*
 * � Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.transfer;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.error.BankErrorHandlerDelegate;
import com.discover.mobile.bank.framework.BankUser;
import com.discover.mobile.bank.services.error.BankError;
import com.discover.mobile.bank.services.error.BankErrorResponse;
import com.discover.mobile.bank.services.transfer.TransferDetail;
import com.discover.mobile.bank.ui.widgets.AmountValidatedEditField;
import com.discover.mobile.bank.util.BankStringFormatter;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.nav.NavigationRootActivity;
import com.discover.mobile.common.ui.widgets.CalendarFragment;
import com.discover.mobile.common.ui.widgets.CalendarListener;
import com.discover.mobile.common.ui.widgets.PositiveIntegerEditText;
import com.discover.mobile.common.utils.StringUtility;

/**
 * View for the reocurring transfer widget
 * @author jthornton
 *
 */
public class BankFrequencyDetailView extends RelativeLayout implements BankErrorHandlerDelegate {	
	/**Rotation Key Values*/
	private static final String RADIO = "radio";
	private static final String DATE_VALUE = "dateValue";
	private static final String TRANS_VALUE = "transValue";
	private static final String AMOUNT_VALUE = "amountValue";
	private static final String DISPLAY_CALENDAR = "display-calendar";
	private static final int CANCELLED = 0;
	private static final int DATE = 1;
	private static final int TRANSACTION = 2;
	private static final int AMOUNT = 3;
	private static final int MAX_TRANSFERS_ALLOWED = 3;

	
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
	private final PositiveIntegerEditText transactionAmount;

	/**Text view holding the date*/
	private final TextView dateValue;

	/**View of the layout*/
	private final View view;

	/**Application Resources*/
	private final Resources res;

	/** Earliest payment date */
	private final Calendar earliestPaymentDate;

	/** Chosen payment date */
	private final Calendar chosenPaymentDate;

	/** Fragment used to select a payment date*/
	private CalendarFragment calendarFragment;
	

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
		date = (RadioButton) view.findViewById(R.id.date_button);
		transaction = (RadioButton) view.findViewById(R.id.transactions_button);
		dollar = (RadioButton) view.findViewById(R.id.dollar_button);
		dollarAmount = (AmountValidatedEditField) view.findViewById(R.id.amount_edit);
		transactionAmount = (PositiveIntegerEditText) view.findViewById(R.id.transaction_amount);
		dateValue = (TextView) view.findViewById(R.id.date_value);

		view.findViewById(R.id.cancelled_layout).setOnClickListener(getLayoutListener(CANCELLED));
		view.findViewById(R.id.date_layout).setOnClickListener(getLayoutListener(DATE));
		view.findViewById(R.id.transaction_layout).setOnClickListener(getLayoutListener(TRANSACTION));
		view.findViewById(R.id.dollar_layout).setOnClickListener(getLayoutListener(AMOUNT));

		dollarAmount.enableBankAmountTextWatcher(true);
		
		transactionAmount.setMaxInputLength(MAX_TRANSFERS_ALLOWED);
		earliestPaymentDate = Calendar.getInstance();
		earliestPaymentDate.add(Calendar.DAY_OF_MONTH, 1);
		chosenPaymentDate = Calendar.getInstance();
		chosenPaymentDate.add(Calendar.DAY_OF_MONTH, 1);
		
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
			public final void onClick(final View v) {
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
		outState.putBoolean(DISPLAY_CALENDAR, (calendarFragment != null));
		
		return outState;
	}
	
	/**
	 * Resume the sate of the view
	 * @param bundle - bundle containing the data
	 */
	public void resumeState(final Bundle bundle){
		if(bundle != null){
			index = bundle.getInt(RADIO, CANCELLED);
			final String savedDate = bundle.getString(DATE_VALUE);
			final String savedTransactionLimit = bundle.getString(TRANS_VALUE);
			final String savedAmount = bundle.getString(AMOUNT_VALUE);
			if(null != savedDate){
				dateValue.setText(savedDate);
			}
			transactionAmount.setText(savedTransactionLimit);
			dollarAmount.setText(savedAmount);
			disableCancelled();
			setRadioButtonState(index);
			
			/** Reset Calendar Event Listener */
			final Fragment fragment = ((NavigationRootActivity) DiscoverActivityManager.getActiveActivity()).getSupportFragmentManager()
					.findFragmentByTag(CalendarFragment.TAG);
			/** Verify calendar was being shown before recreating listeners */
			if (fragment != null && fragment instanceof CalendarFragment && bundle.getBoolean(DISPLAY_CALENDAR, false)) {
				calendarFragment = (CalendarFragment) fragment;
				calendarFragment.setCalendarListener(createCalendarListener());
			}
			
		}
	}

	/**
	 * Updates the selected radio button to the passed index value.
	 * 
	 * @param selectedIndex
	 */
	private void setRadioButtonState(final int selectedIndex) {
		switch(selectedIndex){
		case CANCELLED:
			enableCancelled();
			break;
		case DATE:
			enableDate();
			break;
		case TRANSACTION:
			enableTransaction();
			break;
		case AMOUNT:
			enableAmount();
			break;
		default:
			break;
		}
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
				value = BankStringFormatter.convertToISO8601Date(dateValue.getText().toString(),false);
				break;
			case TRANSACTION:
				value = transactionAmount.getText().toString();
				break;
			case AMOUNT:
				value = dollarAmount.getText().toString().
												replaceAll(StringUtility.NON_NUMBER_CHARACTERS, StringUtility.EMPTY);
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
				showCalendar();
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
			default:
				break;
		}

	}

	/**
	 * Disable the cancelled cell
	 */
	private void disableCancelled(){
		cancelled.setChecked(false);
		((TextView)view.findViewById(R.id.canceled_label)).setTextColor(res.getColor(R.color.field_copy));
	}

	/**
	 * Disable the date cell
	 */
	private void disableDate(){
		date.setChecked(false);
		((TextView)view.findViewById(R.id.date_label)).setTextColor(res.getColor(R.color.field_copy));
		((TextView)view.findViewById(R.id.date_value)).setTextColor(res.getColor(R.color.field_copy));
		final ImageView caret = (ImageView)view.findViewById(R.id.freq_caret);
		caret.setImageDrawable(getResources().getDrawable(R.drawable.gray_right_arrow));
		clearErrorLabel(R.id.date_error_label);
		((TextView)view.findViewById(R.id.date_value)).setText(getResources().getString(R.string.transfer_selected_date));
	}
	
	private void clearErrorLabel(final int labelResId) {
		final TextView errorLabel = (TextView)findViewById(labelResId);
		if(errorLabel != null) {
			errorLabel.setText("");
			errorLabel.setVisibility(View.GONE);
		}else {
			Log.e(BankFrequencyDetailView.class.getSimpleName(), "Could not hide error label, Resource ID not found");
		}
	}
	
	/**
	 * Disable the transaction cell
	 */
	private void disableTransaction(){
		transactionAmount.clearFocus();
		transaction.setChecked(false);
		((TextView)view.findViewById(R.id.transactions_label)).setTextColor(res.getColor(R.color.field_copy));
		transactionAmount.clearErrors();
		transactionAmount.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
		transactionAmount.setText("");
		clearErrorLabel(R.id.transactions_error_label);
	}

	/**
	 * Disable the amount cell
	 */
	private void disableAmount(){
		dollar.setChecked(false);
		((TextView)view.findViewById(R.id.dollar_label)).setTextColor(res.getColor(R.color.field_copy));
		((TextView)view.findViewById(R.id.dollar)).setTextColor(res.getColor(R.color.field_copy));
		dollarAmount.clearFocus();
		dollarAmount.enableBankAmountTextWatcher(false);
		dollarAmount.setText("");
		dollarAmount.enableBankAmountTextWatcher(true);
		dollarAmount.setupDefaultAppearance();
		dollarAmount.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
		clearErrorLabel(R.id.dollar_error_label);
	}

	/**
	 * Enable the cancelled cell
	 */
	private void enableCancelled(){
		index = CANCELLED;
		cancelled.setChecked(true);
		((TextView)view.findViewById(R.id.canceled_label)).setTextColor(res.getColor(R.color.body_copy));
	}

	/**
	 * Enable the date cell
	 */
	private void enableDate(){
		index = DATE;
		date.setChecked(true);
		((TextView)view.findViewById(R.id.date_value)).setTextColor(res.getColor(R.color.body_copy));
		final ImageView caret = (ImageView)view.findViewById(R.id.freq_caret);
		caret.setImageDrawable(getResources().getDrawable(R.drawable.blue_arrow_right));
	}

	/**
	 * Enable the transaction cell
	 */
	private void enableTransaction(){
		index = TRANSACTION;
		transaction.setChecked(true);
		((TextView)view.findViewById(R.id.transactions_label)).setTextColor(res.getColor(R.color.body_copy));
		transactionAmount.setImeOptions(EditorInfo.IME_ACTION_DONE);
		transactionAmount.requestFocus();
	}

	/**
	 * Enable the amount cell
	 */
	private void enableAmount(){
		index = AMOUNT;
		dollar.setChecked(true);
		((TextView)view.findViewById(R.id.dollar)).setTextColor(res.getColor(R.color.body_copy));
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

	/**
	 * Method displays a calendar in a dialog form with the chosen date selected.
	 */
	public void showCalendar() {
		/** Verify a calendar is not already being shown */
		if (null == calendarFragment) {
			calendarFragment = new CalendarFragment();

			/** The calendar will appear with the month and year in this Calendar instance */
			Calendar displayedDate = Calendar.getInstance();
			
			
			/** Convert stored in text field into chosen date, this will avoid issue on rotation */
			try {
				final String[] date = dateValue.getText().toString().split("[\\/]+");

				/** The Calendar will appear with the date specified by this calendar instance selected */
				chosenPaymentDate.set(Integer.parseInt(date[2]), Integer.parseInt(date[0]) - 1, Integer.parseInt(date[1]));

				/** Check if restoring calendar selection date, -1 means it is initializing */
				displayedDate = chosenPaymentDate;

			} catch (final Exception ex) {
				chosenPaymentDate.set(earliestPaymentDate.get(Calendar.YEAR), chosenPaymentDate.get(Calendar.MONTH),
						chosenPaymentDate.get(Calendar.DAY_OF_MONTH));

				displayedDate = chosenPaymentDate;
			}
			
			/** Show calendar as a dialog */
			calendarFragment.show(((NavigationRootActivity) DiscoverActivityManager.getActiveActivity()).getSupportFragmentManager(), res
					.getString(R.string.select_transfer_date), displayedDate, chosenPaymentDate, earliestPaymentDate, BankUser.instance()
					.getHolidays(), createCalendarListener());
		}
	}

	/**
	 * Create the calendar listener
	 */
	private CalendarListener createCalendarListener() {
		// Setup listener
		final CalendarListener calendarListener = new CalendarListener(calendarFragment) {
			private static final long serialVersionUID = -5277452816704679940L;
			
			@Override
			public void onSelectDate(final Date date, final View view) {
				super.onSelectDate(date, view);
				final long halfSecondDelay = 500;
				final Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				setChosenPaymentDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE));
				
				//Delay closing of calendar to be able to see the selection change
				new Handler().postDelayed(getCalendarDissmissRunnable(), halfSecondDelay);
			}

			@Override
			public void onCancel() {
				/** Clear reference to calendar fragment so that it can be recreated and shown again */
				calendarFragment = null;
			}
		};
		return calendarListener;
	}
	
	/**
	 * 
	 * @return a runnable, which when, is run, dismisses the calendar fragment
	 */
	private Runnable getCalendarDissmissRunnable() {
		return new Runnable() {
			
			@Override
			public void run() {
				if(calendarFragment != null){
					calendarFragment.dismiss();

					/** Clear reference to calendar fragment so that it can be recreated and shown again */
					calendarFragment = null;
				}
			}
		};
	}

	/**
	 * Updates the chosen date Calendar variable, {@code chosenPaymentDate}. If
	 * the date is earlier than the earliest possible date, then it is set to
	 * that. This additionally updates the text view.
	 * 
	 * @param year
	 * @param month
	 * @param day
	 */
	private void setChosenPaymentDate(final Integer year, final Integer month,
			final Integer day) {

		dateValue.setText(BankStringFormatter.formatDate(year.toString(),
				BankStringFormatter.formatDayOfMonth(month), BankStringFormatter.formatDayOfMonth(day)));
		chosenPaymentDate.set(year, month - 1, day);
	}

	/**
	 * Handle the inline errors that the server responds with.
	 */
	@Override
	public boolean handleError(final BankErrorResponse msgErrResponse) {
		boolean isHandled = false;
		
		for(final BankError error : msgErrResponse.errors) {
			final String errorFieldName = error.name;
			
			if(TransferDetail.DURATION_VALUE.equalsIgnoreCase(errorFieldName)){
				TextView errorLabel = null;
				isHandled = true;
				final String durationType = getDurationType();
				
				if(TransferDetail.UNTIL_AMOUNT.equalsIgnoreCase(durationType)) {
					errorLabel = (TextView)this.findViewById(R.id.dollar_error_label);
					errorLabel.setText(error.message);
				} else if (TransferDetail.UNTIL_DATE.equalsIgnoreCase(durationType)) {
					errorLabel = (TextView)this.findViewById(R.id.date_error_label);
					errorLabel.setText(error.message);
				} else if (TransferDetail.UNTIL_COUNT.equalsIgnoreCase(durationType)) {
					errorLabel = (TextView)this.findViewById(R.id.transactions_error_label);
					errorLabel.setText(error.message);
				} else{
					isHandled = false;
				}
				if(isHandled) {
					errorLabel.setVisibility(View.VISIBLE);
				}
				
			}
		}
		return isHandled;
	}

}
