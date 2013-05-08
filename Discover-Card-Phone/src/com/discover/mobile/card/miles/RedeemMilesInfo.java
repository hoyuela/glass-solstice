package com.discover.mobile.card.miles;

import android.view.View.OnClickListener;

import com.discover.mobile.card.R;
import com.discover.mobile.card.home.HomeSummaryFragment;
import com.discover.mobile.common.nav.section.ClickComponentInfo;
import com.discover.mobile.common.nav.section.ComponentInfo;
import com.discover.mobile.common.nav.section.FragmentComponentInfo;
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
public class RedeemMilesInfo extends FragmentComponentInfo {

    /**
     * 
     */

    public RedeemMilesInfo() {
       // super(R.string.section_title_redeem_miles, HomeSummaryFragment.class);
         super(R.string.section_title_redeem_miles, true,
         RedeemMilesFragment.class);
    }

}

