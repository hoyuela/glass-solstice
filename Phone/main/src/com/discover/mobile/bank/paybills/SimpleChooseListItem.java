package com.discover.mobile.bank.paybills;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.discover.mobile.R;

/**
 * Simple view that is used to display a sting and a carrot that when selected will go to a different screen.
 * This is meant to be reusable.
 * 
 * @author jthornton
 *
 */
public class SimpleChooseListItem extends RelativeLayout{

	/**
	 * Constructor of the class
	 * @param context - activity context
	 * @param attrs - attributes to give to the layout
	 */
	public SimpleChooseListItem(final Context context, final AttributeSet attrs) {
		super(context, attrs);

		final RelativeLayout mainView =
				(RelativeLayout) LayoutInflater.from(context).inflate(R.layout.simple_choose_item, null);

		addView(mainView);
	}
}