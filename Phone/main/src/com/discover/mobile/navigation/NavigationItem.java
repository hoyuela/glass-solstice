package com.discover.mobile.navigation;

import java.util.List;

import android.widget.ListView;

import com.discover.mobile.section.ComponentInfo;
import com.discover.mobile.section.FragmentComponentInfo;
import com.discover.mobile.section.GroupComponentInfo;
import com.discover.mobile.section.Sections;
import com.google.common.collect.Lists;

abstract class NavigationItem {
	
	final NavigationItemAdapter adapter;
	final NavigationItemView view;
	final int absoluteIndex;
	
	NavigationItem(final NavigationItemAdapter adapter, final NavigationItemView view, final int absoluteIndex) {
		this.adapter = adapter;
		this.view = view;
		this.absoluteIndex = absoluteIndex;
	}
	
	abstract void onClick(ListView listView);
	
	static void initializeAdapterWithSections(final NavigationItemAdapter adapter) {
		final FragmentNavigationItem firstItem = initializeAdapterWithFirstSection(adapter);
		initializeAdapterWithRemainingSections(adapter);

		adapter.setSelectedItem(firstItem);
		adapter.getNavigationRoot().makeFragmentVisible(firstItem.getCachedOrCreateFragment());
		// TODO set first section as selected	 
		
	}
	
	private static FragmentNavigationItem initializeAdapterWithFirstSection(final NavigationItemAdapter adapter) {
		final FragmentComponentInfo sectionInfo = (FragmentComponentInfo) Sections.SECTION_LIST.get(0);
		final FragmentNavigationItem item = createSectionFragmentItem(sectionInfo, adapter, 0);
		adapter.add(item);
		return item;
	}
	
	private static void initializeAdapterWithRemainingSections(final NavigationItemAdapter adapter) {
		for(int i = 1; i < Sections.SECTION_LIST.size(); i++) {
			final NavigationItem navItem = createSectionItem(adapter, i);
			adapter.add(navItem);
		}
	}
	
	private static NavigationItem createSectionItem(final NavigationItemAdapter adapter, final int index) {
		final ComponentInfo sectionInfo = Sections.SECTION_LIST.get(index);
		if(sectionInfo instanceof GroupComponentInfo)
			return createSectionGroupItem((GroupComponentInfo)sectionInfo, adapter, index);
		else if(sectionInfo instanceof FragmentComponentInfo)
			return createSectionFragmentItem((FragmentComponentInfo)sectionInfo, adapter, index);
		else
			throw new UnsupportedOperationException("Unknown ComponentInfo: " + sectionInfo); //$NON-NLS-1$
	}
	
	private static NavigationItem createSectionGroupItem(final GroupComponentInfo sectionInfo,
			final NavigationItemAdapter adapter, final int index) {
		
		final List<FragmentNavigationItem> children = createChildren(sectionInfo, adapter, index);
		
		final SectionNavigationItemView view = new SectionNavigationItemView(sectionInfo);
		return new GroupNavigationItem(adapter, view, children, index);
	}
	
	private static List<FragmentNavigationItem> createChildren(final GroupComponentInfo sectionInfo,
			final NavigationItemAdapter adapter, final int groupIndex) {
		
		final List<FragmentNavigationItem> children =
				Lists.newArrayListWithCapacity(sectionInfo.getSubSections().size());
		
		for(int i = 0; i < sectionInfo.getSubSections().size(); i++) {
			final FragmentComponentInfo childInfo = sectionInfo.getSubSections().get(i);
			final int childIndex = groupIndex + i + 1;
			final FragmentNavigationItem childItem = createSubSectionFragmentItem(childInfo, adapter, childIndex);
			children.add(childItem);
		}
		
		return children;
	}
	
	private static FragmentNavigationItem createSectionFragmentItem(final FragmentComponentInfo componentInfo,
			final NavigationItemAdapter adapter, final int absoluteIndex) {

		final SectionNavigationItemView view = new SectionNavigationItemView(componentInfo);
		return new FragmentNavigationItem(componentInfo, adapter, view, absoluteIndex);
	}
	
	private static FragmentNavigationItem createSubSectionFragmentItem(final FragmentComponentInfo componentInfo,
			final NavigationItemAdapter adapter, final int absoluteIndex) {

		final SubSectionNavigationItemView view = new SubSectionNavigationItemView(componentInfo);
		return new FragmentNavigationItem(componentInfo, adapter, view, absoluteIndex);
	}
	
}
