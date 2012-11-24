package com.discover.mobile.section.account;

import com.discover.mobile.R;
import com.discover.mobile.section.SectionInfo;
import com.discover.mobile.section.SubSectionInfo;

public final class AccountSectionInfo extends SectionInfo {
	
	public AccountSectionInfo() {
		// TEMP titles
		super(R.string.app_name, AccountHomeFragment.class,
				new SubSectionInfo(R.string.app_name, AccountHomeFragment.class), // TEMP home
				new SubSectionInfo(R.string.app_name, AccountHomeFragment.class));
		// TODO others
	}
	
}
