package com.discover.mobile.bank.ui.table;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.discover.mobile.bank.R;

/**
 * This layout is used to have a the top and middle labels layout swapped so the larger bold text is the top label
 * and the smaller grey text is the middle label.  The primary current usage for this is select account transfer funds.
 * 
 * @author stephenfarr
 *
 */

public class ReverseViewPagerListItem extends ViewPagerListItem {
	
	//------------------------------ Constructors ------------------------------
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

	//------------------------------ Overridden Methods ------------------------------
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
