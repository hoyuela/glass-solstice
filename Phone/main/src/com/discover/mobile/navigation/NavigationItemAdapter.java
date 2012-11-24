package com.discover.mobile.navigation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.discover.mobile.section.SectionInfo;
import com.discover.mobile.section.Sections;

class NavigationItemAdapter extends ArrayAdapter<NavigationItem> {

	static final int TYPE_SECTION = 0;
	static final int TYPE_SUB_SECTION = TYPE_SECTION + 1;
	
	NavigationItemAdapter(final Context context) {
		super(context, 0);
		
		addSections();
	}
	
	private void addSections() {
		for(final SectionInfo sectionInfo : Sections.SECTIONS)
			add(new SectionNavigationItem(sectionInfo));
	}
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	@Override
	public int getItemViewType(final int position) {
		return getItem(position).getViewType();
	}
	
	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		return getItem(position).getView(this, convertView, parent);
	}
	
	LayoutInflater getLayoutInflater() {
		return (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
}
