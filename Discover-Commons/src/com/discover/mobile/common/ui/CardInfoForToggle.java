package com.discover.mobile.common.ui;

import java.io.Serializable;

/**
 * Holds data necessary for the Account Toggle widget. This data is required for
 * Bank users to be able to see their account name and ending digits from the
 * widget.
 */
public class CardInfoForToggle implements Serializable{

	/** Serial ID */
	private static final long serialVersionUID = -1629914074130942085L;

	/** Card name (e.g. 'it Card' */
	private String cardAccountName;
	
	/** Ending card digits (e.g. 1234) */
	private String cardEndingDigits;
	
	/**
	 * Constructor
	 * 
	 * @param name
	 * @param digits
	 */
	public CardInfoForToggle(String name, String digits) {
		cardAccountName = name;
		cardEndingDigits = digits;
	}
	
	/**
	 * Default for facade (can remove later if unused).
	 */
	public CardInfoForToggle() {
		cardAccountName = "Discover Card";
		cardEndingDigits = "1234";
	}

	public String getCardAccountName() {
		return cardAccountName;
	}

	public void setCardAccountName(String cardAccountName) {
		this.cardAccountName = cardAccountName;
	}

	public String getCardEndingDigits() {
		return cardEndingDigits;
	}

	public void setCardEndingDigits(String cardEndingDigits) {
		this.cardEndingDigits = cardEndingDigits;
	}
}
