package com.discover.mobile.section;

import com.discover.mobile.section.account.AccountSectionInfo;
import com.discover.mobile.section.account.HomeSectionInfo;
import com.google.common.collect.ImmutableList;

public final class Sections {
	
	public static final ImmutableList<ComponentInfo> SECTION_LIST = ImmutableList.<ComponentInfo>builder()
			.add(new HomeSectionInfo())
			.add(new AccountSectionInfo())
			// TODO other sections
			.build();
	
	private Sections() {
		throw new UnsupportedOperationException("This class is non-instantiable."); //$NON-NLS-1$
	}
	
}
