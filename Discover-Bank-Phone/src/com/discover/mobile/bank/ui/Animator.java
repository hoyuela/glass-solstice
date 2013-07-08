package com.discover.mobile.bank.ui;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import com.discover.mobile.bank.R;

/**
 * Animator class that is a common place for Java animations
 * @author jthornton
 *
 */
public final class Animator {

	/**Default constructor*/
	private Animator(){ }

	/**
	 * Animation used to slide the layout to the left off the screen and at the end hide the layout
	 * @param context - context
	 * @param view - view to slide
	 * @return the Animation used to slide the layout to the left off the screen and at the end hide the layout
	 */
	public static Animation createSlideToLeftAnimation(final Context context, final View view){
		final Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_left);
		animation.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(final Animation animation) {
				view.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(final Animation animation) { }

			@Override
			public void onAnimationStart(final Animation animation) { }

		});
		return animation;
	}

	/**
	 * Animation used to slide the layout to the right on to the screen
	 * @param context - context
	 * @param view - view to slide
	 * @return Animation used to slide the layout to the right on to the screen
	 */
	public static Animation createSlideToRightAnimation(final Context context, final View view){
		final Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_right);
		animation.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(final Animation animation) { }

			@Override
			public void onAnimationRepeat(final Animation animation) { }

			@Override
			public void onAnimationStart(final Animation animation) {
				view.setVisibility(View.VISIBLE);
			}

		});
		return animation;
	}

	/**
	 * Method used to create a rotation animation that can be applied to a view. At the end of the rotation the position
	 * of the view is maintained.
	 * 
	 * @param up
	 *            If true rotates the view from 0 to 180 degrees otherwise rotates 180 to 0 degrees.
	 * 
	 * @return Reference to constructed animation.
	 */
	public static Animation createRotationAnimation(final boolean up, final long duration) {

		final RotateAnimation rAnim;
		if (up) {
			rAnim = new RotateAnimation(0.0f, 180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		} else {
			rAnim = new RotateAnimation(180.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		}

		rAnim.setRepeatCount(0);
		rAnim.setInterpolator(new LinearInterpolator());
		rAnim.setDuration(duration);
		rAnim.setFillAfter(true);

		return rAnim;
	}
}

