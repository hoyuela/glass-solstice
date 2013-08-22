package com.discover.mobile.card.earncashbackbonus;

import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.common.nav.section.ClickComponentInfo;
import com.discover.mobile.common.nav.section.GroupComponentInfo;

import com.discover.mobile.card.R;

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

    /**
     * Constructor
     */
    public EarnCashbackBonusInfo() {
        super(R.string.section_title_earn_cashback_bonus,

        new ClickComponentInfo(R.string.sub_section_title_signup_for_2, false,
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                    }
                }),

        new ClickComponentInfo(R.string.sub_section_title_extras, false,
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                    }
                }),

        new ClickComponentInfo(R.string.sub_section_title_refer_a_friend,
                false, new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                    }
                }));
    }

    class EarnCashbackBonusComponentInfo extends ClickComponentInfo {

        public EarnCashbackBonusComponentInfo(final int titleResource,
                final OnClickListener listener) {
            super(titleResource, false, listener);
        }

    }
}
