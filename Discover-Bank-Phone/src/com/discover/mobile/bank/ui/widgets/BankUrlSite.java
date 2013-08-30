package com.discover.mobile.bank.ui.widgets;

/**
 * Site object that is used wit the url changer.
 * @author jthornton
 *
 */
public class BankUrlSite {

	/**Keys used for passing information*/
	public static final String TITLE = "title";
	public static final String LINK = "url";
	public static final String URL_NUMBER = "number";

	/**Default value assigned to a new site*/
	public static final int NEW_SITE = -1;

	public String link;
	public String title;
	public int urlNumber;
	public boolean selected;
	public boolean canBeEdited;

	/**
	 * Constructor for the object
	 * @param link - url that will be hit
	 * @param title - title of the url
	 * @param urlNumber - url number
	 * @param canBeEdited - boolean set to true if it cannot be edited
	 */
	public BankUrlSite(final String link, final String title, final int urlNumber, final boolean canBeEdited) {
		this.link = link;
		this.title = title;
		this.urlNumber = urlNumber;
		selected = false;
		this.canBeEdited = canBeEdited;
	}

	/**
	 * Constructor for the object
	 * @param link - url that will be hit
	 * @param title - title of the url
	 * @param canBeEdited - boolean set to true if it cannot be edited
	 */
	public BankUrlSite(final String link, final String title, final boolean canBeEdited) {
		this.link = link;
		this.title = title;
		selected = false;
		urlNumber = NEW_SITE;
		this.canBeEdited = canBeEdited;
	}

	/**
	 * Check to see if this site is equal to another site
	 * @param site - site in question 
	 * @return return true if these values are equal.
	 */
	public boolean isEqualTo(final BankUrlSite site){
		return link.equals(site.link) && title.equals(site.title) && urlNumber == site.urlNumber;
	}
}
