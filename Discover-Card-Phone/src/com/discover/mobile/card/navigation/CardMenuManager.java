package com.discover.mobile.card.navigation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.util.Log;

import com.discover.mobile.card.R;

/**
 * ©2013 Discover Bank
 * 
 * This class is a util class containing all the menus that will be part of left
 * navigation.
 * 
 * @author CTS
 * 
 * @version 1.0
 */
public class CardMenuManager {

    private Map<String, CardMenu> cardMenuTypes = null;
    private ArrayList<String> supportedMenus = null;
    private final Context mContext;

    /**
     * Constructor
     * 
     */
    public CardMenuManager(Context context) {
        mContext = context;
        cardMenuTypes = new HashMap<String, CardMenu>();

        cardMenuTypes.put(mContext.getString(R.string.card_cashback),
                new CashBackCard());
        cardMenuTypes.put(mContext.getString(R.string.card_miles),
                new MilesCard());
        cardMenuTypes.put(mContext.getString(R.string.card_sendmoney),
                new SendMoney());
        Log.d("CardMenu", "list size is" + cardMenuTypes.size());
    }

    public ArrayList<String> getValidMenuItems(String incentiveTypeCode,
            String incentiveCode, String optionCode) {

        Set<String> cards = cardMenuTypes.keySet();
        supportedMenus = new ArrayList<String>();

        for (String card : cards) {
            // CardMenu cardMenu = cardMenuTypes.get(card);

            if (null != cardMenuTypes.get(card)) {
                if (!incentiveTypeCode.isEmpty() && !incentiveCode.isEmpty()
                        && !optionCode.isEmpty()) {
                    if (cardMenuTypes.get(card).its.indexOf(incentiveTypeCode) != -1
                            && cardMenuTypes.get(card).ics
                                    .indexOf(incentiveCode) != -1
                            && cardMenuTypes.get(card).docs.indexOf(optionCode) == -1) {
                        supportedMenus.add(card);
                        Log.d("CardMenu", "adding menu " + card);
                    } else
                        Log.d("CardMenu", "list is empty");
                }
            }
        }

        return supportedMenus;
    }

    public ArrayList<String> getSupportedCardMenuTypes() {
        return supportedMenus;
    }

}

class CardMenu {
    public String code = null;
    public ArrayList<String> its = null;
    public ArrayList<String> ics = null;
    public ArrayList<String> docs = null;

    public CardMenu() {
        its = new ArrayList<String>();
        ics = new ArrayList<String>();
        docs = new ArrayList<String>();
    }
}

class CashBackCard extends CardMenu {
    /*
     * public String code = null; public ArrayList<String> its = null; public
     * ArrayList<String> ics = null; public ArrayList<String> docs = null;
     */

    public CashBackCard() {
        code = "CB";
        its = new ArrayList<String>();
        its.add("CBB");

        ics = new ArrayList<String>();
        ics.add("000001");
        ics.add("000002");
        ics.add("000003");
        ics.add("000004");
        ics.add("000005");
        ics.add("000006");
        ics.add("000007");
        ics.add("000011");
        ics.add("000013");
        ics.add("000014");
        ics.add("000015");
        ics.add("000016");

        docs = new ArrayList<String>();
    }
}

class MilesCard extends CardMenu {

    /*
     * public String code = null; public ArrayList<String> its = null; public
     * ArrayList<String> ics = null; public ArrayList<String> docs = null;
     */

    public MilesCard() {
        code = "MI";
        its = new ArrayList<String>();
        its.add("MI2");

        ics = new ArrayList<String>();
        ics.add("000001");
        ics.add("000002");
        ics.add("000003");

        docs = new ArrayList<String>();
    }
}

class SendMoney extends CardMenu {

    /*
     * public String code = null; public ArrayList<String> its = null; public
     * ArrayList<String> ics = null; public ArrayList<String> docs = null;
     */

    public SendMoney() {
        code = "MM";
        its = new ArrayList<String>();
        its.add("CBB");
        its.add("MI2");

        ics = new ArrayList<String>();
        ics.add("000001");
        ics.add("000011");
        ics.add("000013");
        ics.add("000014");
        ics.add("000015");
        ics.add("000016");
        ics.add("000002");
        ics.add("000003");

        docs = new ArrayList<String>();
    }
}