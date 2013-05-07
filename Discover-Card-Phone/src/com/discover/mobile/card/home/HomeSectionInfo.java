package com.discover.mobile.card.home;

import android.view.View.OnClickListener;

import com.discover.mobile.card.R;
import com.discover.mobile.common.nav.section.FragmentComponentInfo;

public class HomeSectionInfo extends FragmentComponentInfo {
	
	public HomeSectionInfo() {
		super(R.string.section_title_home, HomeSummaryFragment.class);
	}
	
	public HomeSectionInfo(boolean showPushCount,OnClickListener pushCLick)
	{
		super(R.string.section_title_home, showPushCount,pushCLick, HomeSummaryFragment.class);
	}	
}
