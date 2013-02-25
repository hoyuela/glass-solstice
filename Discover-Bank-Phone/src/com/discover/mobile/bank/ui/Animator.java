package com.discover.mobile.bank.ui;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout.LayoutParams;

/**
 * Animator class that is a common place for Java animations
 * @author jthornton
 *
 */
public final class Animator {

	/**Default constructor*/
	private Animator(){ }

	/**Interpolated time*/
	private static final int INTERPOLATED_TIME = 20;

	/**Collapse height amount*/
	private static final float COLLAPSE_AMT = .1f;

	/**Expand collapse time*/
	private static final int DURATION = 300;

	/**
	 * Create an expand item
	 * @param v - view to base the expand off
	 * @return the expand animation
	 */
	public static Animation expand(final View v) {
		v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		final int targtetHeight = v.getMeasuredHeight();
		v.getLayoutParams().height = 0;
		v.setVisibility(View.VISIBLE);

		final Animation animation = new Animation(){
			@Override
			protected void applyTransformation(final float interpolatedTime, final Transformation t) {
				v.getLayoutParams().height = (interpolatedTime == INTERPOLATED_TIME)
						? LayoutParams.WRAP_CONTENT : (int)(targtetHeight * interpolatedTime);
				v.requestLayout();
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		// 1dp/ms
		animation.setDuration(DURATION);
		return animation;

	}

	/**
	 * Create a collapse animation
	 * @param v - view to base the animation off of
	 * @return the collapse animation
	 */
	public static Animation collapse(final View v) {
		final int initialHeight = v.getMeasuredHeight();

		final Animation animation = new Animation(){
			@Override
			protected void applyTransformation(final float interpolatedTime, final Transformation t) {
				if(interpolatedTime == DURATION){
					v.setVisibility(View.INVISIBLE);
				}else{
					v.getLayoutParams().height = (initialHeight - (int)(initialHeight * COLLAPSE_AMT));
					v.requestLayout();
				}
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		animation.setDuration(DURATION);
		return animation;
	}
}
