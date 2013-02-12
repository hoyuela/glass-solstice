package com.discover.mobile.common.nav;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.discover.mobile.common.nav.section.ClickComponentInfo;

public class ClickNavigationItem extends NavigationItem {

	final OnClickListener listener;
	public ClickNavigationItem(final ClickComponentInfo clickComponentInfo, final NavigationItemAdapter adapter, final NavigationItemView view,
			final int absoluteIndex) {
		super(adapter, view, absoluteIndex);
		listener = clickComponentInfo.getOnClickListener();
	}

	@Override
	void onClick(final ListView listView, final View clickedView) {
		clickedView.setSelected(true);
		listener.onClick(clickedView);
	}

}