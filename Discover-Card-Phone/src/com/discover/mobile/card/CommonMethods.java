package com.discover.mobile.card;

import com.discover.mobile.card.services.auth.AccountDetails;
import com.discover.mobile.common.AccountCodes;
import com.google.common.base.Strings;

public final class CommonMethods {
	private final static String TAG = CommonMethods.class.getSimpleName();

	
	

	

	
	
	/**
	 * Determines whether or not the current account's card type is a Miles
	 * Escape, or not.
	 * 
	 * @param accountDetails
	 * @return true if a Miles Escape, false otherwise.
	 */
	public final static boolean isEscapeCard(AccountDetails accountDetails) {

		// No Outage
		if (!accountDetails.cardProductGroupOutageMode) {
			if (accountDetails.cardProductGroupCode
					.equalsIgnoreCase(AccountCodes.CPGC_ESCAPE)) {
				return true;
			}
			return false;
		}

		// Outage - manual discernment of type
		if (accountDetails.cardType
				.equalsIgnoreCase(AccountCodes.CARD_TYPE_PERSONAL)
				&& accountDetails.incentiveCode
						.equalsIgnoreCase(AccountCodes.INCENTIVE_CODE_ESC)
				&& accountDetails.incentiveTypeCode
						.equalsIgnoreCase(AccountCodes.INCENTIVE_TYPE_MI2)) {
			return true;
		}
		return false;
	}


	

	/**
	 * Determines whether or not the current account's card type is a Cashback
	 * Rewards card.
	 * 
	 * @param accountDetails
	 * @return true if a Cashback, false otherwise.
	 */
	public final static boolean isCashbackCard(AccountDetails accountDetails) {

		if (accountDetails.incentiveTypeCode
				.equalsIgnoreCase(AccountCodes.INCENTIVE_TYPE_CBB)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Takes a string and adds commas ',' every three character places from the
	 * right. Used for formatting natural numbers with thousand commas.
	 * 
	 * @param str
	 * @return Formatted string with commas.
	 */
	public final static String insertCommas(String str) {
		if(Strings.isNullOrEmpty(str))
			return "";
		if (str.length() < 4) 
			return str;
		else	
			return insertCommas(str.substring(0, str.length() - 3)) + ","
					+ str.substring(str.length() - 3, str.length());
	}

	private CommonMethods() {
		throw new UnsupportedOperationException(
				"This class is non-instantiable"); //$NON-NLS-1$
	}
}
