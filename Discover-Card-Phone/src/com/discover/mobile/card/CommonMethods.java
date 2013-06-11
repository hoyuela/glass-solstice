package com.discover.mobile.card;

import com.google.common.base.Strings;

import com.discover.mobile.common.AccountCodes;

import com.discover.mobile.card.services.auth.AccountDetails;

public final class CommonMethods {
    
    /* 13.3  Changes */
    public static final String INCENTIVE_TYPE_DBC = "SBC";
    /* 13.3  Changes */    

    /**
     * Determines whether or not the current account's card type is a Miles
     * Escape, or not.
     * 
     * @param accountDetails
     * @return true if a Miles Escape, false otherwise.
     */
    public final static boolean isEscapeCard(final AccountDetails accountDetails) {

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
    public final static boolean isCashbackCard(
            final AccountDetails accountDetails) {

        /* 13.3  Changes */
        if (accountDetails.incentiveTypeCode
                .equalsIgnoreCase(AccountCodes.INCENTIVE_TYPE_CBB)||accountDetails.incentiveTypeCode
                .equalsIgnoreCase(INCENTIVE_TYPE_DBC)) {
            return true;
        } else {
            return false;
        }
        /* 13.3  Changes */
    }

    /**
     * Takes a string and adds commas ',' every three character places from the
     * right. Used for formatting natural numbers with thousand commas.
     * 
     * @param str
     * @return Formatted string with commas.
     */
    public final static String insertCommas(final String str) {
        if (Strings.isNullOrEmpty(str)) {
            return "";
        }
        if (str.length() < 4) {
            return str;
        } else {
            return insertCommas(str.substring(0, str.length() - 3)) + ","
                    + str.substring(str.length() - 3, str.length());
        }
    }

    private CommonMethods() {
        throw new UnsupportedOperationException(
                "This class is non-instantiable"); //$NON-NLS-1$
    }
}
