package com.discover.mobile.bank.ui.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * This class is a view which will constrain its height with respect to its width.
 * It is used in the review deposit Fragment where we have images that need to resize based on their container
 * and keep their proportions so they do not become distorted.
 * 
 * @author scottseward
 *
 */
public class ScalableImage extends View {

  	public ScalableImage(final Context context) {
	    super(context);
  	}

  	public ScalableImage(final Context context, final AttributeSet attrs) {
	    super(context, attrs);
	   
  	}

  	public ScalableImage(final Context context, final AttributeSet attrs, final int defStyle) {
	    super(context, attrs, defStyle);
	    
  	}

  	/**
  	 * Sets the height of the View to a proportion of the width.
  	 */
  	@Override protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
  		Drawable logo = this.getBackground();
  		int width = 0;
  		int height = 0;
  		
  		if(logo != null) {
  			width = MeasureSpec.getSize(widthMeasureSpec);
  			height = width * logo.getIntrinsicHeight() / logo.getIntrinsicWidth();
  		}
  		
	    setMeasuredDimension(width, height);
  	}
}