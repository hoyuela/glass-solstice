package com.discover.mobile.common.nav;

import roboguice.inject.ContextSingleton;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.inject.Inject;

@ContextSingleton
class NavigationItemAdapter extends ArrayAdapter<NavigationItem> {
	
	static final int TYPE_SECTION = 0;
	static final int TYPE_SUB_SECTION = TYPE_SECTION + 1;
	static final int TYPE_COUNT = TYPE_SUB_SECTION + 1;
	
	@Inject
	private LayoutInflater layoutInflater;
	
	private NavigationItem selectedItem;
	
	
	@Inject
	NavigationItemAdapter(final Context context) {
		super(context, 0);
	}
	
	@Override
	public int getViewTypeCount() {
		return TYPE_COUNT;
	}
	
	@Override
	public int getItemViewType(final int position) {
		return getItem(position).view.getViewType();
	}
	
	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		return getItem(position).view.getView(convertView, layoutInflater, position);
	}
	
	NavigationItem getSelectedItem() {
		return selectedItem;
	}
	
	void setSelectedItem(final NavigationItem selectedItem) {
		this.selectedItem = selectedItem;
	}
	
	NavigationRoot getNavigationRoot() {
		return (NavigationRoot) getContext();
	}
	
}
