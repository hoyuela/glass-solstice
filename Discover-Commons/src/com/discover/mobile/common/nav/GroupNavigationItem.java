package com.discover.mobile.common.nav;

import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

/**
 * Responder controls a group of sub-items.
 */
final class GroupNavigationItem extends NavigationItem {

	private final List<NavigationItem> children;

	private boolean expanded;
	
	/** true when this item's ComponentInfo has set its own listener. */
	private boolean overrideClick;

	GroupNavigationItem(final NavigationItemAdapter navigationItemAdapter, final NavigationItemView view,
			final List<NavigationItem> children, final int absoluteIndex) {

		super(navigationItemAdapter, view, absoluteIndex);
		this.children = children;
		overrideClick =  NavigationItem.section.get(absoluteIndex).getPushClick() != null;
	}

	@Override
	void onClick(final ListView listView, final View clickedView) {	
		
		// Determine if click listener was overridden
		if (overrideClick) {
			onClickOverride(clickedView);
			return;
		}
		
		// Use default expand/collapse commands
		if(expanded) {
			collapse();
		} else {
			expand();
		}
	}
	
	/** Performs the onClick method associated with the ComponentInfo and updates the adapter accordingly. */
	private void onClickOverride(final View clickedView) {
		if (!expanded && adapter.getSelectedItem() instanceof GroupNavigationItem) {
			// Another group is expanded -- collapse it
			((GroupNavigationItem) adapter.getSelectedItem()).collapse();
		}

		// Initiate the click listener associated with the ComponentInfo.
		OnClickListener groupListener = NavigationItem.section.get(absoluteIndex).getPushClick();
		groupListener.onClick(clickedView);
		
		// Update the index and adapter
		NavigationIndex.setIndex(absoluteIndex);
		adapter.setSelectedItem(this);
	}

	public void expand() {
		if(expanded) {
			return;
		}
		final NavigationItem selectedItem = adapter.getSelectedItem();
		if(selectedItem instanceof GroupNavigationItem) {
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

		// Save the current group section being highlighted
		final int mainIndex = NavigationIndex.getMainIndex();

		// Set Index to -1 to store the current state of the menu into the previous state of the menu
		// So when the menu is expanded it will be restored to the previous state.
		NavigationIndex.setIndex(-1);

		// Set the mainIndex so the group section is still highlighted when collapsed
		NavigationIndex.setIndex(mainIndex);
		expanded = false;
	}

}
