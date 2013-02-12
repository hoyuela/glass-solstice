package com.discover.mobile.bank.account;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.discover.mobile.bank.R;
import com.discover.mobile.common.nav.section.ClickComponentInfo;
import com.discover.mobile.common.nav.section.FragmentComponentInfo;
import com.discover.mobile.common.nav.section.GroupComponentInfo;
import com.discover.mobile.common.urlmanager.BankUrlManager;

public final class BankAccountSectionInfo extends GroupComponentInfo {

	public BankAccountSectionInfo() {
		super(R.string.section_title_account,
				new FragmentComponentInfo(R.string.sub_section_title_account_summary, BankAccountSummaryFragment.class), 
				new ClickComponentInfo(R.string.sub_section_title_statements,true,listener()),
				new ClickComponentInfo(R.string.sub_section_title_open_new_account,true, listener()));
	}

	private static OnClickListener listener(){

		return new OnClickListener() {

			@Override
			public void onClick(final View v) {
				final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(BankUrlManager.getBaseUrl()));
				v.getContext().startActivity(browserIntent);
			}
		};
	}
}
