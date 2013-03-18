/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.atm;

import android.location.Location;
import android.os.Bundle;

/**
 * Interface for any fragment that will be getting location
 * information.
 * @author jthornton
 *
 */
public interface LocationFragment {

	/**State of the fragment meaning that location services are not enabled*/ 
	int NOT_ENABLED = 0;

	/**
	 * State of the fragment meaning that location services are enabled,
	 * but the app does not yet have a user confirmation of using
	 * their current location.
	 */ 
	int ENABLED = 1;

	/**
	 * State of the fragment meaning that location services are enabled and
	 * the app is currently searching for the location.
	 */ 
	int SEARCHING = 2;

	/**
	 * State of the fragment meaning that location services are enabled and
	 * the app has the users current location.
	 */ 
	int LOCKED_ON = 3;

	/**
	 * State of the fragment meaning that location of the user cannot or
	 * will not be enabled or retrieved.
	 */ 
	int NOT_USING_LOCATION = 4;

	/**Key to get the location status out of a bundle*/
	String LOCATION_STATUS = "status";

	/**Constant to save the users longitude location*/
	String LONG_KEY = "long";

	/**Constant to save the users latitude location*/
	String LAT_KEY = "lat";

	/**Zoom level that the app should zoom into when the users current location is found*/
	float MAP_CURRENT_GPS_ZOOM = 14f;

	/**Zoom level that the app should zoom into when the users current location is found*/
	float MAP_CURRENT_NETWORK_ZOOM = 14f;

	/**Latitude used to center the map over the United States*/
	Double MAP_CENTER_LAT = 37.88;

	/**Longitude used to center the map over the United States*/
	Double MAP_CENTER_LONG = -98.21;

	/**
	 * Set the users location on the map.
	 */
	void setUserLocation(final Location location);

	/**
	 * @return the locationStatus
	 */

	void getLocation();

	/**
	 * @param locationStatus the locationStatus to set
	 */
	void setLocationStatus(final int locationStatus);

	/**
	 * Launch the settings activity so that the user can turn the location services on.
	 */
	void launchSettings();

	/**
	 * Show that either the location could not be retrieved. 
	 */
	void showNoLocation();

	/**
	 * Handle the timeout of the listeners.  Meaning that the listeners
	 * were unable to get the location in a reasonable amount of time.
	 */
	void handleTimeOut();

	/**
	 * Set the location of the current user
	 * @param location - location of the current user
	 */
	void setLocation(Location location);

	/**
	 * Show the list fragment
	 */
	void showList();

	/**
	 * Show the map fragment
	 */
	void showMap();

	/**
	 * Show the street view fragment
	 * @param bundle - bundle to used to load the data
	 */
	void showStreetView(Bundle bundle);

}
