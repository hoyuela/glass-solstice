package com.discover.mobile.common.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.discover.mobile.common.BaseFragment;

/**
 * ExtendingScrollView is a scrollview that will detect when the bottom of a
 * screen is reached. Belonging to this class is a reference to a BaseFragment.
 * Implementing Fragments should both override
 * {@code BaseFragment.scrollViewBottomReached()} and call
 * {@code ExtendingScrollView.attachFragment(BaseFragment this)} during the
 * view's creation.
 * 
 * @author Samuel Frank Smith
 * 
 */
public class ExtendingScrollView extends ScrollView {

	/** The Fragment for which scrollViewBottomReached() will need to be called. */
	private BaseFragment a;

	/** A flag that helps determine if we already reached the bottom once. */
	private boolean justReached;

	/**
	 * An additional value used to tell if we've scrolled at all since last
	 * callback.
	 */
	private int prevScrollY;

	public ExtendingScrollView(Context context, AttributeSet as) {
		super(context, as);

		justReached = false;
		prevScrollY = -1;
	}

	@Override
	protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX,
			boolean clampedY) {

		// Bottom has been reached its second time.
		if (clampedY && a != null && justReached) {
			a.scrollViewBottomReached();
			justReached = false;

			// Bottom has been reached its first time.
		} else if (clampedY && scrollY > 0 && prevScrollY != scrollY) {
			justReached = true;
		}
		prevScrollY = scrollY;

		super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
	}

	/**
	 * Provides a reference to the BaseFragment in which this view is contained.
	 * This is later used to tell the BaseFragment when the scrollview reaches
	 * the bottom of the screen.
	 * 
	 * @param a
	 */
	public void attachFragment(BaseFragment a) {
		this.a = a;
	}

}
