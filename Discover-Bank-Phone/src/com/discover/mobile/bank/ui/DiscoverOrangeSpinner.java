package com.discover.mobile.bank.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;

import com.discover.mobile.bank.R;

/**
 * This class is an animated loading dialog using the orange spinner image
 * that is essentially an iOS loading spinner... but orange.
 * 
 * It works by rotating a canvas by 1/12 of a rotation every 100 milliseconds.
 * It can be started and stopped.
 * 
 * @author scottseward
 *
 */
public class DiscoverOrangeSpinner extends View {
	/**The current amount of rotation, in degrees, that will be applied to the image */
	private float rotationDegrees = 0f;
	
	/**Constants for rotation */
	private final float twoPiDegrees = 360.0f;
	private final float imageSegments = 12.0f;
	private final float spinnerSegmentSeparation = twoPiDegrees/imageSegments;
	private final long delayTime = 100;
	
	/**A boolean for if the spinner should continue to animate it self*/
	private boolean isAnimating = true;
	
	/**The actual image that is being rotated */
	private final Bitmap spinner = BitmapFactory.decodeResource(getResources(), R.drawable.large_loading_icon);

	/**The matrix that is being transformed to allow rotation of the image */
	final Matrix transform = new Matrix();

	public DiscoverOrangeSpinner(final Context context) {
		super(context);
		doSetup();
	}
	
	public DiscoverOrangeSpinner(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		doSetup();
	}
	
	public DiscoverOrangeSpinner(final Context context, final AttributeSet attrs,
			final int defStyle) {
		super(context, attrs, defStyle);
		doSetup();
	}
	
	private void doSetup() {
		rotateLogo();
	}
	
	public void startAnimation() {
		isAnimating = true;
		rotateLogo();
	}
	
	public void stopAnimation() {
		isAnimating = false;
		rotationDegrees = 0;
	}
	
	/**
	 * Causes the size of the view to be the size of the spinner image.
	 * Should use the xml width and height attribute 'wrap_content'
	 */
	@Override
	protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
	    setMeasuredDimension(spinner.getWidth(), spinner.getHeight());
	}

	/**
	 * Draws the spinner with the current amount of rotation applied.
	 */
	@Override
	public void onDraw(final Canvas canvas) {
	    transform.setTranslate(0, 0);
	    transform.preRotate(rotationDegrees, spinner.getWidth()/2, spinner.getHeight()/2);
	    
		canvas.save();
	    canvas.drawBitmap(spinner, transform, null);

		canvas.restore();
	}

	/**
	 * Queues the rotateCanvasRunnable onto the main thread with a delay.
	 * After the delay time, the spinner will be rotated and redrawn.
	 */
	private void rotateLogo() {
		if(isAnimating) {
			this.postDelayed(rotateCanvasRunnable, delayTime);
		}
	}
	
	/**
	 * This runnable, when run, will rotate the image and redraw it.
	 */
	private final Runnable rotateCanvasRunnable = new Runnable() {
		
		@Override
		public void run() {
			if(rotationDegrees <= -twoPiDegrees) {
				rotationDegrees = 0;
			}
			
			rotationDegrees -= spinnerSegmentSeparation;
			invalidate();
			
			//Continue calling rotateLogo, will keep posting this runnable to the main thread
			//until isAnimating is set to false.
			rotateLogo();
		}
	};
}
