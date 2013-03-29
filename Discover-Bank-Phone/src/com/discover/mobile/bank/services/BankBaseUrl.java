/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.services;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This class retrieves the value from the bankurl properties file. 
 * 
 * @author ajleeds
 *
 */
public class BankBaseUrl {
	private static final String BUNDLE_NAME = "com.discover.mobile.bank.bankurl"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private BankBaseUrl() {
	}

	public static String getString(final String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (final MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
