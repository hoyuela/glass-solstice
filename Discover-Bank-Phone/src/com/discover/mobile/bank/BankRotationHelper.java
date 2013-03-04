package com.discover.mobile.bank;

import android.os.Bundle;

/**
 * Rotation helper used for saving bundles.  This is used because in some fragments the on create view can be called 
 * more than once.  Note this is a work around until the underlying issues is uncovwred.  
 * @author jthornton
 *
 */
public class BankRotationHelper {

	/**Instance of this class*/
	private static BankRotationHelper helper;

	/**Bundle holding the state*/
	private Bundle bundle;

	/**
	 * Default constructor
	 */
	private BankRotationHelper(){

	}

	/**
	 * Get the helper
	 * @return the helper
	 */
	public static BankRotationHelper getHelper(){
		if(null == helper){
			helper = new BankRotationHelper();
		}
		return helper;
	}

	/**
	 * @return the bundle
	 */
	public Bundle getBundle() {
		return bundle;
	}

	/**
	 * @param bundle the bundle to set
	 */
	public void setBundle(final Bundle bundle) {
		this.bundle = bundle;
	}
}
