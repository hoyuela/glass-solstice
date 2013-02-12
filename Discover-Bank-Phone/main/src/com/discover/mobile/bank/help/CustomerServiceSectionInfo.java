package com.discover.mobile.bank.help;

import com.discover.mobile.bank.R;
import com.discover.mobile.common.nav.section.FragmentComponentInfo;
import com.discover.mobile.common.nav.section.GroupComponentInfo;

/**
 * This class contains the subsections under the profile and settings menu in the sliding nav menu.
 * @author jthornton
 *
 */
public class CustomerServiceSectionInfo extends GroupComponentInfo{
	
	/**
	 * Construct the sub menu and add all the subsections under the profile and settings tip level item 
	 */
	public CustomerServiceSectionInfo(){
		super(R.string.customer_service,
				new FragmentComponentInfo(R.string.contact_us, CustomerServiceContactsFragment.class));
		
	}

}