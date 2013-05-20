/**
 * 
 */
package com.discover.mobile.card.push.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.discover.mobile.common.AccountType;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.facade.FacadeFactory;

import com.discover.mobile.card.common.SessionCookieManager;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;

import com.discover.mobile.card.R;
import com.discover.mobile.card.navigation.CardNavigationRootActivity;

import com.discover.mobile.PushConstant;
import com.xtify.sdk.api.NotificationsPreference;

/**
 * @author 328073
 * 
 */
public class XtifyEventReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        NotificationsPreference.setIcon(context, R.drawable.discove_mobile_icn);
        String payload = null;

        final SharedPreferences pushSharedPrefs = context.getSharedPreferences(
                "PUSH_PREF", // TODO: Push
                Context.MODE_PRIVATE);

        payload = intent.getExtras().getString("data.customKey");
        String[] keyValuePayload = null;
        String[] reqIdKeyValue = null;
        String[] pageCodeKeyValue = null;

        if (payload != null) {
            keyValuePayload = payload.split(",");
            reqIdKeyValue = keyValuePayload[0].split("=");
            pageCodeKeyValue = keyValuePayload[1].split("=");
        }

        if (intent.getAction().equalsIgnoreCase("com.xtify.sdk.EVENT_NCK")) {
            if (!(pushSharedPrefs.getBoolean(
                    PushConstant.pref.PUSH_OTHER_USER_STATUS, false)
                    && Globals.getCurrentAccount() != null && Globals
                    .getCurrentAccount() == AccountType.CARD_ACCOUNT)
                    || !(Globals.getCurrentAccount() != null && Globals
                            .getCurrentAccount() == AccountType.BANK_ACCOUNT)) {

                final Editor editor = pushSharedPrefs.edit();
                final CardShareDataStore cardShareDataStore = CardShareDataStore
                        .getInstance(context);
                final SessionCookieManager sessionCookieManagerObj = cardShareDataStore
                        .getCookieManagerInstance();
                Intent intent2;
                if (sessionCookieManagerObj.getSecToken() != null
                        && !sessionCookieManagerObj.getSecToken()
                                .equalsIgnoreCase("")) {
                    editor.putBoolean(PushConstant.pref.PUSH_IS_SESSION_VALID,
                            true);
                    editor.putBoolean(PushConstant.pref.PUSH_OFFLINE, false);
                    intent2 = new Intent(context,
                            CardNavigationRootActivity.class);
                } else {
                    editor.putBoolean(PushConstant.pref.PUSH_IS_SESSION_VALID,
                            false);
                    editor.putBoolean(PushConstant.pref.PUSH_OFFLINE, true);
                    intent2 = new Intent(context, FacadeFactory
                            .getLoginFacade().getLoginActivityClass());
                }

                if (pageCodeKeyValue != null && pageCodeKeyValue[1] != null) {
                    editor.putInt(PushConstant.pref.PUSH_NAVIGATION,
                            getTargetNavigation(pageCodeKeyValue[1]));
                }
                if (reqIdKeyValue != null && reqIdKeyValue[1] != null) {
                    editor.putString(PushConstant.pref.PUSH_REQUEST_ID,
                            reqIdKeyValue[1]);
                }
                editor.commit();

                final int flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP;
                intent2.setFlags(flags);
                context.startActivity(intent2);
            } else {
                return;
            }
        }

    }

    /**
     * Based on requestCode this method will give mapped string to redirect page
     * 
     * @param code
     * @return
     */
    private int getTargetNavigation(final String code) {
        if (code.equalsIgnoreCase(PushConstant.misc.PUSH_ACCOUNT_ACTIVITY)) {
            return R.string.sub_section_title_recent_activity;
        } else if (code.equalsIgnoreCase(PushConstant.misc.PUSH_CASH)) {
            return R.string.sub_section_title_signup_for_2;
        } else if (code.equalsIgnoreCase(PushConstant.misc.PUSH_MILES)) {
            return R.string.section_title_redeem_miles;
        } else if (code.equalsIgnoreCase(PushConstant.misc.PUSH_PAY_HISTORY)) {
            return R.string.sub_section_title_manage_payments;
        } else if (code.equalsIgnoreCase(PushConstant.misc.PUSH_PAYMENT)) {
            return R.string.sub_section_title_manage_payments;
        } else if (code.equalsIgnoreCase(PushConstant.misc.PUSH_REDEMPTION)) {
            return R.string.sub_section_title_partner_gift_cards;
        } else if (code
                .equalsIgnoreCase(PushConstant.misc.PUSH_STATMENT_LANDING)) {
            return R.string.sub_section_title_statements;
        } else {
            return R.string.sub_section_title_alert_history;
        }
    }
}
