package com.discover.mobile.bank.ui.table;

import android.content.Context;
import android.util.AttributeSet;

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
	}
	public ViewPagerListItemWithImage(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}
	public ViewPagerListItemWithImage(final Context context, final AttributeSet attrs,
			final int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected int getItemLayout() {
		return R.layout.view_pager_list_item_with_image;
	}
}
