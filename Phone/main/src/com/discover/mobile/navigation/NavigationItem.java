package com.discover.mobile.navigation;

import android.view.View;
import android.view.ViewGroup;

abstract class NavigationItem {
	
	abstract int getViewType();
	abstract View getView(NavigationItemAdapter sectionAdapter, View convertView, ViewGroup parent);
	
}
