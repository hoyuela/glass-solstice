package com.discover.mobile.common.utils;

/**
 * Class used to store constants used throughout the app related to String building and comparison.
 * Helps to prevent repeated static instances of commonly used String values.
 * @author allie
 */
public final class StringUtility {

	public static final String EMPTY = "";
	public static final String SPACE = " ";
	public static final String ENCODED_SPACE = "%20";
	public static final String COMMA = ",";
	public static final String NEW_LINE = "\n";
	public static final String NON_NUMBER_CHARACTERS = "[^0-9]";
	public static final String DASH = "-";
	public static final String HASH = "#";
	public static final String QUESTION_MARK = "?";
	public static final String SLASH = "/";
	public static final String PERIOD = ".";
	public static final String COLON = ":";
	
	private StringUtility() {
		throw new AssertionError();
	}
	
}
