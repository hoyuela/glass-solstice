package com.discover.mobile.card.payments;

import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.card.R;
import com.discover.mobile.common.nav.section.ClickComponentInfo;
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
public class PaymentsSectionInfo extends GroupComponentInfo {

    /**
     * Constructor
     * 
     */
    public PaymentsSectionInfo() {
        super(R.string.section_title_payments,

        new ClickComponentInfo(R.string.sub_section_title_make_a_payment,
                false, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }),

        new ClickComponentInfo(R.string.sub_section_title_manage_payments,
                false, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }),

        new ClickComponentInfo(
                R.string.sub_section_title_manage_bank_information, false,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }),

        new ClickComponentInfo(R.string.sub_section_title_send_money, false,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }),

        new ClickComponentInfo(R.string.sub_section_title_send_money_history,
                false, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }));
    }

    class PaymentsComponentInfo extends ClickComponentInfo {

        public PaymentsComponentInfo(int titleResource, OnClickListener listener) {
            super(titleResource, false, listener);
        }

    }
}
