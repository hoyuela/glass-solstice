package com.discover.mobile.common.nav;

import java.util.List;

import android.view.View;
import android.widget.ListView;

/**
 * Responder controls a group of sub-items.
 */
final class GroupNavigationItem extends NavigationItem {

	private final List<NavigationItem> children;

	private boolean expanded;

	GroupNavigationItem(final NavigationItemAdapter navigationItemAdapter, final NavigationItemView view,
			final List<NavigationItem> children, final int absoluteIndex) {

		super(navigationItemAdapter, view, absoluteIndex);
		this.children = children;
	}

	@Override
	void onClick(final ListView listView, final View clickedView) {		
		if(expanded) {
			collapse();
		} else {
			expand();
		}
	}

	public void expand() {
		if(expanded) {
			return;
		}
		final NavigationItem selectedItem = adapter.getSelectedItem();
		if(selectedItem != null && selectedItem instanceof GroupNavigationItem) {
			((GroupNavigationItem)selectedItem).collapse();
		}

		for(final NavigationItem child : children) {
			child.show();
		}
		expanded = true;
		NavigationIndex.setIndex(absoluteIndex);
		adapter.setSelectedItem(this);
	}

	public void collapse() {
		if(!expanded) {
			return;
		}
		for(final NavigationItem child : children) {
			child.hide();
		}

		adapter.setSelectedItem(null);
		NavigationIndex.setIndex(-1);
		expanded = false;
	}

}
