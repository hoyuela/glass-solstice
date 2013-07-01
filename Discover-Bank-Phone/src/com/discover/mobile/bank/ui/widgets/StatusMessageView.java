package com.discover.mobile.bank.ui.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.animations.Rotate3dAnimation;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.util.TimedViewController;

/**
 * Utility helper class used to display a view for a fixed amount of time in milliseconds, before
 * it's visibility is set from View.VISIBLE to View.GONE.
 * 
 * How to use this class:
 * 
 * Declare the class in the xml layout:
 * <com.discover.mobile.bank.ui.widgets.StatusMessageView
 *       android:id="@+id/status_message"
 *       android:layout_width="wrap_content"
 *     	 android:layout_height="wrap_content"
 *       android:layout_alignParentTop="true"
 *       />
 *	
 * Use the following code to programmatically start the timer that will show the view and then hide
 * it after the elapse time:
 *		final StatusMessageView status = (StatusMessageView)this.getView().findViewById(R.id.status_message);
 *		status.setText(R.string.bank_pmt_deleted);
 *		status.showAndHide(5000);
 *	
 * @author henryoyuela
 *
 */
public class StatusMessageView extends RelativeLayout {
	private boolean applyAnimation = false;
	private static final int ANIM_DURATION = 1500;

	public StatusMessageView(final Context context) {
		super(context);
		
		inflateLayout();
	}
	
	public StatusMessageView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		
		inflateLayout();
	}

	public StatusMessageView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		
		inflateLayout();
	}

	private void inflateLayout() {
		final LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		inflater.inflate(R.layout.bank_status_view, this);
		
		this.setVisibility(View.GONE);
	}
	
	/**
	 * Method used to set the text of the status message that is displayed
	 * 
	 * @param text Resource identifier for the string that is to be displayed in the Status view
	 */
	public void setText(final String text) {
		final TextView statusText = (TextView)this.findViewById(R.id.status_text);
		if(statusText != null) {
			statusText.setText(text);
			setTextBold(true);
		}
	}
	
	public void setTextBold(final boolean shouldTextBold) {
		final TextView statusText = (TextView)this.findViewById(R.id.status_text); 
		if(statusText != null) {
			if(shouldTextBold) {
				statusText.setTypeface(Typeface.DEFAULT_BOLD);
			}else {
				statusText.setTypeface(Typeface.DEFAULT);
			}
		}
	}
	
	public void hideErrorIcon() {
		final View image = findViewById(R.id.status_image);
		if(image != null) {
			image.setVisibility(View.INVISIBLE);
		}
	}
	
	public void showErrorIcon() {
		final View image = findViewById(R.id.status_image);
		if(image != null) {
			image.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * Method used to show the status view for the amount of time specified in delay (in milliseconds).
	 * 
	 * @param delay Specifies the delay in milliseconds to show the status view before setting 
	 * 		  its visibility to "gone".
	 */
	public void showAndHide(final int delay) {
		final TimedViewController tvc = new TimedViewController(this, delay);
		tvc.start();
		
		applyAnimation = true;
	}
	
	@Override
	public void setVisibility(final int visibility) {
		if (visibility != View.VISIBLE && applyAnimation) {
			applyAnimation = false;
			applyAnimation();
		} else {
			super.setVisibility(visibility);
		}
	}

	private void applyAnimation() {
		final AnimationSet animSet = new AnimationSet(false);

		animSet.addAnimation(createTranslateAnimation());
		animSet.addAnimation(createRotationAnimation(0, -90));
		animSet.addAnimation(createAlphaAnimation());


		animSet.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(final Animation arg0) {
				setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(final Animation arg0) {
			}

			@Override
			public void onAnimationStart(final Animation arg0) {
			}

		});

		animSet.setRepeatCount(0);
		this.startAnimation(animSet);
	}

	/**
	 * Setup a new 3D rotation on the container view.
	 * 
	 * @param position
	 *            the item that was clicked to show a picture, or -1 to show the
	 *            list
	 * @param start
	 *            the start angle at which the rotation must begin
	 * @param end
	 *            the end angle of the rotation
	 */
	public Animation createRotationAnimation(final float start, final float end) {

		// Find the center of the container
		final float centerX = this.getMeasuredWidth() / 2.0f;
		final float centerY = this.getMeasuredHeight();


		// Create a new 3D rotation with the supplied parameter
		// The animation listener is used to trigger the next animation
		final Rotate3dAnimation rotation = new Rotate3dAnimation(start, end,
				centerX, centerY, 310.0f, true);
		rotation.setDuration(ANIM_DURATION);
		rotation.setFillAfter(true);
		rotation.setInterpolator(new AccelerateInterpolator());

		return rotation;
	}

	/**
	 * Method used to change the opacity of this view.
	 * 
	 * @return Reference to an animation object that will change the opacity of
	 *         this view.
	 */
	private Animation createAlphaAnimation() {
		final AlphaAnimation alpha = new AlphaAnimation(1.0f, 0.0f);
		alpha.setDuration(ANIM_DURATION);
		return alpha;
	}

	/**
	 * Method used to change the position of Y for the view.
	 * 
	 * @return Reference to the translate animation applied to this view.
	 */
	private Animation createTranslateAnimation() {
		final TranslateAnimation translate = new TranslateAnimation(0.0f, 0.0f,
				0.0f, -1 * getMeasuredHeight());
		translate.setDuration(ANIM_DURATION);
		return translate;
	}
}
