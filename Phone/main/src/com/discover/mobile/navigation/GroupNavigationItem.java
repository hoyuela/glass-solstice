package com.discover.mobile.navigation;

import java.util.List;

import android.widget.ListView;

/**
 * Responder controls a group of sub-items.
 */
final class GroupNavigationItem extends NavigationItem {

	private final List<FragmentNavigationItem> children;

	private boolean expanded;

	GroupNavigationItem(final NavigationItemAdapter navigationItemAdapter, final NavigationItemView view,
			final List<FragmentNavigationItem> children, final int absoluteIndex) {

		super(navigationItemAdapter, view, absoluteIndex);

		this.children = children;
	}

	@Override
	void onClick(final ListView listView) {
		if(expanded) {
			collapse();
			adapter.setSelectedItem(null);
		} else {
			expand();
			adapter.setSelectedItem(this);
		}
	}

	private void expand() {
		if(expanded)
			return;

		final NavigationItem selectedItem = adapter.getSelectedItem();
		if(selectedItem != null && selectedItem instanceof GroupNavigationItem)
			((GroupNavigationItem)selectedItem).collapse();

		for(final FragmentNavigationItem child : children)
			child.show();

		expanded = true;
	}

	private void collapse() {
		if(!expanded)
			return;

		for(final FragmentNavigationItem child : children)
			child.hide();

		expanded = false;
	}

}
