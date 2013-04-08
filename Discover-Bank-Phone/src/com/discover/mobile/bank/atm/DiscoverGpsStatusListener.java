/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.atm;

import android.location.GpsStatus;
import android.location.GpsStatus.Listener;

/**
 * Lister for the GPS status. This will help control the timeout
 * of getting the location of the user. 
 * @author jthornton
 *
 */
public class DiscoverGpsStatusListener implements Listener{

	/**Current time at the start of the listener*/
	private long time;

	/**Time that the app will search for a location*/
	private static final long STAY_ALIVE_TIME = 25000; //10 seconds

	/**Fragment that is waiting for the received locaiton*/
	private final LocationFragment fragment;

	/**
	 * Constructor for the class
	 * @param fragment - fragment that is waiting for the location
	 */
	public DiscoverGpsStatusListener(final LocationFragment fragment){
		this.fragment = fragment;
	}

	/**
	 * Called when anything about the gps status has changed.
	 */
	@Override
	public void onGpsStatusChanged(final int event) {
		if(event == GpsStatus.GPS_EVENT_STARTED){
			startTimer();
		}else{
			maybeHandleTimeOut();
		}
	}

	/**
	 * Start the timer so that the app will know when getting the location
	 * takes to much time
	 */
	private void startTimer(){
		time = System.currentTimeMillis();
	}

	/**
	 * Maybe handle the timeout. If the time the listener has been active 
	 * is over the allotted amount of time it will let the fragment know to
	 * stop looking.
	 */
	private void maybeHandleTimeOut(){
		final long endTime = time + STAY_ALIVE_TIME;
		if(endTime <= System.currentTimeMillis()){
			fragment.handleTimeOut();
		}
	}
}
