/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.atm;

import android.content.Context;
import android.location.LocationManager;

import com.discover.mobile.common.BaseFragment;

/**
 * Location manager  wrapper, has utility methods for getting the users location
 * @author jthornton
 *
 */
public class DiscoverLocationMangerWrapper {

	/**GPS status listener to attach to the location manager when trying to the users current location*/
	private final DiscoverGpsStatusListener gpsStatusListener;

	/**Location listener to attach to the location manager when trying to get the users current location*/
	private final DiscoverLocationListener gpsListener, networkListener;

	/** Location manager of the application*/
	private final LocationManager manager;

	/**
	 * 
	 * @param fragment - fragment that needs the users locaiton
	 */
	public DiscoverLocationMangerWrapper(final LocationFragment fragment){
		manager = (LocationManager) ((BaseFragment)fragment).getActivity().getSystemService(Context.LOCATION_SERVICE);
		gpsStatusListener = new DiscoverGpsStatusListener(fragment);
		gpsListener = new DiscoverLocationListener(fragment);
		networkListener = new DiscoverLocationListener(fragment);
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
	}

	/**
	 * Get the users current location
	 */
	public void getLocation(){
		manager.addGpsStatusListener(gpsStatusListener);
		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsListener);
		manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, networkListener);
	}
}
