package com.discover.mobile.card.account.summary;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.card.R;
/**
 * Simple list item layout.  Holds an action item, label and a value
 * @author jthornton
 *
 */
public class SimpleListItem extends RelativeLayout{

	/**Label to be shown at the top of the cell*/
	private final TextView label;

	/**Value to be shown under the label*/
	private final TextView value;

	/**Action item to be shown if there is any action*/
	private final TextView action;

	/**Glass bar line shown next to the image line*/
	private final ImageView line;

	/**
	 * Constructor for the class
	 * @param context - activity context
	 * @param attrs - attribute set to apply to the layout
	 */
	public SimpleListItem(final Context context, final AttributeSet attrs) {
		super(context, attrs);

		final RelativeLayout layout = 
				(RelativeLayout)LayoutInflater.from(context).inflate(R.layout.simple_list_item, null);

		label = (TextView)layout.findViewById(R.id.balance_label);
		value = (TextView)layout.findViewById(R.id.balance_value);
		action = (TextView)layout.findViewById(R.id.action_text);
		line = (ImageView)layout.findViewById(R.id.divider_line);

		addView(layout);
	}

	/**
	 * Hide the action text
	 */
	public void hideAction(){
		action.setVisibility(View.INVISIBLE);
	}

	/**
	 * Set the text label shown in the cell
	 * @param label - label to be shown
	 */
	public void setLabel(final String label){
		this.label.setText(label);
	}

	/**
	 * Set the action to be shown 
	 * @param action - action string that should be shown
	 */
	public void setAction(final String action){
		this.action.setText(action);
		line.setVisibility(View.VISIBLE);
	}

	/**
	 * Set the value of the string in the value spot
	 * @param value - value to be shown
	 */
	public void setValue(final String value){
		this.value.setText(value);
	}

	/**
	 * Set the action handler when the action button is clicked.
	 * @param listener - lister to attach to the action button
	 */
	public void setActionHandler(final OnClickListener listener){
		action.setOnClickListener(listener); 
	}
}
