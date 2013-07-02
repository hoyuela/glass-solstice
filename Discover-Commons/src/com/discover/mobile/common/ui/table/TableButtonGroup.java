package com.discover.mobile.common.ui.table;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.discover.mobile.common.R;

/**
 * Linear layout that contains all of the buttons for the table cells.
 * This will dynamically create the buttons from the xml file. To utilized
 * this a custom namespace tag needs to be added to the layout like such:
 * 
 * xmlns:custom="http://schemas.android.com/apk/res-auto/com.discover.mobile.common"
 * 
 * Then you need to add the view like a custom widget and define its layout attributes
 * like you would for a normal layout. Then add a custom view declaration that references
 * an array of strings like follows:
 * 
 *  custom:resourceArry="@array/account_activity_buttons" 
 * 
 * When inflated this group will then iterate through that array and then
 * create a button for each string in that array and make that string the 
 * text to be displayed in the button.
 * 
 * To get the click events that happen when the buttons are clicked the 
 * holding view should implement a OnClickListener and then call addObserver(OnClickListener).
 * This view will then make sure all clicks are routed to the listener.
 * Also, when this view is paused, the view should call removeObserver() to make sure
 * that it unsubscribe from the click events.
 * 
 * 
 * @author jthornton
 *
 */
public class TableButtonGroup extends LinearLayout{

	/**List of buttons that are displayed in this layout*/
	private List<TableHeaderButton> views;

	/**Currently selected button*/
	private TableHeaderButton selected;

	/**
	 * Constructor for the class
	 * @param context - activity context
	 */
	public TableButtonGroup(final Context context, final String[] labels) {
		super(context);
		setClickable(false);
		setOrientation(LinearLayout.HORIZONTAL);
		addViewsToLayout(context, labels);
	}

	/**
	 * Constructor for the class
	 * @param context - activity context
	 * @param attrs - attributes applied to the layout
	 */
	public TableButtonGroup(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		setClickable(false);
		setOrientation(LinearLayout.HORIZONTAL);
		applyAttributes(context, attrs);
	}

	/**
	 * Constructor for the class
	 * @param context - activity context
	 * @param attrs - attributes applied to the layout
	 * @param defStyle - style for the layout
	 */
	@SuppressLint("NewApi")
	public TableButtonGroup(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		setClickable(false);
		setOrientation(LinearLayout.HORIZONTAL);
		applyAttributes(context, attrs);
	}

	/**
	 * Apply the custom attributes if any were applied in the XML.  This will only apply the attributes
	 * that are specific to the custom name space.
	 * @param context - activity context
	 * @param attrs - attributed applied to the layout
	 */
	private void applyAttributes(final Context context, final AttributeSet attrs){
		final TypedArray a = 
				context.obtainStyledAttributes(attrs, R.styleable.com_discover_mobile_common_ui_table_TableButtonGroup);

		String[] array = null;	
		final int n = a.getIndexCount();
		for (int i = 0; i < n; ++i) {
			final int attr = a.getIndex(i);
			switch (attr) {
			case R.styleable.com_discover_mobile_common_ui_table_TableButtonGroup_resourceArry:
				final int id = a.getResourceId(attr, 0);
				array = getResources().getStringArray(id);
				break;

			}
		}
		a.recycle();

		if(null != array){
			addViewsToLayout(context, array);
		}
	}

	/**
	 * Add views to the layout.  
	 * @param context - activity context
	 * @param labels - array of labels to create buttons for
	 */
	private void addViewsToLayout(final Context context, final String[] labels){
		if(null == views){
			views = new ArrayList<TableHeaderButton>();
		}

		/**
		 * Linear layout for the buttons.  This will default to one so that the buttons stretch to
		 * fill the layout in both portrait and landscape.
		 */
		final LinearLayout.LayoutParams params = 
				new LayoutParams(LayoutParams.WRAP_CONTENT,  LayoutParams.WRAP_CONTENT, 1.0f);

		/**
		 * Set the weight of this to be the number of buttons that will be in the layout.
		 * This allows the buttons to all have the same size.
		 */
		setWeightSum(labels.length);

		/**
		 * Iterate through the list so that we can add all the buttons
		 */
		for(int i = 0; i < labels.length; i++){
			final TableHeaderButton button = new TableHeaderButton(context, labels[i]);
			button.setUnselected();
			button.setTag(i);
			views.add(button);
			addView(button, params);
		}

		setUpDefaultLook(labels.length-1);
	}

