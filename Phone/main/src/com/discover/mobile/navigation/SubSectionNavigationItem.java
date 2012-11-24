package com.discover.mobile.navigation;

import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.section.SubSectionInfo;

final class SubSectionNavigationItem extends NavigationItem {
	
	private final SubSectionInfo subSectionInfo;
	
	SubSectionNavigationItem(final NavigationItemAdapter adapter, final SubSectionInfo subSectionInfo,
			final int absoluteIndex) {
		
		super(adapter, R.layout.navigation_menu_sub_section_item, absoluteIndex);
		
		this.subSectionInfo = subSectionInfo;
	}
	
	@Override
	int getViewType() {
		return NavigationItemAdapter.TYPE_SUB_SECTION;
	}
	
	@Override
	void customizeItemView(final View view, final TextView title) {
		title.setText(subSectionInfo.getTitleResource());
		
		// TEMP
		title.setText("Sub-Section " + absoluteIndex);
		
		// TODO show highlight if selected
	}
	
	@Override
	void onClick(final ListView listView) {
		// TODO
	}
	
	void show() {
		navigationItemAdapter.insert(this, absoluteIndex);
		
		// TODO
	}
	
	void hide() {
		navigationItemAdapter.remove(SubSectionNavigationItem.this);
		
		// TODO
	}
	
}
