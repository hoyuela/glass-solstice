package com.discover.mobile.navigation;

import android.view.View;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.section.ComponentInfo;

final class SubSectionNavigationItemView extends NavigationItemView {
	
	SubSectionNavigationItemView(final ComponentInfo subSectionInfo) {
		super(R.layout.navigation_menu_sub_section_item, subSectionInfo);
	}
	
	@Override
	int getViewType() {
		return NavigationItemAdapter.TYPE_SUB_SECTION;
	}
	
	@Override
	void customizeView(final View view, final TextView titleView) {
		// TODO customize and show highlight if selected
	}
	
}