	/**
	 * Set up the look and feel of the buttons when they are first created.
	 * @param lastItem - location of the last item
	 */
	private void setUpDefaultLook(final int lastItem){
		final TableHeaderButton last = views.get(lastItem);
		final TableHeaderButton first = views.get(0);
		last.hideDivider();
		first.setSelected();
		selected = first;
	}

	/**
	 * Add the observer to all the view so that it can receive the click
	 * events of all the buttons.
	 * @param observer - listener that should receive the click event.
	 */
	public void addObserver(final OnClickListener observer){
		if(null != views){
			for(final TableHeaderButton view : views){
				view.setOnClickListener(observer);
			}
		}
	}

	/**
	 * Set the button at a specific location selected and update the currently
	 * selected button.
	 * @param position - position to set selected
	 */
	public void setButtonSelected(final int position){
		final TableHeaderButton newSelection =  views.get(position);
		if(null != newSelection && !selected.getText().equals(newSelection.getText())){
			newSelection.setSelected();
			if(null != selected){
				selected.setUnselected();
			}
			selected = views.get(position);
		}
	}

	/**
	 * Set the button that has a specific associated enum to selected.
	 * @param selectedEnum - Enum of the button that needs to be selected.
	 */
	public void setButtonSelected(final Enum<?> selectedEnum){
		final TableHeaderButton newSelection =  findViewBasedOnEnum(selectedEnum);
		if(null != newSelection && !selected.getText().equals(newSelection.getText())){
			newSelection.setSelected();
			if(null != selected){
				selected.setUnselected();
			}
			selected = newSelection;
		}
	}

	/**
	 * Find the view that has the selected enum
	 * @param selectedEnum - selected enum
	 * @return null if the selected enum cannot be found, otherwise the button holding the enum
	 */
	private TableHeaderButton findViewBasedOnEnum(final Enum<?> selectedEnum){
		TableHeaderButton button = null;
		for(int i = 0; i < views.size(); i++){
			if(views.get(i).getAssociatedEnum().equals(selectedEnum)){
				button = views.get(i);
			}
		}
		return button;
	}

	/**
	 * Get the button at a specific location
	 * @param postion - position in the table to get the button
	 * @return the button at that position
	 */
	public TableHeaderButton getButton(final int postion){
		return views.get(postion);
	}

	/**
	 * Remove the listeners from the buttons
	 */
	public void removeObserver(){
		if(null != views){
			for(final TableHeaderButton view : views){
				view.setOnClickListener(null);
			}
		}
	}

	/**
	 * Check to see if a button is selected
	 * @param clickedEnum - enum that the button pressed contains
	 * @return if the enum of the pressed button matches the enum of the selected
	 */
	public boolean isButtonSelected(final Enum<?> clickedEnum){
		return selected.getAssociatedEnum().equals(clickedEnum);
	}

	/**
	 * Associate an unknown amount of enums with the buttons.
	 * This will associated the enums from left to right.
	 * @param enums enums to associated with buttons
	 */
	public void associateButtonsWithEnum(final Enum<?>...enums){
		for(int i = 0; i < enums.length; i++){
			getButton(i).setAssociatedEnum(enums[i]);
		}
	}

	/**
	 * Get the selected buttons enum
	 * @return the selected buttons enum
	 */
	public Enum<?> getSelectedEnum(){
		return selected.getAssociatedEnum();
	}
}
