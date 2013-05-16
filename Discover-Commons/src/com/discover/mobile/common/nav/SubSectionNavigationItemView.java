package com.discover.mobile.common.nav;

import android.view.View;
import android.widget.TextView;

import com.discover.mobile.common.R;
import com.discover.mobile.common.nav.section.ComponentInfo;

final class SubSectionNavigationItemView extends NavigationItemView {

	SubSectionNavigationItemView(final ComponentInfo subSectionInfo) {
		super(R.layout.navigation_menu_sub_section_item, subSectionInfo);
	}

	@Override
	int getViewType() {
		return NavigationItemAdapter.TYPE_SUB_SECTION;
	}

	@Override
	void customizeView(final View view, final TextView titleView, final boolean selected) {
		if (selected){
			titleView.setTextColor(view.getResources().getColor(R.color.orange));
		}else {
			titleView.setTextColor(view.getResources().getColor(R.color.white));
		}
	}

}
