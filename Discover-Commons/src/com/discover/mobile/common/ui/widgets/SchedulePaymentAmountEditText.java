package com.discover.mobile.common.ui.widgets;

import android.content.Context;
import android.content.res.Configuration;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import com.discover.mobile.common.ui.InvalidAmountCharacterFilter;
import com.discover.mobile.common.utils.CommonUtils;

public class SchedulePaymentAmountEditText extends ValidatedInputField {

	/** Maximum payment amount */
	private static final double MAX_AMOUNT = 25000.0;
	/** Minimum payment amount */
	private static final double MIN_AMOUNT = 1.0;
	/** True when the amount had focus at some point */
	private boolean amountHadFocus = false;
	/** Too High Error message */
	private String amountTooHigh = null;
	/** Too Low Error message */
	private String amountTooLow = null;

	public SchedulePaymentAmountEditText(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		setAmountFieldRestrictions();
	}

	@Override
	public boolean isValid() {
		final String inAmount = this.getText().toString();
		String outAmount = CommonUtils
				.formatCurrencyAsStringWithoutSign(inAmount);
		outAmount = outAmount.replaceAll(",", "");
		
		Double d;
		try {
			d = Double.parseDouble(outAmount);
		} catch (final Exception e) {
			d = 0.0d;
		}
		final String amountSplit[] = d.toString().split(".");
		// Values cannot be greater than 25,000.00. This truncates it to resolve a "defect".
		
		if (amountSplit.length > 1) {
			outAmount = amountSplit[0];
			if(outAmount.length() > 4) {
				outAmount = outAmount.substring(outAmount.length() - 5,
						outAmount.length());
			}
			outAmount = outAmount + "." + amountSplit[1];
			try {
				d = Double.parseDouble(outAmount);
			} catch (final Exception e) {
				d = 0.0d;
			}
		}
		if (d < MIN_AMOUNT) {
			if (amountTooLow != null) {
				this.errorLabel.setText(amountTooLow);
			}
			return false;

		} else if (d > MAX_AMOUNT) {
			if (amountTooHigh != null) {
				this.errorLabel.setText(amountTooHigh);
			}
			return false;
		}
		return true;
	}

	/**
	 * Sets the error labels for when a value is too high or too low.
	 * 
	 * @param low
	 * @param high
	 */
	public void setLowAndHighErrorText(final String low, final String high) {
		if (low != null) {
			amountTooLow = low;
		}
		if (high != null) {
			amountTooHigh = high;
		}
	}
	
	/**
	 * Sets a focus changed listener that will validate and update the appearance of the input field
	 * upon focus change.
	 */
	@Override
	protected void setupFocusChangedListener() {
		this.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(final View v, final boolean hasFocus) {
				clearRightDrawable();
								
				//If Lost Focus
				if( !hasFocus ){
					amountHadFocus = true;
					updateAppearanceForInput();
					if(!isInErrorState) {
						clearErrors();
						clearRightDrawable();
					}
				}
				//If Selected/Has Focus
				else {
					amountHadFocus = false;
					setRightDrawableGrayX();
					if(isInErrorState)
						setRightDrawableRedX();
				}
				
				if(amountHadFocus) {
					setFormattedText(v);
				}
			}

			private void setFormattedText(final View v) {
				final String inAmount = ((EditText)v).getText().toString();
				String outAmount = CommonUtils
						.formatCurrencyAsStringWithoutSign(inAmount);
				outAmount = outAmount.replace(",", "");
				final String amountSplit[] = outAmount.split("\\.");
				if (amountSplit.length > 1) {
					// Values cannot be greater than 25,000.00. This truncates
					// it to resolve a "defect".
					outAmount = amountSplit[0];
					if (outAmount.length() > 4) {
						outAmount = outAmount.substring(outAmount.length() - 5,
								outAmount.length());
					}
					outAmount = outAmount + "." + amountSplit[1];
					outAmount = CommonUtils
							.formatCurrencyAsStringWithoutSign(outAmount);
				}
				((EditText)v).setText(outAmount);
			}
		});
	}
	
	/**
	 * Sets restrictions related to the amount-to-pay field.
	 */
	private void setAmountFieldRestrictions() {
		final InputFilter[] filters = new InputFilter[2];
		filters[0] = new InvalidAmountCharacterFilter();
		filters[1] = new InputFilter.LengthFilter(9);
		this.setFilters(filters);

		// Shows numeric keyboard as default.
		this.setRawInputType(Configuration.KEYBOARD_QWERTY);
	}
}	