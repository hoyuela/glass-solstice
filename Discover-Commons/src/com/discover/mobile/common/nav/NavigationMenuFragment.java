package com.discover.mobile.common.nav;

import android.view.View;
import android.widget.ListView;

import com.discover.mobile.common.RoboSherlockListFragment;
import com.google.inject.Inject;

public abstract class NavigationMenuFragment extends RoboSherlockListFragment {

	@Inject
	protected NavigationItemAdapter navigationItemAdapter;

	@Override
	public void onListItemClick(final ListView listView, final View clickedView, final int position, final long id) {
		super.onListItemClick(listView, clickedView, position, id);
		navigationItemAdapter.getItem(position).onClick(listView, clickedView);
	}

	/**
	 * Method used to select a menu item group title and its corresponding
	 * section if applicable. The group must be within the limit of number of
	 * menu groups in the menu. If the menu item referred to via the group index
	 * specified is not a GroupNavigationItem, then the subsection argument is
	 * ignored.
	 * 
	 * @param group
	 *            Index referring to the menu item or main group menu item
	 *            selected.
	 * @param subSection
	 *            Index referring to the sub section within the group menu
	 *            selected. Ignored if not within the boundaries of the menu
	 *            group.
	 */
	public void setItemSelected(final int group, final int subSection) {

		final int mainIndex = NavigationIndex.getMainIndex();

		// Make sure that the mainIndex is a valid index in the list to collapse
		// the current menu item selected
		if(getView() != null) {
			if (mainIndex >= 0 && getListView().getAdapter().getItem(mainIndex) instanceof GroupNavigationItem) {
				final GroupNavigationItem currentGroup = (GroupNavigationItem) getListView().getAdapter().getItem(mainIndex);
				currentGroup.collapse();
			}
			
	
			final int maxIndex = getListView().getAdapter().getCount();
	
			// Make sure the new group being selected is within the boundaries of
			// the list of menu groups
			if (group >= 0 && group < maxIndex) {
				// Set the new selected menu group
				NavigationIndex.setIndex(group);
	
				// Expand the new menu if it is a menu group with sections
				if (getListView().getAdapter().getItem(group) instanceof GroupNavigationItem) {
					final GroupNavigationItem newGroup = (GroupNavigationItem) getListView().getAdapter().getItem(group);
					newGroup.expand();
	
					// Set the new selected section in the menu
					NavigationIndex.setSubIndex(subSection + group);
				}
				
				//Defect ID:97724 by Cognizant
				else if(getListView().getAdapter().getItem(group) instanceof FragmentNavigationItem)
				{
				    // This ElseIf Block written by Cognizant
		            // Setting 0 as Sub Index if the Menu is not having sub section to fix HighLight issue for Card Side
				    NavigationIndex.setSubIndex(0);
				}
				//Defect ID:97724
			}
		}
	}
	
	public void onPushCountUpdate(final int newCount)
	{
		if(navigationItemAdapter != null)
		{
			navigationItemAdapter.setPushCount(newCount);
			navigationItemAdapter.notifyDataSetChanged();
		}
	}
}
