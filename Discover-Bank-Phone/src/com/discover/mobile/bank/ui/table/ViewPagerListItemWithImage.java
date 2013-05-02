package com.discover.mobile.bank.ui.table;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.discover.mobile.bank.R;


/**
 * A list item to show inside of a ViewPager detail item.
 * Contains 3 text labels and a divider line.
 * 
 * @author scottseward
 *
 */
public class ViewPagerListItemWithImage extends ViewPagerListItem {
	public ViewPagerListItemWithImage(final Context context) {
		super(context);
		doSetup();
	}
	
	public ViewPagerListItemWithImage(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		doSetup();
	}
	
	public ViewPagerListItemWithImage(final Context context, final AttributeSet attrs,
			final int defStyle) {
		super(context, attrs, defStyle);
		doSetup();
	}
	
	private void doSetup() {
		this.removeAllViews();
		this.addView(getInflatedLayout());
		super.loadViews();
	}
	
	private RelativeLayout getInflatedLayout() {
		return (RelativeLayout)LayoutInflater.from(getContext()).inflate(R.layout.view_pager_list_item_with_image, null);
	}
}
