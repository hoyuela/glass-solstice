package com.discover.mobile.bank.paybills;

import java.io.Serializable;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.R;

/**
 * Simple view that is used to display a sting and a carrot that when selected will go to a different screen.
 * This is meant to be reusable.
 * 
 * @author jthornton
 *
 */
public class SimpleChooseListItem extends RelativeLayout{

	/**
	 * Serializable item that the view is holding.
	 */
	private Serializable item;

	/**View of the item*/
	private final RelativeLayout mainView;
	
	private ImageView caret;

	public SimpleChooseListItem(final Context context, final AttributeSet attrs){
		super(context, attrs);
		this.mainView = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.simple_choose_item, null);
		addView(this.mainView);
	}

	/**
	 * Constructor of the class
	 * @param context - activity context
	 * @param attrs - attributes to give to the layout
	 * @param item - item that is associated with this view
	 * @param text - text to display
	 */
	public SimpleChooseListItem(final Context context, final AttributeSet attrs,
			final Serializable item, final String text) {
		super(context, attrs);

		this.mainView = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.simple_choose_item, null);
		
		final TextView title = (TextView) this.mainView.findViewById(R.id.text);
		title.setText(text);
		this.setItem(item);

		addView(this.mainView);
	}

	/**
	 * Constructor of the class
	 * @param context - activity context
	 * @param attrs - attributes to give to the layout
	 * @param item - item that is associated with this view
	 * @param text - text to display
	 * @param showCaret - boolean used to determine whether caret image should be displayed or not
	 */
	public SimpleChooseListItem(final Context context, final AttributeSet attrs,
			final Serializable item, final String text, final boolean showCaret) {
		super(context, attrs);

		this.mainView = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.simple_choose_item, null);
		
		/**Check whether caret in the layout should be shown or hidden*/
		this.caret = (ImageView)mainView.findViewById(R.id.carrot);		
		this.caret.setVisibility((showCaret)?View.VISIBLE:View.GONE);
		
		final TextView title = (TextView) this.mainView.findViewById(R.id.text);
		title.setText(text);
		this.setItem(item);

		addView(this.mainView);
	}

	
	/**
	 * Set the on click listener of the item
	 * @param listener - listener to attach to the view
	 */
	public void setClickListener(final OnClickListener listener){
		this.mainView.setOnClickListener(listener);
	}

	/**
	 * @return the item
	 */
	public Serializable getItem() {
		return this.item;
	}

	/**
	 * @param item the item to set
	 */
	public void setItem(final Serializable item) {
		this.item = item;
	}
	
	/**
	 * Method used to set the text for the title TextView in the layout
	 * 
	 * @param id Resource identifier for the string in res/string
	 */
	public void setTitleText(final int id) {
		final TextView title = (TextView) this.mainView.findViewById(R.id.text);
		title.setText(id);
	}
}