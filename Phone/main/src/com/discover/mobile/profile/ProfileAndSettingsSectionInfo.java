package com.discover.mobile.profile;

import com.discover.mobile.common.R;
import com.discover.mobile.push.PushManageFragment;
import com.discover.mobile.section.FragmentComponentInfo;
import com.discover.mobile.section.GroupComponentInfo;

public class ProfileAndSettingsSectionInfo extends GroupComponentInfo{
	
	public ProfileAndSettingsSectionInfo(){
		super(R.string.section_title_profile_and_settings,
				new FragmentComponentInfo(R.string.manage_alerts, PushManageFragment.class));
		
	}

}
