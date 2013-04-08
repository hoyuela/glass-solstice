package com.discover.mobile.bank.payees;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.discover.mobile.bank.R;

/**
 * 
 * @author henryoyuela
 *
 */
public class BankStateDetail extends BankEditDetail implements OnItemSelectedListener {

	private Spinner stateSpinner;
	
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
		if( stateSpinner != null ) {
			stateSpinner.performClick();
		}
	}
	
	@Override
	public void onFocusChange(final View arg0, final boolean arg1) {
		//Nothing to do
	}
	
	private void initializeSpinner() {
		if( middleLabel != null  ) {
			final Spinner stateSpinner = new Spinner(getContext());
			final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.BELOW, getMiddleLabel().getId());
			stateSpinner.setLayoutParams(params);
			
			final ArrayAdapter<State> spinnerArrayAdapter = new ArrayAdapter<State>( getContext(), 
																					 R.layout.push_simple_spinner_view, 
																					 State.STATES);
			
			stateSpinner.setAdapter(spinnerArrayAdapter);
			
			this.addView(stateSpinner);		
		}
	}

	@Override
	public void onItemSelected(final AdapterView<?> arg0, final View arg1, final int arg2,
			final long arg3) {
	
		final State state = (State)stateSpinner.getSelectedItem();
		getMiddleLabel().setText(state.abbrev);		
	}

	@Override
	public void onNothingSelected(final AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

}

