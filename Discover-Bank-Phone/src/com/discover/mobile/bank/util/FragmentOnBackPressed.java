package com.discover.mobile.bank.util;

/**
 * Implement this interface if a Fragment needs to support overriding the hardware back button.
 * 
 * @author scottseward
 *
 */
public interface FragmentOnBackPressed {

	/**
	 * The onBackPressed method that an Activity normally calls.
	 */
	void onBackPressed();
	
	/**
	 * Interface used for disabling back press from a fragment
	 */
	boolean isBackPressDisabled();
}
