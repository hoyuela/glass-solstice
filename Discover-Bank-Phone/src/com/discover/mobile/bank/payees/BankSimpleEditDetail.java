package com.discover.mobile.bank.payees;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.R;

/**
 * This class is a reduced version of the BankEditDetail class.
 * It removes all of the listener interfaces and extra interface elements that were not needed for
 * a list without 'ghost' cells.
 * 
 * @author scottseward
 *
 */
public class BankSimpleEditDetail extends RelativeLayout {
	protected TextView topLabel;
	protected TextView middleLabel;
	protected TextView errorLabel;
	protected View dividerLine;
	protected View caret;
	protected View view;
	
	public BankSimpleEditDetail(final Context context) {
		super(context);
		doSetup(context);
	}
	
	public BankSimpleEditDetail(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		doSetup(context);
	}
	public BankSimpleEditDetail(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		doSetup(context);
	}

	protected void doSetup(final Context context) {
		addView(getInflatedLayout(context));
		loadViews();
	}
	
	private RelativeLayout getInflatedLayout(final Context context) {
		return (RelativeLayout)LayoutInflater.from(context).inflate(R.layout.bank_simple_edit_detail_view, null);
	}
	
	/**
	 * Retrieve refrences to all necessary interface items.
	 */
	protected void loadViews() {
		view = findViewById(R.id.list_item);
		caret = findViewById(R.id.caret);
		topLabel = (TextView)findViewById(R.id.top_label);
		middleLabel = (TextView)findViewById(R.id.middle_label);
		errorLabel = (TextView)findViewById(R.id.error_label);	
		dividerLine = findViewById(R.id.divider_line);
	}
	
	/**
	 * @return Returns a reference to the View displaying the divider line.
	 */
	public View getDividerLine() {
		return dividerLine;
	}
	
	/** 
	 * @return Returns a reference to the text view displayed at the top of the layout
	 */
	public TextView getTopLabel() {
		return topLabel;
	}
	
	/** 
	 * @return Returns a reference to the text view displayed in the middle of the layout
	 */
	public TextView getMiddleLabel() {
		return middleLabel;
	}
	
	/**
	 * 
	 * @return Returns the text both the editable and textview fields have been set to.
	 */
	public String getText() {
		return middleLabel.getText().toString();
	}
	
	/**
	 * @return the view
	 */
	public View getView() {
		return view;
	}
	
	/**
	 * @return the caret
	 */
	public View getCaret() {
		return caret;
	}

	/**
	 * Method used to set the text for the middle label and editable field at once.
	 * 
	 * @param text Reference to string to use to set the editable field and middle label
	 */
	public void setText(final String text) {
		middleLabel.setText(text);
	}

	/**
	 * @return the errorLabel
	 */
	public TextView getErrorLabel() {
		return errorLabel;
	}
}
