package com.discover.mobile.bank.ui.table;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.discover.mobile.bank.R;


public class ReverseViewPagerListItem extends ViewPagerListItem {
	
	public ReverseViewPagerListItem(final Context context) {
		super(context);
		doSetup();
	}
	public ReverseViewPagerListItem(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		doSetup();
	}
	public ReverseViewPagerListItem(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		doSetup();
	}
	
	protected void doSetup() {
		this.removeAllViews();
		this.addView(getInflatedLayout());
		super.loadViews();
	}
	
	@Override
	protected RelativeLayout getInflatedLayout() {
		return (RelativeLayout)LayoutInflater.from(getContext()).inflate(R.layout.reverse_view_pager_list_item, null);
	}
}
