package com.discover.mobile.bank.util;

import android.os.Handler;
import android.util.Log;
import android.view.View;

/**
 * Utility class used to change a View's visibility from Visible to Gone with a delay.
 * 
 * @author henryoyuela
 *
 */
public class TimedViewController implements Runnable {
	/**
	 * Used to print logs into Android's logcat tool
	 */
	private static final String TAG = TimedViewController.class.getSimpleName();
	/**
	 * Reference to Handler used to schedule a timer to change the view's visibility
	 */
	private final Handler timerFireHandler;
	/**
	 * Reference to the View whose visibility will be changed from Visibile to Gone
	 */
	private final View view;
	/**
	 * Holds the amount of time in milliseconds to wait before a View's visibility is changed to Gone
	 */
	private final int delay;
	
	/**
	 * This constructor is never used
	 */
	@SuppressWarnings("unused")
	private TimedViewController() {
		throw new UnsupportedOperationException("This constructor cannot be used");
	}
	
	/**
	 * 
	 * @param view Reference to View whose visibility will be altered
	 * @param delay Amount of time in milliseconds to wait before altering the View's 
	 *              visibility from Visible to Gone.
	 */
	public TimedViewController(final View view, final int delay) {
		this.view = view;
		this.delay = delay;
		timerFireHandler = new Handler();
	}

	/**
	 * Sets the visibility of the View, passed in the constructor, to Visible and
	 * starts a timer using the delay specified in the constructor to set it to 
	 * Gone after the timer has expired.
	 */
	public void start() {
		this.view.setVisibility(View.VISIBLE);
		timerFireHandler.postDelayed(this, delay);	
	}
	
	/**
	 * Method called from timer scheduled using start() after it has expired.
	 */
	@Override
	public void run() {
		try {
			/**Hide view after time has elapsed*/
			this.view.setVisibility(View.GONE);
		}catch(final Exception ex) {
			if( Log.isLoggable(TAG, Log.ERROR)) {
				Log.e(TAG, "Unable to hide view");
			}
 		}
		
	}
}
