package com.discover.mobile.profile;

import com.discover.mobile.common.R;
import com.discover.mobile.push.history.PushHistoryFragment;
import com.discover.mobile.push.manage.PushManageFragment;
import com.discover.mobile.push.register.PushEnrollFragment;
import com.discover.mobile.section.FragmentComponentInfo;
import com.discover.mobile.section.GroupComponentInfo;

/**
 * This class contains the subsections under the profile and settings menu in the sliding nav menu.
 * @author jthornton
 *
 */
public class ProfileAndSettingsSectionInfo extends GroupComponentInfo{
	
	/**
	 * Construct the sub menu and add all the subsections under the profile and settings tip level item 
	 */
	public ProfileAndSettingsSectionInfo(){
		super(R.string.section_title_profile_and_settings,
				new FragmentComponentInfo(R.string.manage_alerts, PushManageFragment.class),
				new FragmentComponentInfo(R.string.sub_section_alert_history, PushHistoryFragment.class),
				new FragmentComponentInfo(R.string.sub_section_enroll, PushEnrollFragment.class));
		
	}

}
