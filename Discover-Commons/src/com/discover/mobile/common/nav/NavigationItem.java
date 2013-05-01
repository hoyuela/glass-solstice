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
/**
 * This is the base navigation item class. In this class it initializes the menu adapter with the correction options based on 
 * whether or not the objects are GroupComponentInfo's or Click/Fragment ComponentInfos. 
 * 
 * @author ajleeds
 *
 */
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
				
		/**If home fragment is not null then show home fragment **/
		if( homeFragment != null ) {		
			adapter.getNavigationRoot().makeFragmentVisible(homeFragment);
		}

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
	 * Creates the sections under the main titles depending on if the section item is a Group Item or not. 
	 * If its not a group item it will set up the item with its correct action. If it is a group item it
	 * will create a list of child items to be shown underneath. 
	 * 
	 * @param adapter
	 * @param index
	 * @return
	 */
	private static NavigationItem createSectionItem(final NavigationItemAdapter adapter, final int index) {
		final ComponentInfo sectionInfo = section.get(index);
		if(sectionInfo instanceof GroupComponentInfo){
			return createSectionGroupItem((GroupComponentInfo)sectionInfo, adapter, index);
		}else if(sectionInfo instanceof FragmentComponentInfo){
			return createSectionFragmentItem((FragmentComponentInfo)sectionInfo, adapter, index);
		}else if (sectionInfo instanceof ClickComponentInfo){
			return createSectionClickItem((ClickComponentInfo)sectionInfo, adapter, index);
		}else{
			throw new UnsupportedOperationException("Unknown ComponentInfo: " + sectionInfo); //$NON-NLS-1$
		}
	}

	/**
	 * Creates the the main menu options. Calls create children which sets up the subsections.
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
	 * has an onClick that is what handles making the fragment visible. This is the action that is used
	 * for the main menu options. 
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
	 * has an onClick that is what handles making the fragment visible. This is the action that is used for the 
	 * sub menu sections. 
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
	 * Essentially this is the onclick action for the main menu options. The Click Navigation Item
	 * will accept an onclick listener that the onclick will call. This is for the main menu options. 
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
	 * Essentially this is the onclick action for the sub section menu options. The Click Navigation Item
	 * will accept an onclick listener that the onclick will call. This is for the sub menu options. 
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

	/**
	 * Shows the sub menu sctions. 
	 */
	void show() {
		adapter.insert(this, absoluteIndex);
	}

	/**
	 * Hides the sub menu sections. 
	 */
	void hide() {
		adapter.remove(this);
	}

}
