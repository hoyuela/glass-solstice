package com.discover.mobile.section;

import com.discover.mobile.help.CustomerServiceSectionInfo;
import com.discover.mobile.profile.ProfileAndSettingsSectionInfo;
import com.discover.mobile.section.account.AccountSectionInfo;
import com.discover.mobile.section.home.HomeSectionInfo;
import com.google.common.collect.ImmutableList;

/**
 * This class defines the sections of the sliding drawer menu.  
 * @author jthornton
 *
 */
public final class Sections {
	
	/**
	 * Immutable list showing all the top level sections that are displayed in the sliding nav menu
	 */
	public static final ImmutableList<ComponentInfo> SECTION_LIST = ImmutableList.<ComponentInfo>builder()
			//Add Sections below
			.add(new HomeSectionInfo())
			.add(new AccountSectionInfo())
			.add(new ProfileAndSettingsSectionInfo())
			.add(new CustomerServiceSectionInfo())
			.build();
	
	private Sections() {
		throw new UnsupportedOperationException("This class is non-instantiable."); //$NON-NLS-1$
	}
	
}
