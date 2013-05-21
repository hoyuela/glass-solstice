package com.discover.mobile.common;

/**
 * Class used to fetch values for configurable environment variables used
 * through out the application. The values returned will depend on the
 * environment the application is on.
 * 
 * @author henryoyuela
 * 
 */
final public class DiscoverEnvironment {

	private DiscoverEnvironment() {
		throw new UnsupportedOperationException();
	}

	public static String getCardBaseUrl() {
		return DiscoverActivityManager.getActiveActivity().getString(R.string.card_base_url);
	}

	public static String getBankBaseUrl() {
		return DiscoverActivityManager.getActiveActivity().getString(R.string.bank_base_url);
	}

	public static String getBankStrippedUrl() {
		return DiscoverActivityManager.getActiveActivity().getString(R.string.bank_stripped_url);
	}

	public static String getPushKey() {
		return DiscoverActivityManager.getActiveActivity().getString(R.string.push_key);
	}

	public static String getPushID() {
		return DiscoverActivityManager.getActiveActivity().getString(R.string.push_id);
	}

	public static String getCardTrackingHelper() {
		return DiscoverActivityManager.getActiveActivity().getString(R.string.card_tracking_rsid);
	}

	public static String getBankTrackingHelper() {
		return DiscoverActivityManager.getActiveActivity().getString(R.string.bank_tracking_rsid);
	}

}
