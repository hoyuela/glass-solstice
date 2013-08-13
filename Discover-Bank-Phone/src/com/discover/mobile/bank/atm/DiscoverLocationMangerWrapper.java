/*
 * � Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.atm;

import android.content.Context;
import android.location.LocationManager;
import android.os.Handler;

import com.discover.mobile.common.BaseFragment;

/**
 * Location manager  wrapper, has utility methods for getting the users location
 * @author jthornton
 *
 */
public class DiscoverLocationMangerWrapper {
	
	/** Time in milliseconds in which the location search should be considered timed-out. */
	private static final long TIMEOUT = 120000L;

	/**GPS status listener to attach to the location manager when trying to the users current location*/
	private final DiscoverGpsStatusListener gpsStatusListener;

	/**Location listener to attach to the location manager when trying to get the users current location*/
	private final DiscoverLocationListener gpsListener, networkListener;

	/** Location manager of the application*/
	private final LocationManager manager;
	
	private final Runnable onTimeOut;
	
	private final Handler timeOutHandler = new Handler();
	
	/**
	 * @param fragment - fragment that needs the users location
	 * @param onTimeOut - Runnable that will always run once the timeout time is reached.
	 */
	public DiscoverLocationMangerWrapper(final LocationFragment fragment, final Runnable onTimeOut){
		manager = (LocationManager) ((BaseFragment)fragment).getActivity().getSystemService(Context.LOCATION_SERVICE);
		gpsStatusListener = new DiscoverGpsStatusListener(fragment);
		gpsListener = new DiscoverLocationListener(fragment);
		networkListener = new DiscoverLocationListener(fragment);
		this.onTimeOut = onTimeOut;
	}

	/**
	 * Checks to see if the providers for location services are enabled
	 * @return if the providers for location services are enabled
	 */
	public boolean areProvidersenabled(){
		return manager.isProviderEnabled(LocationManager.GPS_PROVIDER) 
				&& manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}

	/**
	 * Sitop getting the users current location
	 */
	public void stopGettingLocaiton(){
		manager.removeGpsStatusListener(gpsStatusListener);
		manager.removeUpdates(gpsListener);
		manager.removeUpdates(networkListener);
		// Ensure any previous posts of the timeout Runnable are removed
		timeOutHandler.removeCallbacks(onTimeOut);
	}

	/**
	 * Get the users current location
	 */
	public void getLocation(){
		manager.addGpsStatusListener(gpsStatusListener);
		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsListener);
		manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, networkListener);
		startTimeOutRunnable();
	}
	
	/** Starts the runnable that will be called once the timeout time has been reached.
	 *  The Runnable will be run regardless of whether or not a location is found. */
	private void startTimeOutRunnable() {
		// Calls provided Runnable after time out has been reached.
		timeOutHandler.removeCallbacks(onTimeOut); // Ensure any previous posts of the Runnable are removed
		timeOutHandler.postDelayed(onTimeOut, TIMEOUT);
	}
}
