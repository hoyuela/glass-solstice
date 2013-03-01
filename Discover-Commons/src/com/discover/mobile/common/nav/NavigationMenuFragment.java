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

	@Override
	public void onStart() {
		super.onStart();
		this.getListView().performItemClick(getListView().getAdapter().getView(NavigationIndex.getMainIndex(), null, null), NavigationIndex.getMainIndex(), NavigationIndex.getMainIndex());
	}

	public void setItemSelected(final int group, final int subSection){
		if(getListView().getAdapter().getItem(NavigationIndex.getMainIndex()) instanceof GroupNavigationItem){
			final GroupNavigationItem currentGroup = 
					(GroupNavigationItem)getListView().getAdapter().getItem(NavigationIndex.getMainIndex());
			currentGroup.collapse();
		}
		if(getListView().getAdapter().getItem(group) instanceof GroupNavigationItem){
			final GroupNavigationItem newGroup = (GroupNavigationItem)getListView().getAdapter().getItem(group);
			newGroup.expand();
		}
		NavigationIndex.setIndex(group);
		NavigationIndex.setSubIndex(subSection+group);
		navigationItemAdapter.notifyDataSetChanged();
	}
}
