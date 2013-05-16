package com.discover.mobile.card.account;

import android.view.View;
import android.view.View.OnClickListener;

import com.discover.mobile.common.nav.section.ClickComponentInfo;
import com.discover.mobile.common.nav.section.GroupComponentInfo;

import com.discover.mobile.card.R;

public final class AccountSectionInfo extends GroupComponentInfo {

    public AccountSectionInfo() {
        super(R.string.section_title_account,

        new ClickComponentInfo(R.string.sub_section_title_account_summary,
                false, new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                    }
                }), new ClickComponentInfo(
                R.string.sub_section_title_recent_activity, false,
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                    }
                }), new ClickComponentInfo(
                R.string.sub_section_title_search_transaction, false,
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                    }
                }), new ClickComponentInfo(
                R.string.sub_section_title_statements, false,
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                    }
                }));
    }

    class AccountComponentInfo extends ClickComponentInfo {

        public AccountComponentInfo(final int titleResource,
                final OnClickListener listener) {
            super(titleResource, false, listener);
        }

    }

}
