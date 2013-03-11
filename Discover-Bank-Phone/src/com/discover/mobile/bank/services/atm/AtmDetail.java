/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.services.atm;

import java.io.Serializable;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.atm.LocationObject;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Main detail object for the get ATM service call.  This class is used
 * to map the response to the results so that the results can be displayed
 * on the map.
 * 
 * API: /api/atmLocator/SearchGeocodedLocation.xml
 * 
 * Sample Response: 
 * 
 * { "atmIdentifier": 1144113, "address1": "2426 N RACINE", "address2":
 * undefined, "address3": undefined, "cityName": "Chicago", "postalCode": 60614,
 * "locationName": "GAS LIGHT", "atmHrAvailDesc": "Unknown", "latitude":
 * 41.926176, "longitude": "-87.658589", "restricted": "U", "braille": "U",
 * "driveUp": "U", "walkUp": "U", "surchargeFree": "N", "stateName": "IL" }
 * 
 * 
 * @author jthornton
 * 
 */
public class AtmDetail extends LocationObject implements Serializable{

	/**Static string to show that the atm is surcharge free*/
	public static String SURCHARGE_FREE = "Y";

	/**Unique serialized object id*/
	private static final long serialVersionUID = -3207359834100328198L;

	/**ATM unique identifier*/
	@JsonProperty("atmIdentifier")
	public int id;

	/**Address of the ATM*/
	@JsonProperty("address1")
	public String address1;

	/**Address of the ATM*/
	@JsonProperty("address2")
	public String address2 = "";

	/**Address of the ATM*/
	@JsonProperty("address3")
	public String address3 = "";

	/**City of the ATM*/
	@JsonProperty("cityName")
	public String city;

	/**Postal code of the at,*/
	@JsonProperty("postalCode")
	public int postalCode;

	/**Location name of the atm*/
	@JsonProperty("locationName")
	public String locationName;

	/**Hours that the atm is available*/
	@JsonProperty("atmHrAvailDesc")
	public String atmHrs;

	/**Latitude of the atm*/
	@JsonProperty("latitude")
	public String latitude;

	/**Longitude of the atm*/
	@JsonProperty("longitude")
	public String longitude;

	/**atm restricted*/
	@JsonProperty("restricted")
	public String restricted;

	/**If the atm has braille*/
	@JsonProperty("braille")
	public String braille;

	/**If the atm has a drive up*/
	@JsonProperty("driveUp")
	public String driveUp;

	/**If the atm has a walk up*/
	@JsonProperty("walkUp")
	public String walkUp;

	/**If the atm is surcharge free*/
	@JsonProperty("surchargeFree")
	public String surchargeFee;

	/**State the atm is in*/
	@JsonProperty("stateName")
	public String state;

	/**Distance from the user*/
	public double distanceFromUser;

	/**
	 * @return if the atm is surcharge free
	 */
	public boolean isAtmSearchargeFree(){
		return SURCHARGE_FREE.equals(surchargeFee);
	}

	@Override
	public double getLongitude() {
		return Double.parseDouble(longitude);
	}

	@Override
	public double getLatitude() {
		return Double.parseDouble(latitude);
	}

	@Override
	public int getPinDrawable() {
		return (isAtmSearchargeFree()) ? R.drawable.atm_orange_pin : R.drawable.atm_gray_pin;	
	}

	@Override
	public void setDistanceFromUser(final double distance) {
		distanceFromUser = distance;
	}
}
