package com.discover.mobile.common.nav;

import android.view.View;
import android.widget.TextView;

import com.discover.mobile.common.R;
import com.discover.mobile.common.nav.section.ComponentInfo;

final class SectionNavigationItemView extends NavigationItemView {

	SectionNavigationItemView(final ComponentInfo componentInfo) {
		super(R.layout.navigation_menu_section_item, componentInfo);
	}

	@Override
	int getViewType() {
		return NavigationItemAdapter.TYPE_SECTION;
	}

	@Override
	void customizeView(final View view, final TextView titleView) {
		if (view.isSelected()){
			titleView.setTextColor(view.getResources().getColor(R.color.orange));
		}else {
			titleView.setTextColor(view.getResources().getColor(R.color.white));
		}

	}

}
