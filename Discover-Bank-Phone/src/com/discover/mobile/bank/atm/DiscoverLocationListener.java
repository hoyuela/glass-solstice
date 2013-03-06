/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.atm;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Location listener to be used by a location fragment
 * when trying to receive the users current location.
 * @author jthornton
 *
 */
public class DiscoverLocationListener implements LocationListener{

	/**Fragment to return the location to*/
	final LocationFragment fragment;

	/**
	 * Constructor for the listener
	 * @param fragment - fragment to return the location to.
	 */
	public DiscoverLocationListener(final LocationFragment fragment){
		this.fragment = fragment;
	}

	@Override
	public void onLocationChanged(final Location location) {
		fragment.setUserLocation(location);
	}

	@Override
	public void onProviderDisabled(final String provider) { }

	@Override
	public void onProviderEnabled(final String provider) { }

	@Override
	public void onStatusChanged(final String provider, final int status, final Bundle extras) { }

}
