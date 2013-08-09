package com.discover.mobile.bank.ui.widgets;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;

import com.discover.mobile.common.utils.StringUtility;

public final class BankUrlChangerPreferences {

	private static final String FILE_NAME = "bank_saved_urls";

	private static final String VALUE = "value";

	private static final String NAME_KEY = "name";

	private static final String URL_KEY = "url";

	private static List<BankUrlSite> allSites;

	public static List<BankUrlSite> getSites(){

		//TODO:  Add the saved ones
		return getDefaultSites();
	}

	public static void addSite(final Context context, final BankUrlSite newSite){
		if(null == allSites){
			getSites();
		}
		newSite.urlNumber = allSites.size();
		saveSite(context, newSite);
	}

	public static void deleteSite(final Context context, final BankUrlSite site){
		final SharedPreferences prefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		prefs.edit().remove(VALUE + StringUtility.PERIOD + NAME_KEY + StringUtility.PERIOD + site.urlNumber).commit();
		prefs.edit().remove(VALUE + StringUtility.PERIOD + URL_KEY + StringUtility.PERIOD + site.urlNumber).commit();	
	}

	public static void editSite(final Context context, final BankUrlSite site){
		saveSite(context, site);
	}

	private static void saveSite(final Context context, final BankUrlSite site){
		final SharedPreferences prefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		prefs.edit().putString(VALUE + StringUtility.PERIOD + NAME_KEY + StringUtility.PERIOD + site.urlNumber, site.title).commit();
		prefs.edit().putString(VALUE + StringUtility.PERIOD + URL_KEY + StringUtility.PERIOD + site.urlNumber, site.link).commit();
	}

	private static final List<BankUrlSite> getDefaultSites(){
		final List<BankUrlSite> defaults = new ArrayList<BankUrlSite>();
		defaults.add(new BankUrlSite("https://www.discoverbank.com", "Prod", 0));
		defaults.add(new BankUrlSite("https://asys.discoverbank.com", "Asys", 0));
		defaults.add(new BankUrlSite("https://mst0.discoverbank.com", "Mst0", 0));
		defaults.add(new BankUrlSite("https://qsys.discoverbank.com", "Qsys", 0));
		//TODO:  Change these
		defaults.add(new BankUrlSite("https://www.discoverbank.com1", "Charles", 0));
		defaults.add(new BankUrlSite("https://www.discoverbank.com2", "Mock", 0));

		return defaults;
	}
}
