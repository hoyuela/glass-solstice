/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.card;

import java.util.HashMap;
import java.util.Map;

import android.util.Pair;

/**
 * Class holding all of the indexes of items in the menu. The group values are
 * the components in the menu that expand and collapse. These indexes start at
 * 0. The section values are the location under each group that the section
 * under the location. For example, if the section was immediately below the
 * group when the group is expanded the index would be 1.
 * 
 * @author jthornton
 * 
 */
public final class CardMenuItemLocationIndex {

    /**
     * Home indexes
     */
    public static final int HOME_GROUP = 0;
    public static final int HOME_SECTION = 0;

    /**
     * Account indexes
     */
    public static final int ACCOUNT_GROUP = 1;
    public static final int ACCOUNT_SUMMARY_SECTION = 1;
    public static final int RECENT_ACTIVITY_SECTION = 2;
    public static final int SEARCH_TRANSACTION_SECTION = 3;
    public static final int STATEMENTS_SECTION = 4;

    /**
     * Payment indexes
     */
    public static final int PAYMENT_GROUP = 2;
    public static final int MAKE_A_PAYMENT_SECTION = 1;
    public static final int MANAGE_PAYMENTS_SECTION = 2;
    public static final int MANAGE_BANK_INFO_SECTION = 3;
    public static final int SEND_MONEY_SECTION = 4;
    public static final int SEND_MONEY_HISTORY_SECTION = 5;

    /**
     * Earn Cashback Bonus indexes
     */
    public static final int EARN_CASHBACK_BONUS_GROUP = 3;
    public static final int SIGN_UP_SECTION = 1;
    public static final int EXTRAS_SECTION = 2;
    public static final int REFER_A_FRIEND_SECTION = 3;

    /**
     * Redeem Cashback Bonus indexes
     */
    public static final int REDEEM_CASHBACK_BONUS_GROUP = 4;
    public static final int PARTNER_GIFT_SECTION = 1;
    public static final int DISCOVER_GIFT_SECTION = 2;
    public static final int STATEMENT_CREDIT_SECTION = 3;
    public static final int DIRECT_DEPOSIT_SECTION = 4;
    public static final int PAY_WITH_CASHBACK_BONUS_SECTION = 5;
    public static final int REDEMPTION_HISTORY_SECTION = 6;

    /**
     * Earn Miles indexes
     */
    public static final int EARN_MILES_GROUP = 3;
    public static final int SIGN_UP_FOR_MILES_SECTION = 1;
    public static final int EXTRAS_MILES_SECTION = 2;
    public static final int REFER_A_FRIEND_MILES_SECTION = 3;

    /**
     * Redeem Miles indexes
     */
    public static final int REDEEM_MILES_GROUP = 4;
    public static final int REDEEM_MILES_SECTION = 0;

    /**
     * Profile and Setting Indexes
     */
    public static final int PROFILE_AND_SETTINGS_GROUP = 5;
    public static final int MANAGE_ALERTS_SECTION = 1;
    public static final int ALERTS_HISTORY_SECTION = 2;
    public static final int CREATE_CASH_PIN_SECTION = 3;

    /**
     * Customer Service indexes
     */
    public static final int CUSTOMER_SERVICE_GROUP = 6;
    public static final int CONTACT_US_SECTION = 1;
    public static final int FAQ_SECTION = 2;

    private static Map<Integer, Pair<Integer, Integer>> itemIdLocationMap;

    private static CardMenuItemLocationIndex mCardMenuItemLocationIndex = null;

    /**
     * Private constructor so the class cannot be instantiated
     */
    private CardMenuItemLocationIndex() {

    }

    public static CardMenuItemLocationIndex getInstance() {
        if (null == mCardMenuItemLocationIndex) {
            mCardMenuItemLocationIndex = new CardMenuItemLocationIndex();
            initialiseMap();
        }

        return mCardMenuItemLocationIndex;
    }

