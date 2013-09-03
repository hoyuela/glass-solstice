package com.discover.mobile.bank.ui.widgets;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;

import com.discover.mobile.common.utils.StringUtility;

/**
 * Shared preferences wrapper used by the url changer
 * @author jthornton
 *
 */
public final class BankUrlChangerPreferences {

	/**Shared preferences keys*/
	private static final String FILE_NAME = "bank_saved_urls";
	private static final String VALUE = "value";
	private static final String NAME_KEY = "name";
	private static final String URL_KEY = "url";

	/**List of all the sites*/
	private static List<BankUrlSite> allSites;

	/**
	 * Private constructor
	 */
	private BankUrlChangerPreferences(){
		//Left blank intentionally
	}

	/**
	 * Get a list of all the sites
	 * @param context - context requesting the list
	 * @return the list of all sites
	 */
	public static List<BankUrlSite> getSites(final Context context){
		if(null == allSites){
			allSites = new ArrayList<BankUrlSite>();
			allSites.addAll(getDefaultSites());
			allSites.addAll(getSavedSites(context));
		}
		return allSites;
	}

	/**
	 * Add a site to the list
	 * @param context - context requesting the add
	 * @param newSite - site to be added
	 */
	public static void addSite(final Context context, final BankUrlSite newSite){
		if(null == allSites){
			allSites = getSites(context);
		}
		newSite.urlNumber = saveSite(context, newSite);
		allSites.add(newSite);
	}

	/**
	 * Delete a site
	 * @param context - context requesting a delete
	 * @param site - the site to be deleted
	 */
	public static void deleteSite(final Context context, final BankUrlSite site){
		final SharedPreferences prefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		final int numSites = prefs.getInt(VALUE, 0);
		if(numSites > 0){
			allSites.remove(site);
			prefs.edit().putInt(VALUE, numSites - 1).commit();
			prefs.edit().remove(site.urlNumber + StringUtility.PERIOD + NAME_KEY + StringUtility.PERIOD).commit();
			prefs.edit().remove(site.urlNumber  + StringUtility.PERIOD + URL_KEY + StringUtility.PERIOD).commit();	
		}
	}

	/**
	 * Edit a site
	 * @param context - context requesting the edit
	 * @param site - new site information
	 * @param oldSite - old site information
	 */
	public static void editSite(final Context context, final BankUrlSite site, final BankUrlSite oldSite){
		saveSite(context, site);
		for(int i = 0; i < allSites.size(); i++){
			if(oldSite.isEqualTo(allSites.get(i))){
				final BankUrlSite temp = allSites.get(i);
				temp.title = site.title;
				temp.link = site.link;
				break;
			}
		}
	}

	/**
	 * Save a site
	 * @param context - context requesting the save
	 * @param site - site to save
	 * @return the url number of the site
	 */
	private static int saveSite(final Context context, final BankUrlSite site){
		final SharedPreferences prefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		int siteNumber;
		if(BankUrlSite.NEW_SITE == site.urlNumber){
			siteNumber = prefs.getInt(VALUE, 0) + 1;
			site.urlNumber = siteNumber;
		}else{
			siteNumber = site.urlNumber;
		}
		prefs.edit().putInt(VALUE, siteNumber + 1).commit();
		prefs.edit().putString(siteNumber + StringUtility.PERIOD + NAME_KEY + StringUtility.PERIOD, site.title).commit();
		prefs.edit().putString(siteNumber + StringUtility.PERIOD + URL_KEY + StringUtility.PERIOD, site.link).commit();

		return siteNumber;
	}

	/**
	 * Get a list of saved sites in shared preferences
	 * @param context - context requesting the list
	 * @return the list of saved sites
	 */
	private static List<BankUrlSite> getSavedSites(final Context context){
		final List<BankUrlSite> sites = new ArrayList<BankUrlSite>();
		String name = "";
		String link = "";

		final SharedPreferences prefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		final int numSites = prefs.getInt(VALUE, 0);
		if(numSites > 0){
			for(int i = 0; i < numSites; i++){
				name = prefs.getString(i + StringUtility.PERIOD + NAME_KEY + StringUtility.PERIOD, StringUtility.EMPTY);
				link = prefs.getString(i + StringUtility.PERIOD + URL_KEY + StringUtility.PERIOD, StringUtility.EMPTY);
				sites.add(new BankUrlSite(link, name, i, true));
			}
		}

		return sites;
	}

	/**
	 * Get the default list of sites
	 * @return the default list of sites
	 */
	private static List<BankUrlSite> getDefaultSites(){
		final List<BankUrlSite> defaults = new ArrayList<BankUrlSite>();
		//Un editable fields
		defaults.add(new BankUrlSite("https://www.discoverbank.com", "Prod", 0, false));
		defaults.add(new BankUrlSite("https://asys.discoverbank.com", "Asys", 0, false));
		defaults.add(new BankUrlSite("https://mst0.discoverbank.com", "Mst0", 0, false));
		defaults.add(new BankUrlSite("https://qsys.discoverbank.com", "Qsys", 0, false));
		return defaults;
	}
}
