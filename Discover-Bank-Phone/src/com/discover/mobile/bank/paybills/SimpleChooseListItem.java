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

	public SimpleChooseListItem(final Context context, final AttributeSet attrs){
		super(context, attrs);
		mainView = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.simple_choose_item, null);
		addView(mainView);
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
		this(context, attrs, item, text, R.layout.simple_choose_item);
	}

	/**
	 * Constructor - creates a background based on the layout id sent.
	 * @param context - activity context
	 * @param attrs - attributes to give to the layout
	 * @param item - item that is associated with this view
	 * @param text - text to display
	 * @param layoutResourceId - custom layout to use for element
	 */
	public SimpleChooseListItem(final Context context, final AttributeSet attrs,
			final Serializable item, final String text, final int layoutResourceId) {
		super(context, attrs);

		mainView = (RelativeLayout) LayoutInflater.from(context).inflate(layoutResourceId, null);

		final TextView title = (TextView) mainView.findViewById(R.id.text);
		title.setText(text);
		this.setItem(item);

		addView(mainView);
	}

	/**
	 * Constructor of the class - creates a background with solid stroke on all sides.
	 * @param context - activity context
	 * @param attrs - attributes to give to the layout
	 * @param item - item that is associated with this view
	 * @param text - text to display
	 * @param showCaret - boolean used to determine whether caret image should be displayed or not
	 */
	public SimpleChooseListItem(final Context context, final AttributeSet attrs,
			final Serializable item, final String text, final boolean showCaret) {
		super(context, attrs);

		mainView = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.simple_choose_item, null);

		/**Check whether caret in the layout should be shown or hidden*/
		final ImageView caret = (ImageView)mainView.findViewById(R.id.carrot);		
		caret.setVisibility((showCaret)?View.VISIBLE:View.GONE);

		final TextView title = (TextView) mainView.findViewById(R.id.text);
		title.setText(text);
		this.setItem(item);

		addView(mainView);
	}

	/**
	 * Sets the background resource to a solid stroke on all sides except for
	 * the top. The top is a dashed line.
	 * 
	 * @param context
	 */
	public void setBackgroundAsBottomItem(final Context context) {
		final RelativeLayout view = (RelativeLayout) this
				.getChildAt(0);
		view.setBackgroundDrawable(context.getResources().getDrawable(
				R.drawable.home_list_item_dash));
		view.setPadding(
				(int) context.getResources().getDimension(
						R.dimen.forms_inner_padding),
						(int) context.getResources().getDimension(
								R.dimen.table_inner_padding),
								(int) context.getResources().getDimension(
										R.dimen.forms_inner_padding),
										(int) context.getResources().getDimension(
												R.dimen.table_inner_padding));
	}

	/**
	 * Sets the background resource to a solid stroke on all sides except for
	 * the bottom. The bottom is a blank.
	 * 
	 * @param context
	 */
	public void setBackgroundAsTopItem(final Context context) {
		final RelativeLayout view = (RelativeLayout) this
				.getChildAt(0);
		view.setBackgroundDrawable(context.getResources().getDrawable(
				R.drawable.home_list_item_no_bottom_stroke));
		view.setPadding(
				(int) context.getResources().getDimension(
						R.dimen.forms_inner_padding),
						(int) context.getResources().getDimension(
								R.dimen.table_inner_padding),
								(int) context.getResources().getDimension(
										R.dimen.forms_inner_padding),
										(int) context.getResources().getDimension(
												R.dimen.table_inner_padding));
	}

	/**
	 * Sets the background resource to a solid stroke on all sides except for
	 * the bottom. The bottom is a blank.
	 * 
	 * @param context
	 */
	public void setTextBlue(final Context context) {
		final TextView title = (TextView) mainView.findViewById(R.id.text);
		title.setTextColor(context.getResources().getColor(R.color.blue_link));
	}

	/**
	 * Set the on click listener of the item
	 * 
	 * @param listener
	 *            - listener to attach to the view
	 */
	public void setClickListener(final OnClickListener listener){
		mainView.setOnClickListener(listener);
	}

	/**
	 * @return the item
	 */
	public Serializable getItem() {
		return item;
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
		final TextView title = (TextView) mainView.findViewById(R.id.text);
		title.setText(id);
	}
}