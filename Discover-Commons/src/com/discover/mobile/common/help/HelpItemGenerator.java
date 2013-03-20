/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.common.help;

import android.view.View.OnClickListener;

/**
 * Object used to assist the help widget in rendering layouts.
 * This object, when fully populated gets passed into the adapter and the values
 * in this object will then render.
 * 
 * @author jthornton
 *
 */
public class HelpItemGenerator {

	/**Resource integer value of the string that should be shown in the layout*/
	final int text;

	/**Resource id of the background that should be shown for the item in the list view*/
	final int background;

	/**Boolean set to true when the arrow should be shown next to the text*/
	final boolean showArrow;

	/**Click listener that will be attached to the view while it is visible in the search widget*/
	final OnClickListener listener;

	/**
	 * Constructor for the help item
	 * @param text - resource integer value of the string that should be shown in the layout
	 * @param background - resource id of the background that should be shown for the item in the list view
	 * @param showArrow - boolean set to true when the arrow should be shown next to the text
	 * @param listener - click listener that will be attached to the view while it is visible in the search widget
	 */
	public HelpItemGenerator(final int text, final int background, final boolean showArrow, final OnClickListener listener){
		this.text = text;
		this.background = background;
		this.showArrow = showArrow;
		this.listener = listener;
	}

	/**
	 * @return the text
	 */
	public int getText() {
		return text;
	}

	/**
	 * @return the background
	 */
	public int getBackground() {
		return background;
	}

	/**
	 * @return the showArrow
	 */
	public boolean isShowArrow() {
		return showArrow;
	}

	/**
	 * @return the listener
	 */
	public OnClickListener getListener() {
		return listener;
	}

}
