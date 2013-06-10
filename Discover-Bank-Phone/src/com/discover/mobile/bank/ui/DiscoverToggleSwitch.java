package com.discover.mobile.bank.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.CheckBox;

import com.discover.mobile.bank.R;

/**
 * An animated toggle switch used on the Login page.
 * 
 * Works by drawing a custom layered canvas that includes a mask so that, in effect, there is a small
 * visible window that exposes half of the underlying image, which slides underneath the window and gives the
 * impression of an animated sliding toggle switch.
 * 
 * @author scottseward
 *
 */
public class DiscoverToggleSwitch extends CheckBox {
	/**
	 * The mask for the switch. This allows the underlying switch to be only partially visible.
	 */
	private final Bitmap mask = BitmapFactory.decodeResource(getResources(), R.drawable.switch_mask);
	
	/**
	 * The two switch bitmaps that are used, this could be optimized slightly so that on toggle()
	 * we only change the handle image rather than the entire switch image.
	 */
	private final Bitmap toggleOff = BitmapFactory.decodeResource(getResources(), R.drawable.switch_long_off);
	private final Bitmap toggleOn = BitmapFactory.decodeResource(getResources(), R.drawable.switch_long_on);
	
	private Paint mPaint = null;
	private Bitmap duplicate = null;
	private Canvas maskCanvas = null;
	
	private static int xPosition = 0;
	private static boolean animateToOff = true;
	private static boolean isAnimating = false;

	private final int fivePixels = 5;
	
	private int px = 0 ;	
	private int offPosition = 0;
	
	public DiscoverToggleSwitch(final Context context) {
		super(context);
		doSetup();
	}
	
	public DiscoverToggleSwitch(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		doSetup();
	}
	
	public DiscoverToggleSwitch(final Context context, final AttributeSet attrs,
			final int defStyle) {
		super(context, attrs, defStyle);
		doSetup();
	}
	
	/**
	 * Setup the layered Canvas to use and get the proper dimensions of certain assets
	 * and key locations on the screen.
	 */
	private void doSetup() {
		
		if(!isChecked()) {
	    	xPosition = getOffPosition();
	    	offPosition = xPosition;
	    	animateToOff = false;
	    }
		
		updateToggleSwitchBitmap();
		
	    mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	    mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
	    
	    maskCanvas = new Canvas(duplicate);
	    mPaint.setColor(Color.BLACK);
	    mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
	    
	    //Compute a pixel value that will be used when animating the toggle.
	    //we get density independent value so that the animation speed will be the same on
	    //displays with differing pixel densities.
	    final Resources r = getResources();
		px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, fivePixels, r.getDisplayMetrics());
	}
	
	/**
	 * onMeasure is overridden so that the size of the toggle view is the size of the mask layer.
	 */
	@Override
	protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec){
		this.setMeasuredDimension(mask.getWidth(), mask.getHeight());
	}

	/**
	 * Performs the drawing of the toggle switch and will also perform the animation if
	 * required.
	 */
	@Override
	public void onDraw(final Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();
		
		animateToggleSlide(canvas);
		canvas.translate(0, 0);
		maskCanvas.drawBitmap(mask, 0, 0, mPaint);
		canvas.drawBitmap(duplicate, xPosition, 0, null);
	}

	/**
	 * The public method to toggle the switch, will toggle with an animation.
	 */
	@Override
	public void toggle() {
		super.toggle();

		if(!isAnimating) {
			isAnimating = true;
		}else {
			//Change direction of the animation.
			animateToOff = !isChecked();
		}
		
		updateToggleSwitchBitmap();
		invalidate();
	}
	
	/**
	 * Allows the switch to be set without performing the animaiton.
	 * @param isChecked the state to put the switch in.
	 */
	public final void toggleWithoutAnimating(final boolean isChecked) {
		this.setChecked(isChecked);
		setSwitchPositionSeated();
		updateToggleSwitchBitmap();
		invalidate();
	}
	
	/**
	 * Based on the state of the toggle, the switch bitmap will be set to the full ON or OFF position.
	 * Must call invalidate() after this method if you want the UI to update accordingly.
	 */
	private void setSwitchPositionSeated() {
		if(isChecked()) {
			xPosition = 0;
		}else {
			xPosition = offPosition;
		}
	}
	
	/**
	 * Animates the switch.
	 * Works by gradually adjusting the xPosition value, which is used as an offset value
	 * when drawing the canvas that the switch resides on.
	 * 
	 * @param canvas
	 */
	private void animateToggleSlide(final Canvas canvas) {
		
		if(isAnimating) {
			
			/**
			 * Animate to the left.
			 */
			if(animateToOff && xPosition > offPosition) {
				xPosition -= px;
				if(xPosition < offPosition) {
					animateToOff = false;
					isAnimating = false;
					xPosition = offPosition - px;
				}
			}
			
			/**
			 * Animate to the right
			 */
			if(!animateToOff && xPosition < 0) {
				xPosition += px;
				if(xPosition >= 0) {
					animateToOff = true;
					isAnimating = false;
					xPosition = 0;
				}
			}
			invalidate();
		}
	}
	
	/**
	 * 
	 * @return if the toggle is in the process of animating to a new state.
	 */
	public final boolean isAnimating() {
		return isAnimating;
	}
	
	/**
	 * 
	 * @return the x value that the underlying switch needs to be translated by to be in the off state.
	 */
	private int getOffPosition() {
		Bitmap toggleHandle = BitmapFactory.decodeResource(getResources(), R.drawable.toggle_handle);
		
		final int toggleWidth = toggleHandle.getWidth();
		toggleHandle.recycle();
		toggleHandle = null;
		
		return -(mask.getWidth() - toggleWidth);
	}
	
	/**
	 * Set the bitmap for the switch to the correct image based on the current state of the toggle.
	 */
	private void updateToggleSwitchBitmap() {
		
		if(isChecked()) {
			duplicate = toggleOn.copy(Config.ARGB_8888, true);
		}else {
			duplicate = toggleOff.copy(Config.ARGB_8888, true);

		}
		
	}

}
