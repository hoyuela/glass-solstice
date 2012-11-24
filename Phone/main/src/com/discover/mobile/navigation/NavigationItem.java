package com.discover.mobile.navigation;

import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.discover.mobile.R;

abstract class NavigationItem {
	
	final NavigationItemAdapter navigationItemAdapter;
	final int absoluteIndex;
	
	private final int viewResource;
	
	NavigationItem(final NavigationItemAdapter navigationItemAdapter, final int viewResource, final int absoluteIndex) {
		this.navigationItemAdapter = navigationItemAdapter;
		this.viewResource = viewResource;
		this.absoluteIndex = absoluteIndex;
	}
	
	abstract int getViewType();
	abstract void customizeItemView(View view, TextView title);
	abstract void onClick(ListView listView);
	
	final View getView(final View convertView) {
		View view;
		if(convertView == null)
			view = navigationItemAdapter.getLayoutInflater().inflate(viewResource, null);
		else
			view = convertView;
		
		final TextView title = (TextView) view.findViewById(R.id.title);
		
		customizeItemView(view, title);
		
		return view;
	}
	
}
