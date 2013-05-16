package com.discover.mobile.common.nav;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.discover.mobile.common.R;
import com.discover.mobile.common.nav.section.ComponentInfo;

/**
 * Naviation Item view class is what the listvie adapter will call in the getView method. 
 * 
 * @author ajleeds
 *
 */
abstract class NavigationItemView {

	final ComponentInfo componentInfo;

	private final int viewResource;
	private int pushCount;
	
	NavigationItemView(final int viewResource, final ComponentInfo componentInfo) {
		this.viewResource = viewResource;
		this.componentInfo = componentInfo;
	}

	abstract int getViewType();

	abstract void customizeView(View view, TextView titleView, boolean selected);

	final View getView(final View convertView, final LayoutInflater inflater, final int position) {
		View view;

		if (convertView == null) {
			view = inflater.inflate(viewResource, null);
		} else {
			view = convertView;
		}

		final TextView titleView = (TextView) view
				.findViewById(com.discover.mobile.common.R.id.title);
		final ImageView externalLink = (ImageView) view
				.findViewById(R.id.external);
		
		final TextView pushCountView = (TextView) view.findViewById(R.id.push_countTV);
		titleView.setText(componentInfo.getTitleResource());
		
		if (componentInfo.getIsExternalLink()) {
			externalLink.setVisibility(View.VISIBLE);
		} else {
			externalLink.setVisibility(View.GONE);
		}
		
		if(componentInfo.isPushCountAvailable())
		{
			if(getLatestPushCount() > 99)
			{
				pushCountView.setText("99+");
				pushCountView.setVisibility(View.VISIBLE);
			}
			else
			{
				if(getLatestPushCount() > 0)
				{
					pushCountView.setText(""+getLatestPushCount());
					pushCountView.setVisibility(View.VISIBLE);
				}
				else
				{
					pushCountView.setVisibility(View.GONE);
				}
			}
		}
		else
		{
			pushCountView.setVisibility(View.GONE);
		}
		
		pushCountView.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				if(componentInfo.getPushClick() != null)
				{
					componentInfo.getPushClick().onClick(v);
				}
				
			}
		});
		boolean selected = false;
		
		if (getViewType() == NavigationItemAdapter.TYPE_SECTION && position == NavigationIndex.getMainIndex()){
			selected = true;
		}
		
		if (getViewType() == NavigationItemAdapter.TYPE_SUB_SECTION && position == NavigationIndex.getSubIndex()){
			selected = true;
		}
		
		customizeView(view, titleView, selected);

		return view;
	}
	
	public int getLatestPushCount()
	{
		return pushCount;
	}
	
	public void setPushCount(int pushCount)
	{
		this.pushCount = pushCount;
	}
}
