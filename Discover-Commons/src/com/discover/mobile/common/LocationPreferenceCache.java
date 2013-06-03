package com.discover.mobile.common;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;

/**
 * This class is responsible for keeping tack of the "Use My Location" modal selection.
 * 
 * For a logged in user it will remember their choice on the modal if they chose to use their current location. This
 * means that the modal will not appear again until they log in with a new user, or the app is terminated on their device.
 * 
 * For logged out states this class keeps track of the location choice as well. If a user navigates to the ATM locator
 * while logged out, and chooses to use the current location, they will not see the location modal again, until they
 * terminate the app and restart it.
 * 
 * @author scottseward
 *
 */
public final class LocationPreferenceCache {
	private final List<String> usersWhoWantToUseTheirLocation = new ArrayList<String>();
	private String mostRecentUser = null;
	private boolean nonAuthenticatedLocation = false;
	
	/**
	 * Sets the most recent user. If a new user signs into the app, the location modal choice is reset.
	 * Be sure that any username that is stored to this cache is encrypted!
	 * 
	 * @param currentUser the last user to log into the app.
	 */
	public void setMostRecentUser(final String currentUser) {
		mostRecentUser = currentUser;
	}
	
	/**
	 * Save the user's location modal choice. If they are logged in, the user is added to a list of
	 * users who have accepted the modal. 
	 * If logged out a boolean flag is set.
	 */
	public void setUserAcceptedModal() {
		if(Globals.isLoggedIn()) {
			addCurrentUserToAcceptedList();
		}else {
			nonAuthenticatedLocation = true;
		}
	}
	
	/**
	 * Adds the mostRecentUser to the list of users who have accepted the location modal.
	 */
	private void addCurrentUserToAcceptedList() {
		if(!Strings.isNullOrEmpty(mostRecentUser)) {
			usersWhoWantToUseTheirLocation.add(mostRecentUser);
		}
	}
	
	/**
	 * 
	 * @return based on a user being logged in or out if the modal should be shown
	 */
	public boolean shouldShowModal() {
		boolean shouldShowModal = true;
		
		if(Globals.isLoggedIn() && !Strings.isNullOrEmpty(mostRecentUser)) {
			shouldShowModal = !usersWhoWantToUseTheirLocation.contains(mostRecentUser);
		}else {
			shouldShowModal = !nonAuthenticatedLocation;
		}
		
		return shouldShowModal;
	}
}
