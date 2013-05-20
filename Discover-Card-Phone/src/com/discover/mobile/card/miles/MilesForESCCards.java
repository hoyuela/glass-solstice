package com.discover.mobile.card.miles;

import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.common.nav.section.ClickComponentInfo;
import com.discover.mobile.common.nav.section.GroupComponentInfo;

import com.discover.mobile.card.R;

/**
 * ©2013 Discover Bank
 * 
 ** This class contains the subsections under the Payments menu in the sliding
 * nav menu.
 * 
 * @author CTS
 * 
 * @version 1.0
 */
public class MilesForESCCards extends GroupComponentInfo {

    /**
     * Constructor
     */
    public MilesForESCCards() {
        super(R.string.section_title_miles,

        new ClickComponentInfo(R.string.sub_section_title_miles_extras, false,
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                    }
                }),

        new ClickComponentInfo(R.string.sub_section_title_miles_refer_a_friend,
                false, new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                    }
                }));
    }

    class MilesComponentInfo extends ClickComponentInfo {

        public MilesComponentInfo(final int titleResource,
                final OnClickListener listener) {
            super(titleResource, false, listener);
        }

    }
}
