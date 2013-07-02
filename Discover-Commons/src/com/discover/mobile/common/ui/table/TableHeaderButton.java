package com.discover.mobile.common.ui.table;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.common.R;

/**
 * Layout representing the buttons for the table headers.
 * Nothing in the inflated layout is clickable so that this view 
 * is always the view clicked.
 * 
 * @author jthornton
 *
 */
public class TableHeaderButton extends RelativeLayout{

	/**View that is inflated*/
	private final View view;

	/**Boolean indicating if the current layout is selected*/
	private boolean selected = false;

	/**Enum to be associated with this button*/
	private Enum<?> associatedEnum;

	/**
	 * Default constructor for the class.  This should not be used
	 * @param context - activity context
	 */
	private TableHeaderButton(final Context context){
		super(context);
		view = inflate(context, R.layout.common_table_tab, null);
	}

	/**
	 * Constructor for the layout
	 * @param context - activity context
	 * @param label - label to place in the button
	 */
	public TableHeaderButton(final Context context, final String label) {
		super(context);

		view = inflate(context, R.layout.common_table_tab, null);
		setClickable(true);
		setBackgroundResource(R.drawable.common_tab_background_selector);
		setText(label);
		addView(view);
	}

	/**
	 * Set the text in the button
	 * @param text - text to set
	 */
	public void setText(final String text){
		((TextView) view.findViewById(R.id.button_text)).setText(text);
	}

	/**
	 * Get the text in the button
	 * @return the text in the button
	 */
	public String getText(){
		return ((TextView) view.findViewById(R.id.button_text)).getText().toString();
	}

	/**
	 * Set the button selected
	 */
	public void setSelected(){
		selected = true;
		view.findViewById(R.id.selected).setVisibility(View.VISIBLE);
	}

	/**
	 * Set the button unselected
	 */
	public void setUnselected(){
		selected = false;
		view.findViewById(R.id.selected).setVisibility(View.INVISIBLE);
	}

	/**
	 * Hide the divider in the layout
	 */
	public void hideDivider(){
		view.findViewById(R.id.divider).setVisibility(View.GONE);
	}

	/**
	 * Return if the layout is selected
	 */
	@Override
	public boolean isSelected(){
		return selected;
	}

	/**
	 * Get the associated enum with this button
	 * @return the associated enum
	 */
	public Enum<?> getAssociatedEnum() {
		return associatedEnum;
	}

	/**
	 * Set the associated enum with this button
	 * @param associatedEnum the the enun to associate with this button
	 */
	public void setAssociatedEnum(final Enum<?> associatedEnum) {
		this.associatedEnum = associatedEnum;
	}

}
