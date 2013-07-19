package com.discover.mobile.bank.util;

/*
 * This interface can be used to inject a preprocess. 
 * This preprocess will happen before the banknavigation root 
 * activity make another fragment visbile.  The processing that
 * should take place is left up to the fragment. 
 */
public interface OnPreProcessListener {

	/*
	 * Runnable contains reference to what should be executed after
	 * the preprocessing happens.
	 */
	public void preProcess(final Runnable r);
}
