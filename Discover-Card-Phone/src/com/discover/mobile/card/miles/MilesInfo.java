package com.discover.mobile.card.miles;

import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.card.R;
import com.discover.mobile.common.nav.section.ClickComponentInfo;
import com.discover.mobile.common.nav.section.GroupComponentInfo;

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
public class MilesInfo extends GroupComponentInfo {

    /**
     * Constructor
     */
    public MilesInfo() {
        super(R.string.section_title_miles,

        new ClickComponentInfo(R.string.sub_section_title_sign_up_for_miles,
                false, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }),

        new ClickComponentInfo(R.string.sub_section_title_miles_extras, false,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }),

        new ClickComponentInfo(R.string.sub_section_title_miles_refer_a_friend,
                false, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }));
    }

    class MilesComponentInfo extends ClickComponentInfo {

        public MilesComponentInfo(int titleResource, OnClickListener listener) {
            super(titleResource, false, listener);
        }

    }
}
