package com.discover.mobile.bank.ui;

import java.lang.reflect.Method;

import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Animation that either expands or collapses a view by sliding it down to make it visible. Or by sliding it up so it
 * will hide. It will look like it slides behind the view above.
 * 
 * @auther hoyuela
 */
public class ExpandCollapseAnimation extends Animation {
	final View mView;
	final boolean mExpand;
	final int mInitialHeight;

	/**
	 * Initializes expand collapse animation..
	 * 
	 * @param view
	 *            The view to animate
	 * @param expand
	 *            True to expand the view, false to collapse
	 */
	public ExpandCollapseAnimation(final View v, final boolean expand, final int duration) {

		mView = v;
		mExpand = expand;

		/** Remember the initial height of the view */
		mInitialHeight = getViewMeasuredHeight();

		/** Set the target height based on whether it is being collapsed or expanded */
		if (expand) {
			v.getLayoutParams().height = 0;
		} else {
			v.getLayoutParams().height = mInitialHeight;
		}

		v.setVisibility(View.VISIBLE);

		/** Set how long the animation will take */
		this.setDuration(duration);

	}
	
	/**
	 * Method used to retrieve the measured height of the View that is being collapsed or expanded by this animation
	 * class.
	 * 
	 * @return Measured height of the View stored in mView.
	 */
	public int getViewMeasuredHeight() {
		/** Measure the maximum height of the view */
		try {
			final Method m = mView.getClass().getDeclaredMethod("onMeasure", int.class, int.class);
			m.setAccessible(true);
			m.invoke(mView, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
					MeasureSpec.makeMeasureSpec(((View) mView.getParent()).getMeasuredWidth(), MeasureSpec.AT_MOST));
		} catch (final Exception e) {
			e.printStackTrace();
		}
		
		return mView.getMeasuredHeight();		
	}

	@Override
	protected void applyTransformation(final float interpolatedTime, final Transformation t) {
		int newHeight = 0;
		if (mExpand) {
			newHeight = (int) (mInitialHeight * interpolatedTime);
		} else {
			newHeight = (int) (mInitialHeight * (1 - interpolatedTime));
		}
		mView.getLayoutParams().height = newHeight;
		mView.requestLayout();
		if (interpolatedTime == 1 && !mExpand)
			mView.setVisibility(View.GONE);

	}

	@Override
	public boolean willChangeBounds() {
		return true;
	}
}