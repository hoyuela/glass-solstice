package com.discover.mobile.common.ui.widgets;

import android.content.Context;
import android.content.res.Configuration;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.common.R;
import com.discover.mobile.common.ui.InvalidAmountCharacterFilter;
import com.discover.mobile.common.utils.CommonUtils;

public class SchedulePaymentAmountEditText extends ValidatedInputField {

	/** Maximum payment amount */
	private final double MAX_AMOUNT = 25000.0;
	/** Minimum payment amount */
	private final double MIN_AMOUNT = 1.0;
	/** True when the amount had focus at some point */
	private boolean amountHadFocus = false;
	/** Too High Error message */
	private String amountTooHigh = null;
	/** Too Low Error message */
	private String amountTooLow = null;

	/** Potential layout to stretch during errors */
	RelativeLayout amountCell;
	/** TextView error label for values too high/low */
	TextView amountError;

	public SchedulePaymentAmountEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		setAmountFieldRestrictions();
	}

	@Override
	public boolean isValid() {
		String inAmount = this.getText().toString();
		String outAmount = CommonUtils
				.formatCurrencyAsStringWithoutSign(inAmount);
		outAmount = outAmount.replaceAll(",", "");
		
		Double d;
		try {
			d = Double.parseDouble(outAmount);
		} catch (Exception e) {
			d = 0.0d;
		}
		final String amountSplit[] = d.toString().split(".");
		// Values cannot be greater than 25,000.00. This truncates it to resolve a "defect".
		
		if (amountSplit.length > 1) {
			outAmount = amountSplit[0];
			outAmount = outAmount.substring(outAmount.length() - 5,
					outAmount.length());
			outAmount = outAmount + "." + amountSplit[1];
			try {
				d = Double.parseDouble(outAmount);
			} catch (Exception e) {
				d = 0.0d;
			}
		}
		if (d < MIN_AMOUNT) {
			if (amountTooLow != null) {
				amountError.setText(amountTooLow);
			}
			return false;

		} else if (d > MAX_AMOUNT) {
			if (amountTooHigh != null) {
				amountError.setText(amountTooHigh);
			}
			return false;
		}
		return true;
	}

	/**
	 * Attach the id of a textView to show for values too high and too low.
	 * Additionally, if the label sits within a list cell, pass the reference of
	 * that too.
	 * 
	 * @param textViewId
	 *            Id of the textview to show/hide during errors.
	 * @param relativeLayoutCellId
	 *            Id of the layout to resize for error label. Null is allowed.
	 */
	public void attachErrorLabelAndLayout(TextView errorText,
			RelativeLayout relativeLayoutCell) {
		if (relativeLayoutCell != null) {
			amountCell = relativeLayoutCell;
		}
		if (errorText != null) {
			amountError = errorText;
		}
	}

	/**
	 * Shows the error labels and stretches the cell.
	 */
	@Override
	public void setErrors() {
		super.setErrors();
		if (amountCell != null) {
			int padding = (int) getResources().getDimension(
					R.dimen.between_related_elements_padding);
			amountCell.getLayoutParams().height = (int) getResources()
					.getDimension(R.dimen.listview_vertical_height)
					+ (int) getResources().getDimension(R.dimen.small_copy_mid);
			amountCell.setPadding(padding, padding, padding, padding);
			amountCell.invalidate();
		}
		if (amountError != null) {
			amountError.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Hides the error labels and shrinks the cell.
	 */
	@Override
	public void clearErrors() {
		super.clearErrors();
		if (amountCell != null) {
			int padding = (int) getResources().getDimension(
					R.dimen.between_related_elements_padding);
			amountCell.getLayoutParams().height = (int) getResources()
					.getDimension(R.dimen.listview_vertical_height);
			amountCell.setPadding(padding, padding, padding, padding);
			amountCell.invalidate();
		}
		if (amountError != null) {
			amountError.setVisibility(View.GONE);
		}
	}

	/**
	 * Sets the error labels for when a value is too high or too low.
	 * 
	 * @param low
	 * @param high
	 */
	public void setLowAndHighErrorText(String low, String high) {
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

			private void setFormattedText(View v) {
				final String inAmount = ((EditText)v).getText().toString();
				String outAmount = CommonUtils
						.formatCurrencyAsStringWithoutSign(inAmount);
				outAmount = outAmount.replace(",", "");
				String amountSplit[] = outAmount.split("\\.");
				if (amountSplit.length > 1) {
					// Values cannot be greater than 25,000.00. This truncates
					// it to resolve a "defect".
					outAmount = amountSplit[0];
					outAmount = outAmount.substring(outAmount.length() - 5,
							outAmount.length());
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
		InputFilter[] filters = new InputFilter[2];
		filters[0] = new InvalidAmountCharacterFilter();
		filters[1] = new InputFilter.LengthFilter(9);
		this.setFilters(filters);

		// Shows numeric keyboard as default.
		this.setRawInputType(Configuration.KEYBOARD_QWERTY);
	}
}	