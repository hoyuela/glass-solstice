package com.discover.mobile.card.redeemcashbackbonus;

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
public class RedeemCashbackBonusInfo extends GroupComponentInfo {

    /**
     * Constructor
     * 
     */
    public RedeemCashbackBonusInfo() {
        super(R.string.section_title_redeem_cashback_bonus,

        new ClickComponentInfo(R.string.sub_section_title_partner_gift_cards,
                false, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }), new ClickComponentInfo(
                R.string.sub_section_title_discover_gift_cards, false,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }), new ClickComponentInfo(
                R.string.sub_section_title_statement_credit, false,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }), new ClickComponentInfo(
                R.string.sub_section_title_direct_deposit, false,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }), new ClickComponentInfo(
                R.string.sub_section_title_pay_with_cashback_bonus, false,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }), new ClickComponentInfo(
                R.string.sub_section_title_redemption_history, false,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }));
    }

    class RedeemCashbackBonusComponentInfo extends ClickComponentInfo {

        public RedeemCashbackBonusComponentInfo(int titleResource,
                OnClickListener listener) {
            super(titleResource, false, listener);
        }
    }
}
