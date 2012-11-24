package com.discover.mobile.navigation;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.section.SectionInfo;
import com.discover.mobile.section.SubSectionInfo;
import com.google.common.collect.ImmutableList;

final class SectionNavigationItem extends NavigationItem {
	
	private final SectionInfo sectionInfo;
	private final List<SubSectionNavigationItem> subSections;
	
	SectionNavigationItem(final SectionInfo sectionInfo) {
		this.sectionInfo = sectionInfo;
		
		final ImmutableList.Builder<SubSectionNavigationItem> builder = ImmutableList.builder();
		for(final SubSectionInfo subSectionInfo : sectionInfo.getSubSections())
			builder.add(new SubSectionNavigationItem(subSectionInfo));
		subSections = builder.build();
	}
	
	@Override
	int getViewType() {
		return NavigationItemAdapter.TYPE_SECTION;
	}
	
	@Override
	View getView(final NavigationItemAdapter sectionAdapter, final View convertView, final ViewGroup parent) {
		View view;
		if(convertView == null)
			view = sectionAdapter.getLayoutInflater().inflate(R.layout.navigation_menu_section_item, null);
		else
			view = convertView;
		
		final TextView title = (TextView) view.findViewById(R.id.title);
		title.setText(sectionInfo.getTitleResource());
		
		// TODO
		
		return view;
	}
	
}
