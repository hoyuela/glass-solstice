package com.discover.mobile.card.home;

import android.view.View.OnClickListener;

import com.discover.mobile.common.nav.section.FragmentComponentInfo;

import com.discover.mobile.card.R;

/**
 * 
 * ©2013 Discover Bank
 * 
 * Home Section group menu item
 * 
 * @author CTS
 * 
 * @version 1.0
 */
public class HomeSectionInfo extends FragmentComponentInfo {

    public HomeSectionInfo() {
        super(R.string.section_title_home, HomeSummaryFragment.class);
    }

    public HomeSectionInfo(final boolean showPushCount,
            final OnClickListener pushCLick) {
        super(R.string.section_title_home, showPushCount, pushCLick,
                HomeSummaryFragment.class);
    }
}
