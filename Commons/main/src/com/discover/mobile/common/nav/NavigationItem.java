package com.discover.mobile.common.nav;

import java.util.List;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ListView;

import com.discover.mobile.common.nav.section.ClickComponentInfo;
import com.discover.mobile.common.nav.section.ComponentInfo;
import com.discover.mobile.common.nav.section.FragmentComponentInfo;
import com.discover.mobile.common.nav.section.GroupComponentInfo;
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

	abstract void onClick(ListView listView, View clickedView);

	/**
	 * Sets up the adapter and make the home fragment the first visible fragment when logging in.  
	 * @param adapter
	 * @param sectionInfo
	 */
	public static void initializeAdapterWithSections(final NavigationItemAdapter adapter, final ImmutableList<ComponentInfo> sectionInfo, final Fragment homeFragment) {
		section = sectionInfo;
		initializeAdapterWithSections(adapter);
		adapter.getNavigationRoot().makeFragmentVisible(homeFragment);
		// TODO set first section as selected	 

	}

	/**
	 * Sets up the menu with the main menu options as well as the sections underneath. 
	 * @param adapter
	 */
	private static void initializeAdapterWithSections(final NavigationItemAdapter adapter) {
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
		else if (sectionInfo instanceof ClickComponentInfo)
			return createSectionClickItem((ClickComponentInfo)sectionInfo, adapter, index);
		else
			throw new UnsupportedOperationException("Unknown ComponentInfo: " + sectionInfo); //$NON-NLS-1$
	}

	/**
	 * Creates the listview underneath the main menu options. Calls create children which sets up the subsections.
	 * @param sectionInfo
	 * @param adapter
	 * @param index
	 * @return
	 */
	private static NavigationItem createSectionGroupItem(final GroupComponentInfo sectionInfo,
			final NavigationItemAdapter adapter, final int index) {

		final List<NavigationItem> children = createChildren(sectionInfo, adapter, index);

		final SectionNavigationItemView view = new SectionNavigationItemView(sectionInfo);
		return new GroupNavigationItem(adapter, view, children, index);
	}

	/**
	 * Creates the sub sections of the main menu listview.
	 * 
	 * @param sectionInfo
	 * @param adapter
	 * @param groupIndex
	 * @return
	 */
	private static List<NavigationItem> createChildren(final GroupComponentInfo sectionInfo,
			final NavigationItemAdapter adapter, final int groupIndex) {

		final List<NavigationItem> children =
				Lists.newArrayListWithCapacity(sectionInfo.getSubSections().size());

		for(int i = 0; i < sectionInfo.getSubSections().size(); i++) {
			final ComponentInfo childInfo = sectionInfo.getSubSections().get(i);
			final int childIndex = groupIndex + i + 1;
			if (childInfo instanceof FragmentComponentInfo){
				final FragmentNavigationItem childItem  = createSubSectionFragmentItem((FragmentComponentInfo) childInfo, adapter, childIndex);
				children.add(childItem);
			}else if (childInfo instanceof ClickComponentInfo){
				final ClickNavigationItem childClickItem = createSubSectionClickItem((ClickComponentInfo) childInfo, adapter, childIndex);
				children.add(childClickItem);
			}

		}

		return children;
	}

	/**
	 * Essentially this is the onclick action for the main menu options. The Fragment Navigation Item
	 * has an onClick that is what handles making the fragment visible.
	 * 
	 * @param componentInfo
	 * @param adapter
	 * @param absoluteIndex
	 * @return
	 */
	private static FragmentNavigationItem createSectionFragmentItem(final FragmentComponentInfo componentInfo,
			final NavigationItemAdapter adapter, final int absoluteIndex) {

		final SectionNavigationItemView view = new SectionNavigationItemView(componentInfo);
		return new FragmentNavigationItem(componentInfo, adapter, view, absoluteIndex);
	}

	/**
	 * Essentially this is the onclick action for the sub section menu options. The Fragment Navigation Item
	 * has an onClick that is what handles making the fragment visible.
	 * 
	 * @param componentInfo
	 * @param adapter
	 * @param absoluteIndex
	 * @return
	 */
	private static FragmentNavigationItem createSubSectionFragmentItem(final FragmentComponentInfo componentInfo,
			final NavigationItemAdapter adapter, final int absoluteIndex) {

		final SubSectionNavigationItemView view = new SubSectionNavigationItemView(componentInfo);
		return new FragmentNavigationItem(componentInfo, adapter, view, absoluteIndex);
	}

	/**
	 * Essentially this is the onclick action for the main menu options. The Fragment Navigation Item
	 * has an onClick that is what handles making the fragment visible.
	 * 
	 * @param componentInfo
	 * @param adapter
	 * @param absoluteIndex
	 * @return
	 */
	private static ClickNavigationItem createSectionClickItem(final ClickComponentInfo componentInfo,
			final NavigationItemAdapter adapter, final int absoluteIndex) {

		final SectionNavigationItemView view = new SectionNavigationItemView(componentInfo);
		return new ClickNavigationItem(componentInfo, adapter, view, absoluteIndex);
	}

	/**
	 * Essentially this is the onclick action for the sub section menu options. The Fragment Navigation Item
	 * has an onClick that is what handles making the fragment visible.
	 * 
	 * @param componentInfo
	 * @param adapter
	 * @param absoluteIndex
	 * @return
	 */
	private static ClickNavigationItem createSubSectionClickItem(final ClickComponentInfo componentInfo,
			final NavigationItemAdapter adapter, final int absoluteIndex) {

		final SubSectionNavigationItemView view = new SubSectionNavigationItemView(componentInfo);
		return new ClickNavigationItem(componentInfo, adapter, view, absoluteIndex);
	}

	void show() {
		adapter.insert(this, absoluteIndex);

		// TODO
	}

	void hide() {
		adapter.remove(this);

		// TODO
	}

}
