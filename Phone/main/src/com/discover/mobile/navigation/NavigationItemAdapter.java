package com.discover.mobile.navigation;

import roboguice.inject.ContextSingleton;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.discover.mobile.section.SectionInfo;
import com.discover.mobile.section.Sections;
import com.google.inject.Inject;

@ContextSingleton
class NavigationItemAdapter extends ArrayAdapter<NavigationItem> {
	
	static final int TYPE_SECTION = 0;
	static final int TYPE_SUB_SECTION = TYPE_SECTION + 1;
	static final int TYPE_COUNT = TYPE_SUB_SECTION + 1;
	
	@Inject
	private LayoutInflater layoutInflater;
	
	private SectionNavigationItem expandedSection;
	
	@Inject
	NavigationItemAdapter(final Context context) {
		super(context, 0);
		
		addSections();
	}
	
	private void addSections() {
		for(int i = 0; i < Sections.SECTIONS.size(); i++) {
			final SectionInfo sectionInfo = Sections.SECTIONS.get(i);
			add(new SectionNavigationItem(this, sectionInfo, i));
		}
	}
	
	@Override
	public int getViewTypeCount() {
		return TYPE_COUNT;
	}
	
	@Override
	public int getItemViewType(final int position) {
		return getItem(position).getViewType();
	}
	
	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		return getItem(position).getView(convertView);
	}
	
	LayoutInflater getLayoutInflater() {
		return layoutInflater;
	}

	SectionNavigationItem getExpandedSection() {
		return expandedSection;
	}

	void setExpandedSection(final SectionNavigationItem expandedSection) {
		this.expandedSection = expandedSection;
	}
	
	NavigationRoot getNavigationRoot() {
		return (NavigationRoot) getContext();
	}
	
	void onListItemClick(final ListView listView, final int position) {
		getItem(position).onClick(listView);
	}
	
}
