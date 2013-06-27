package com.discover.mobile.bank.ui.modals;

import com.discover.mobile.bank.R;
import com.discover.mobile.common.utils.StringUtility;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * This class displays a small "progress dialog" showing the user that they are currently searching for ATMs.  
 * 
 * @author Stephen Farr
 */

public class AtmSearchingForAtmsModal extends AlertDialog {

	/** Length of time before adding a new period at the end of the sentence. */
	private final int PERIOD_ITERATION = 500;
	
	/** Maximum number of periods allowed */
	private final int MAX_PERIOD_COUNT = 3;
	
	/** Minimum number of periods allowed */
	private final int MINIMUM_PERIOD_COUNT = 0;
	
	private TextView content;
	private final Context context;
	private int numberOfPeriods = 0;
	
	//------------------------------ Constructors ------------------------------
	public AtmSearchingForAtmsModal(Context context, int theme) {
		super(context, theme);
		
		this.context = context;
	}

	public AtmSearchingForAtmsModal(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		
		this.context = context;
	}

	public AtmSearchingForAtmsModal(Context context) {
		super(context);
		
		this.context = context;
	}
	
	//------------------------------ Android Lifecycle Methods ------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initializeModal();
	}

	//------------------------------ Private Helper Methods ------------------------------
	
	/**
	 * Initializes the modal layout and flags.
	 */
	private void initializeModal() {
		//Initializes the layout
		this.setContentView(getLayoutInflater().inflate(R.layout.atm_loading_atms_modal, null));
	
		//Creates the looping "text animation" for the "progress dialog"
		content = (TextView) findViewById(R.id.loading_atms_modal_content);
		
		this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		
		continueAddingPeriods();
	}
	
	/**
	 * Cyclically adds periods to the end of the progress dialog text to show work is being done.
	 */
	private void continueAddingPeriods() {
		new Handler().postDelayed(rotateCanvasRunnable, PERIOD_ITERATION);
	}
	
	/**
	 * Runnable to perform the period changes
	 */
    private final Runnable rotateCanvasRunnable = new Runnable() {
    	
    	/**
    	 * Returns the progress dialog method after concatenating the main string with the calculated number of periods
    	 * 
    	 * @param numberOfPeriods - Number of periods to display
    	 * @return progress dialog message
    	 */
    	private String getProgressMessage(final int numberOfPeriods) {
    		return context.getString(R.string.atm_searching_for_atms) + this.getPeriods(numberOfPeriods);
    	} 
    	
    	/**
    	 * Returns a string containing periods equivalent to the number of periods passed in.
    	 * 
    	 * @param numberOfPeriods - Number of periods to display
    	 * @return string containing periods equivalent to number passed in.
    	 */
    	private String getPeriods(final int numberOfPeriods) {
    		String periods = StringUtility.EMPTY;
    		for (int i = 0; i < numberOfPeriods; i++) {
    			periods = periods + StringUtility.PERIOD;
    		}
    		
    		return periods;
    	}
    	
        @Override
        public void run() {
        	content.setText(this.getProgressMessage(numberOfPeriods));
        	
        	numberOfPeriods = (numberOfPeriods < MAX_PERIOD_COUNT) ? ++numberOfPeriods : MINIMUM_PERIOD_COUNT;
        	
        	continueAddingPeriods();
        }
    };
}