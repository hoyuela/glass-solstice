package com.discover.mobile.navigation;

import android.view.View;
import android.view.ViewGroup;

import com.discover.mobile.section.SubSectionInfo;

final class SubSectionNavigationItem extends NavigationItem {
	
	private final SubSectionInfo subSectionInfo;
	
	SubSectionNavigationItem(final SubSectionInfo subSectionInfo) {
		this.subSectionInfo = subSectionInfo;
	}
	
	@Override
	int getViewType() {
		return NavigationItemAdapter.TYPE_SUB_SECTION;
	}
	
	@Override
	View getView(final NavigationItemAdapter sectionAdapter, final View convertView, final ViewGroup parent) {
		// TODO
		
		// TEMP
		return null;
	}
	
}
