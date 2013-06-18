/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.atm;

import com.twotoasters.clusterkraf.InputPoint;

/**
 * Object the classes should extend to use the map wrapper
 * @author jthornton
 *
 */
public abstract class LocationObject {

	/**Get the longitude of the object*/
	public abstract double getLongitude();

	/**Get the latitude of the object*/
	public abstract double getLatitude();

	/**Get the drawable that should be put on the map*/
	public abstract int getPinDrawable();

	/**Set the distance away from the user*/
	public abstract void setDistanceFromUser(final double distance);
}
