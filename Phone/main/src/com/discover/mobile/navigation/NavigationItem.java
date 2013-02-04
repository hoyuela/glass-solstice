package com.discover.mobile.navigation;

import java.util.List;

import android.support.v4.app.Fragment;
import android.widget.ListView;

import com.discover.mobile.section.ComponentInfo;
import com.discover.mobile.section.FragmentComponentInfo;
import com.discover.mobile.section.GroupComponentInfo;
import com.discover.mobile.section.home.HomeSummaryFragment;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public abstract class NavigationItem {
	
	final NavigationItemAdapter adapter;
	final NavigationItemView view;
	final int absoluteIndex;
	static ImmutableList<ComponentInfo> section;
	
	NavigationItem(final NavigationItemAdapter adapter, final NavigationItemView view, final int absoluteIndex) {
		this.adapter = adapter;
		this.view = view;
		this.absoluteIndex = absoluteIndex;
	}
	
	abstract void onClick(ListView listView);
	
	/**
	 * Sets up the adapter and make the home fragment the first visible fragment when logging in.  
	 * @param adapter
	 * @param sectionInfo
	 */
	public static void initializeAdapterWithSections(final NavigationItemAdapter adapter, ImmutableList<ComponentInfo> sectionInfo) {
		section = sectionInfo;
		initializeAdapterWithRemainingSections(adapter);
		Fragment homeFragment = new HomeSummaryFragment();
		adapter.getNavigationRoot().makeFragmentVisible(homeFragment);
		// TODO set first section as selected	 
		
	}
	
	/**
	 * Sets up the menu with the main menu options as well as the sections underneath. 
	 * @param adapter
	 */
	private static void initializeAdapterWithRemainingSections(final NavigationItemAdapter adapter) {
		for(int i = 0; i < section.size(); i++) {
			final NavigationItem navItem = createSectionItem(adapter, i);
			adapter.add(navItem);
		}
	}
	
	/**
	 * Creates the sections under the main titles
	 * @param adapter
	 * @param index
	 * @return
	 */
	private static NavigationItem createSectionItem(final NavigationItemAdapter adapter, final int index) {
		final ComponentInfo sectionInfo = section.get(index);
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
