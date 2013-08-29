package com.discover.mobile.card.earncashbackbonus;

import java.util.ArrayList;

import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.card.R;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.common.nav.section.ClickComponentInfo;
import com.discover.mobile.common.nav.section.ComponentInfo;
import com.discover.mobile.common.nav.section.GroupComponentInfo;

/**
 * ©2013 Discover Bank
 * 
 * This class contains the subsections under the Payments menu in the sliding
 * nav menu.
 * 
 * @author CTS
 * 
 * @version 1.0
 */
public class EarnCashbackBonusInfo extends GroupComponentInfo {

	/*    13.5  Changes */
    public static ArrayList<ComponentInfo> componentInfo = new ArrayList<ComponentInfo>();
    public static ComponentInfo componentInfoArray[];

    public static void setComponentArray(
            final ArrayList<ComponentInfo> clickComponentInfos) {
        componentInfoArray = new ComponentInfo[clickComponentInfos.size()];
        int i = 0;
        for (ComponentInfo component : clickComponentInfos) {
            componentInfoArray[i] = component;
            i++;
        }
    }

    public static void setComponentList() {

        if (Utils.isOptionAvailable(R.string.sub_section_title_signup_for_2_hidearray)) {
            componentInfo.add(new ClickComponentInfo(
                    R.string.sub_section_title_signup_for_2, false,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {

                        }
                    }));
        }

        if (Utils.isOptionAvailable(R.string.sub_section_title_extras)) {
            componentInfo.add(new ClickComponentInfo(
                    R.string.sub_section_title_extras, false,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {

                        }
                    }));

        }

        if (Utils.isOptionAvailable(R.string.sub_section_title_refer_a_friend_hidearray)) {
            componentInfo.add(new ClickComponentInfo(
                    R.string.sub_section_title_refer_a_friend, false,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {

                        }
                    }));
        }
        setComponentArray(componentInfo);
        componentInfo.clear();

    }

    /**
     * Constructor
     */
    public EarnCashbackBonusInfo() {
        super(R.string.section_title_earn_cashback_bonus, componentInfoArray);
        componentInfoArray = null ;
    }

    /*    13.5  Changes */

    class EarnCashbackBonusComponentInfo extends ClickComponentInfo {

        public EarnCashbackBonusComponentInfo(final int titleResource,
                final OnClickListener listener) {
            super(titleResource, false, listener);
        }

    }
}
