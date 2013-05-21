package com.discover.mobile.card.profile;

import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.common.nav.section.ClickComponentInfo;
import com.discover.mobile.common.nav.section.GroupComponentInfo;

import com.discover.mobile.card.R;

/**
 * This class contains the subsections under the profile and settings menu in
 * the sliding nav menu.
 * 
 * @author jthornton
 * 
 */
public class ProfileAndSettingsSectionInfo extends GroupComponentInfo {

    /**
     * Construct the sub menu and add all the subsections under the profile and
     * settings tip level item
     */
    public ProfileAndSettingsSectionInfo() {
        super(R.string.section_title_profile_and_settings,

        new ClickComponentInfo(R.string.sub_section_title_manage_alerts, false,
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                    }
                }),

        new ClickComponentInfo(R.string.sub_section_title_alert_history, false,
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                    }
                }),

        new ClickComponentInfo(R.string.sub_section_title_create_cash_pin,
                false, new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                    }
                }));

    }

    class ProfileAndSettingsComponentInfo extends ClickComponentInfo {

        public ProfileAndSettingsComponentInfo(final int titleResource,
                final OnClickListener listener) {
            super(titleResource, false, listener);
        }

    }

}
