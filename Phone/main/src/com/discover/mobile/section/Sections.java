package com.discover.mobile.section;

import com.discover.mobile.section.account.AccountSectionInfo;
import com.google.common.collect.ImmutableList;

public final class Sections {
	
	public static final ImmutableList<SectionInfo> SECTIONS = ImmutableList.<SectionInfo>builder()
			.add(new AccountSectionInfo()) // TEMP home
			.add(new AccountSectionInfo())
			// TODO other sections
			.build();
	
	private Sections() {
		throw new UnsupportedOperationException("This class is non-instantiable."); //$NON-NLS-1$
	}
	
}
