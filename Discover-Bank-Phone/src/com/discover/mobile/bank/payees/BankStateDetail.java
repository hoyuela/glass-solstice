package com.discover.mobile.bank.payees;

import android.content.Context;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RelativeLayout;
import android.widget.Spinner;

/**
 * Class derived from BankEditDetail used for displaying a Spinner with a list of abbreviations for
 * each U.S. state that a user can select from.
 * 
 * @author henryoyuela
 *
 */
public class BankStateDetail extends BankEditDetail implements OnItemSelectedListener{
	/**
	 * Spinner that displays a list of abbreviations for each U.S. States
	 */
	private Spinner stateSpinner;
	/**
	 * Flag used to determine whether the spinner was shown because the user clicked on the layout of this control.
	 */
	private boolean stateSpinnerClicked = false;;
	
	public BankStateDetail(final Context context) {
		super(context);
	}
	
	public BankStateDetail(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}
	public BankStateDetail(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void doSetup(final Context context) {
		super.doSetup(context);
		
		initializeSpinner();
		
	}
	
	@Override
	public void onClick(final View sender) {
		showSpinner();
	}
	
	@Override
	public void onFocusChange(final View arg0, final boolean arg1) {
		//Nothing to do
	}
	

	private void initializeSpinner() {
		stateSpinner = new Spinner(getContext());
		
		if( middleLabel != null && stateSpinner != null ) {
			/**Create Adapter that will state the list of U.S. State abbreviations*/
			final StateArrayAdapter adapter = new StateArrayAdapter(getContext());
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
			
			stateSpinner.setAdapter(adapter);			
			
			/**Add Spinner Control to the layout of this object*/
			final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 
																					   RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.ALIGN_TOP, middleLabel.getId());
			params.addRule(RelativeLayout.ALIGN_LEFT, middleLabel.getId());
			stateSpinner.setLayoutParams(params);
			stateSpinner.setVisibility(View.INVISIBLE);		
			((ViewGroup) view).addView(stateSpinner);
			
			/**Set Event Handlers*/
			stateSpinner.setOnItemSelectedListener(this);
			
			/**Set minimum and maximum character limits*/
			getEditableField().setMinimum(2);
			final InputFilter[] inputFilters = { new InputFilter.LengthFilter(2) };
			getEditableField().setFilters(inputFilters);
		}
	}
	
	private void showSpinner() {
		if( stateSpinner != null) {
			stateSpinner.performClick();
		
			stateSpinnerClicked = true;
		}
	}
	

	@Override
	public void setEditMode( final boolean value ) {
		super.setEditMode(false);
		
		if( value ) {
			showSpinner();
		} 
	}
	
	@Override
	public void setEditModeNoFocus( final boolean value ) {
		super.setEditModeNoFocus(false);
		
		
	}

	@Override
	public void onItemSelected(final AdapterView<?> arg0, final View arg1, final int arg2,
			final long arg3) {
		
		/**
		 * Set the text of the middle label in this control to the state selected by the user 
 	     * then move the focus to the next control.
 	     */
		if( stateSpinner != null && stateSpinnerClicked) {
			final State state = (State)stateSpinner.getSelectedItem();
			setText(state.abbrev);
			
			if( nextDetail != null ) {
				stateSpinnerClicked = false;
				nextDetail.setEditMode(true);
			}
		}
	}

	@Override
	public void onNothingSelected(final AdapterView<?> arg0) {
		//Nothing to do here
	}
}

