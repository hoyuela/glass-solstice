package com.discover.mobile.bank.whatsnew;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.discover.mobile.bank.R;

/**
 * Adapter of the whats new view pager.  
 * This is used to display the data in the view pager.
 * 
 * @author jthornton
 *
 */
public class WhatsNewViewPagerAdapter extends FragmentStatePagerAdapter {

	/**Array containing reference to the drawables to be displayed in the view pager*/
	private final TypedArray drawables; 

	/**Array holding the titles of the view pager*/
	private final String[] titles;

	/**Array holding the content of the view page*/
	private final String[] contents;

	/**
	 * Constructor for the class
	 * @param context - context to get resources from
	 * @param fm - fragment manager used to display the different pages
	 * 
	 * The warning is suppressed because in this case the array cannot be garbage collected
	 * because the reference to the array needs to be maintained so that pages can be
	 * rendered correctly.
	 */
	@SuppressLint("Recycle")
	public WhatsNewViewPagerAdapter(final Context context, final FragmentManager fm) {
		super(fm);
		final Resources res = context.getResources();
		drawables = res.obtainTypedArray(R.array.bank_whats_new_drawables);
		titles = res.getStringArray(R.array.bank_whats_new_titles);
		contents = res.getStringArray(R.array.bank_whats_new_content);
	}

	/**
	 * Get the fragment item
	 * @param position - position to get the item 
	 */
	@Override
	public Fragment getItem(final int position) {
		final Bundle bundle = new Bundle();
		bundle.putInt(WhatsNewViewPagerFragment.DRAWABLE, drawables.getResourceId(position, 0));
		bundle.putString(WhatsNewViewPagerFragment.TITLE, titles[position]);
		bundle.putString(WhatsNewViewPagerFragment.CONTENT, contents[position]);

		final WhatsNewViewPagerFragment fragment = new WhatsNewViewPagerFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	/**
	 * Get the count of total pages in the view pager.
	 * @return the number of pages
	 */
	@Override
	public int getCount() {
		return drawables.length();
	}
}