    private static void initialiseMap() {
        itemIdLocationMap = new HashMap<Integer, Pair<Integer, Integer>>();

        itemIdLocationMap.put(R.string.sub_section_title_account_summary,
                new Pair<Integer, Integer>(ACCOUNT_GROUP,
                        ACCOUNT_SUMMARY_SECTION));
        itemIdLocationMap.put(R.string.sub_section_title_recent_activity,
                new Pair<Integer, Integer>(ACCOUNT_GROUP,
                        RECENT_ACTIVITY_SECTION));
        itemIdLocationMap.put(R.string.sub_section_title_search_transaction,
                new Pair<Integer, Integer>(ACCOUNT_GROUP,
                        SEARCH_TRANSACTION_SECTION));
        itemIdLocationMap.put(R.string.sub_section_title_statements,
                new Pair<Integer, Integer>(ACCOUNT_GROUP, STATEMENTS_SECTION));

        itemIdLocationMap.put(R.string.sub_section_title_make_a_payment,
                new Pair<Integer, Integer>(PAYMENT_GROUP,
                        MAKE_A_PAYMENT_SECTION));
        itemIdLocationMap.put(R.string.sub_section_title_manage_payments,
                new Pair<Integer, Integer>(PAYMENT_GROUP,
                        MANAGE_PAYMENTS_SECTION));
        itemIdLocationMap.put(
                R.string.sub_section_title_manage_bank_information,
                new Pair<Integer, Integer>(PAYMENT_GROUP,
                        MANAGE_BANK_INFO_SECTION));
        itemIdLocationMap.put(R.string.sub_section_title_send_money,
                new Pair<Integer, Integer>(PAYMENT_GROUP, SEND_MONEY_SECTION));
        itemIdLocationMap.put(R.string.sub_section_title_send_money_history,
                new Pair<Integer, Integer>(PAYMENT_GROUP,
                        SEND_MONEY_HISTORY_SECTION));

        itemIdLocationMap.put(R.string.sub_section_title_signup_for_2,
                new Pair<Integer, Integer>(EARN_CASHBACK_BONUS_GROUP,
                        SIGN_UP_SECTION));
        itemIdLocationMap.put(R.string.sub_section_title_extras,
                new Pair<Integer, Integer>(EARN_CASHBACK_BONUS_GROUP,
                        EXTRAS_SECTION));
        itemIdLocationMap.put(R.string.sub_section_title_refer_a_friend,
                new Pair<Integer, Integer>(EARN_CASHBACK_BONUS_GROUP,
                        REFER_A_FRIEND_SECTION));

        itemIdLocationMap.put(R.string.sub_section_title_partner_gift_cards,
                new Pair<Integer, Integer>(REDEEM_CASHBACK_BONUS_GROUP,
                        PARTNER_GIFT_SECTION));
        itemIdLocationMap.put(R.string.sub_section_title_discover_gift_cards,
                new Pair<Integer, Integer>(REDEEM_CASHBACK_BONUS_GROUP,
                        DISCOVER_GIFT_SECTION));
        itemIdLocationMap.put(R.string.sub_section_title_statement_credit,
                new Pair<Integer, Integer>(REDEEM_CASHBACK_BONUS_GROUP,
                        STATEMENT_CREDIT_SECTION));
        itemIdLocationMap.put(R.string.sub_section_title_direct_deposit,
                new Pair<Integer, Integer>(REDEEM_CASHBACK_BONUS_GROUP,
                        DIRECT_DEPOSIT_SECTION));
        itemIdLocationMap.put(
                R.string.sub_section_title_pay_with_cashback_bonus,
                new Pair<Integer, Integer>(REDEEM_CASHBACK_BONUS_GROUP,
                        PAY_WITH_CASHBACK_BONUS_SECTION));
        itemIdLocationMap.put(R.string.sub_section_title_redemption_history,
                new Pair<Integer, Integer>(REDEEM_CASHBACK_BONUS_GROUP,
                        REDEMPTION_HISTORY_SECTION));

        itemIdLocationMap.put(R.string.sub_section_title_manage_alerts,
                new Pair<Integer, Integer>(PROFILE_AND_SETTINGS_GROUP,
                        MANAGE_ALERTS_SECTION));
        itemIdLocationMap.put(R.string.sub_section_title_alert_history,
                new Pair<Integer, Integer>(PROFILE_AND_SETTINGS_GROUP,
                        ALERTS_HISTORY_SECTION));
        itemIdLocationMap.put(R.string.sub_section_title_create_cash_pin,
                new Pair<Integer, Integer>(PROFILE_AND_SETTINGS_GROUP,
                        CREATE_CASH_PIN_SECTION));

        itemIdLocationMap.put(R.string.sub_section_title_contact_us,
                new Pair<Integer, Integer>(CUSTOMER_SERVICE_GROUP,
                        CONTACT_US_SECTION));
        itemIdLocationMap
                .put(R.string.sub_section_title_faq,
                        new Pair<Integer, Integer>(CUSTOMER_SERVICE_GROUP,
                                FAQ_SECTION));

        itemIdLocationMap.put(R.string.sub_section_title_sign_up_for_miles,
                new Pair<Integer, Integer>(EARN_MILES_GROUP,
                        SIGN_UP_FOR_MILES_SECTION));
        itemIdLocationMap.put(R.string.sub_section_title_miles_extras,
                new Pair<Integer, Integer>(EARN_MILES_GROUP,
                        EXTRAS_MILES_SECTION));
        itemIdLocationMap.put(R.string.sub_section_title_miles_refer_a_friend,
                new Pair<Integer, Integer>(EARN_MILES_GROUP,
                        REFER_A_FRIEND_MILES_SECTION));

        itemIdLocationMap.put(R.string.section_title_redeem_miles,
                new Pair<Integer, Integer>(REDEEM_MILES_GROUP,
                        REDEEM_MILES_SECTION));
    }

    public int getMenuGroupLocation(final int title) {
        int location = 0;

        if (title != -1) {
            if (itemIdLocationMap.containsKey(title)) {
                location = itemIdLocationMap.get(title).first;
            }
        }
        return location;
    }

    public int getMenuSectionLocation(final int title) {
        int location = 0;

        if (title != -1) {
            if (itemIdLocationMap.containsKey(title)) {
                location = itemIdLocationMap.get(title).second;
            }
        }
        return location;
    }
}