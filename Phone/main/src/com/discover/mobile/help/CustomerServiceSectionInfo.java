package com.discover.mobile.help;

import com.discover.mobile.R;
import com.discover.mobile.push.manage.PushManageFragment;
import com.discover.mobile.section.FragmentComponentInfo;
import com.discover.mobile.section.GroupComponentInfo;

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
				new FragmentComponentInfo(R.string.contact_us, PushManageFragment.class));
		
	}

}