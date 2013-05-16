package com.discover.mobile.card.phonegap.plugins;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.utils.Utils;

/**
 * ©2013 Discover Bank
 * 
 * TODO: This class will map all the Strings received from JQM to native string
 * resource ids.
 * 
 * @author CTS
 * 
 * @version 1.0
 */
public class JQMResourceMapper {

    private static JQMResourceMapper m_JQMStringMapper = null;

    /**
     * Hashmap of the JQM page titles and respective resource id.
     */
    private static Map<String, Integer> titleIdMap;

    private final String TAG = "JQMResourceMapper";

    private JQMResourceMapper() {

    }

    public static JQMResourceMapper getInstance() {
        if (null == m_JQMStringMapper) {
            m_JQMStringMapper = new JQMResourceMapper();
            initialiseTitleMap();
        }

        return m_JQMStringMapper;
    }

    /**
     * This function initialises and creates the hashmap of JQM page titles.
     * 
     */
    private static void initialiseTitleMap() {

        Map<String, Integer> temp = new HashMap<String, Integer>();
        temp.put("Account Summary", R.string.sub_section_title_account_summary);
        temp.put("Recent Activity", R.string.sub_section_title_recent_activity);
        temp.put("Search Transactions",
                R.string.sub_section_title_search_transaction);
        temp.put("Statements", R.string.sub_section_title_statements);

        temp.put("Make a Payment", R.string.sub_section_title_make_a_payment);
        temp.put("Manage Payments", R.string.sub_section_title_manage_payments);
        temp.put("Manage Bank Information",
                R.string.sub_section_title_manage_bank_information);
        temp.put("Send Money", R.string.sub_section_title_send_money);
        temp.put("Send Money History",
                R.string.sub_section_title_send_money_history);

        temp.put("Cashback Bonus Promos",
                R.string.sub_section_title_signup_for_2);
        temp.put("Extras", R.string.sub_section_title_extras);
        temp.put("Refer A Friend", R.string.sub_section_title_refer_a_friend);

        temp.put("Partner Gift Cards & eCerts",
                R.string.sub_section_title_partner_gift_cards);
        temp.put("Discover Gift Cards",
                R.string.sub_section_title_discover_gift_cards);
        temp.put("Statement Credit",
                R.string.sub_section_title_statement_credit);
        temp.put("Direct Deposit", R.string.sub_section_title_direct_deposit);
        temp.put("Pay with Cashback Bonus",
                R.string.sub_section_title_pay_with_cashback_bonus);
        temp.put("Redemption History",
                R.string.sub_section_title_redemption_history);

        temp.put("Manage Text & Alerts",
                R.string.sub_section_title_manage_alerts);
        temp.put("Push Alerts History",
                R.string.sub_section_title_alert_history);
        temp.put("Create Cash PIN", R.string.sub_section_title_create_cash_pin);

        temp.put("Contact Us", R.string.sub_section_title_contact_us);
        temp.put("Frequently Asked Questions", R.string.sub_section_title_faq);

        temp.put("Miles Promotions",
                R.string.sub_section_title_sign_up_for_miles);

        temp.put("Redeem Miles", R.string.section_title_redeem_miles);
        temp.put("Privacy & Terms", R.string.privacy_terms_title);
        temp.put("Enhanced Account Security",
                R.string.enhanced_account_security_title);

        temp.put("Redemption Options", R.string.redeem_cashback_bonus_landing);

        titleIdMap = Collections.unmodifiableMap(temp);
    }

    /**
     * This funciton returns the respective string resource id for the string
     * title.
     * 
     * @param title
     *            for which resource id to be returned.
     * @return corresponding integer resource id.
     */
    public int getTitleStringId(String title) {
        Utils.log(TAG, "inside getTitleString n title is " + title
                + "value is " + titleIdMap.get(title));

        int titleId = -1;

        if (null != title) {
            if (titleIdMap.containsKey(title))
                titleId = titleIdMap.get(title);
            return titleId;
        } else
            return titleId;
    }
}
