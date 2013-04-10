package com.discover.mobile.bank.payees;

/**
 * Class used to create objects that represent U.S. states with it's abbreviation.
 * 
 * @author henryoyuela
 *
 */
public class State {
	public static State[] STATES = new State[] {
		new State("Alabama", "AL"),
		new State("Alaska", "AK"),
		new State("Alabama", "AL"),
		new State("Alaska", "AK"),
		new State("Arizona", "AZ"),
		new State("Arkansas	State", "AR"),
		new State("California", "CA"),
		new State("Colorado", "CO"),
		new State("Connecticut", "CT"),
		new State("Delaware", "DE"),
		new State("District of Columbia", "DC"),
		new State("Florida", "FL"),
		new State("Georgia", "GA"),
		new State("Hawaii", "HI"),
		new State("Idaho", "ID"),
		new State("Illinois", "IL"),
		new State("Indiana", "IN"),
		new State("Iowa", "IA"),
		new State("Kansas", "KS"),
		new State("Kentucky", "KY"),
		new State("Louisiana", "LA"),
		new State("Maine", "ME"),
		new State("Maryland", "MD"),
		new State("Massachusetts", "MA"),
		new State("Michigan", "MI"),
		new State("Minnesota", "MN"),
		new State("Mississippi", "MS"),
		new State("Missouri", "MO"),
		new State("Montana", "MT"),	
		new State("Nebraska", "NE"),
		new State("Nevada", "NV"),
		new State("New Hampshire", "NH"),	
		new State("New Jersey", "NJ"),	
		new State("New Mexico", "NM"),
		new State("New York", "NY"),
		new State("North Carolina", "NC"),
		new State("North Dakota", "ND"),
		new State("Ohio	State", "OH"),
		new State("Oklahoma", "OK"),
		new State("Oregon", "OR"),
		new State("Pennsylvania", "PA"),
		new State("Rhode Island", "RI"),
		new State("South Carolina", "SC"),
		new State("South Dakota", "SD"),
		new State("Tennessee", "TN"),
		new State("Texas", "TX"),
		new State("Utah	State", "UT"),
		new State("Vermont", "VT"),	
		new State("Virginia", "VA"),
		new State("Washington", "WA"),
		new State("West Virginia", "WV"),
		new State("Wisconsin", "WI"),
		new State("Wyoming", "WY")
	};

	
	public String name = "";
	public String abbrev = "";

	/***
	 * Constructo used to populate name and abbreviation at instantiation.
	 * 
	 * @param _name Name of the state represented by the instance of this class.
	 * @param _abbrev Abbreviation of the state represent by the instance of this class.
	 */
	private State( final String _name, final String _abbrev )
	{
	    name = _name;
	    abbrev = _abbrev;
	}

	/**
	 * This method is used by the StateArrayAdapter to populate the TextView shown
	 * in the spinner.
	 */
	@Override
	public String toString()
	{
	    return( name + " (" + abbrev + ")" );
	}
}
