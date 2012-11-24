package com.discover.mobile.navigation;

import java.util.List;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.discover.mobile.R;
import com.discover.mobile.section.SectionInfo;
import com.discover.mobile.section.SubSectionInfo;
import com.google.common.collect.ImmutableList;

final class SectionNavigationItem extends NavigationItem {
	
	private final SectionInfo sectionInfo;
	private final List<SubSectionNavigationItem> subSections;
	
	private boolean expanded;
	
	SectionNavigationItem(final NavigationItemAdapter adapter, final SectionInfo sectionInfo, final int index) {
		super(adapter, R.layout.navigation_menu_section_item, index);
		
		this.sectionInfo = sectionInfo;
		
		final List<SubSectionInfo> subsections = sectionInfo.getSubSections();
		final ImmutableList.Builder<SubSectionNavigationItem> builder = ImmutableList.builder();
		for(int i = 0; i < subsections.size(); i++) {
			final SubSectionInfo subSectionInfo = subsections.get(i);
			builder.add(new SubSectionNavigationItem(adapter, subSectionInfo, index + i + 1));
		}
		subSections = builder.build();
	}
	
	@Override
	Class<? extends Fragment> getFragmentClass() {
		return sectionInfo.getFragmentClass();
	}
	
	@Override
	int getViewType() {
		return NavigationItemAdapter.TYPE_SECTION;
	}
	
	@Override
	void customizeItemView(final View view, final TextView title) {
		title.setText(sectionInfo.getTitleResource());
		
		// TODO show highlight if selected
	}
	
	@Override
	void onClick(final ListView listView) {
		if(expanded)
			collapse();
		else
			expand();
	}
	
	private void expand() {
		if(expanded)
			return;
		
		final SectionNavigationItem expandedSection = navigationItemAdapter.getExpandedSection();
		if(expandedSection != null)
			expandedSection.collapse();
		
		for(final SubSectionNavigationItem subSection : subSections)
			subSection.show();
		
		showFragment();
		
		navigationItemAdapter.setExpandedSection(this);
		expanded = true;
	}
	
	private void collapse() {
		if(!expanded)
			return;
		
		for(final SubSectionNavigationItem subSection : subSections)
			subSection.hide();
		
		navigationItemAdapter.setExpandedSection(null);
		expanded = false;
	}
	
}
