package com.discover.mobile.common.nav;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.discover.mobile.common.nav.section.ComponentInfo;

abstract class NavigationItemView {

	final ComponentInfo componentInfo;

	private final int viewResource;

	NavigationItemView(final int viewResource, final ComponentInfo componentInfo) {
		this.viewResource = viewResource;
		this.componentInfo = componentInfo;
	}

	abstract int getViewType();
	abstract void customizeView(View view, TextView titleView);

	final View getView(final View convertView, final LayoutInflater inflater) {
		View view;

		if(convertView == null){
			view = inflater.inflate(viewResource, null);
		}else{
			view = convertView;
		}

		final TextView titleView = (TextView) view.findViewById(com.discover.mobile.common.R.id.title);
		titleView.setText(componentInfo.getTitleResource());

		customizeView(view, titleView);

		return view;
	}

}
