package com.discover.mobile.card.help;

import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.common.nav.section.ClickComponentInfo;
import com.discover.mobile.common.nav.section.GroupComponentInfo;

import com.discover.mobile.card.R;

/**
 * ©2013 Discover Bank
 * 
 * This class contains the subsections under the CustomerService menu in the
 * sliding nav menu.
 * 
 * @author CTS
 * 
 * @version 1.0
 */
public class CustomerServiceContactInfo extends GroupComponentInfo {

    /**
     * Constructor
     */
    public CustomerServiceContactInfo() {
        super(R.string.section_title_customer_service,

        new ClickComponentInfo(R.string.sub_section_title_contact_us, false,
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                    }
                }),

        new ClickComponentInfo(R.string.sub_section_title_faq, false,
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                    }
                }));
    }

    class CustomerServiceContactComponentInfo extends ClickComponentInfo {

        public CustomerServiceContactComponentInfo(final int titleResource,
                final OnClickListener listener) {
            super(titleResource, false, listener);
        }
    }

}
