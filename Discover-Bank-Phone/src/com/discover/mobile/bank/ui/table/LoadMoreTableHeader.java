package com.discover.mobile.bank.ui.table;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.ui.widgets.StatusMessageView;
import com.discover.mobile.common.ui.table.TableButtonGroup;
/**
 * The header that is used in a LoadMoreBaseTable.
 * it includes a list of buttons, a status message and a row of table titles.
 * 
 * @author scottseward
 *
 */
public class LoadMoreTableHeader extends LinearLayout {

	/**Group of buttons displayed*/
	private final TableButtonGroup buttons;

	/**Status message views*/
	private StatusMessageView statusView;

	/**Table titles in the view*/
	private TableTitles titles;

	/**
	 * Constructor for the class
	 * @param context - activity context
	 * @param resourceLabels - integer value pointing to the resource array to be used as button labels
	 */
	public LoadMoreTableHeader(final Context context, final int resourceLabels) {
		super(context);
		setOrientation(LinearLayout.VERTICAL);
		buttons = new TableButtonGroup(context, 
				context.getResources().getStringArray(resourceLabels));
		inflateLayout();

	}

	/**
	 * Constructor for the class
	 * @param context - activity context
	 * @param attrs - attributes to apply to the header
	 */
	public LoadMoreTableHeader(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		setOrientation(LinearLayout.VERTICAL);
		buttons = new TableButtonGroup(context, attrs);
		inflateLayout();
	}

	/**
	 * Inflate the layout
	 */
	private void inflateLayout() {
		final Resources res = getContext().getResources();
		final int formsInnerPadding = (int) res.getDimension(R.dimen.forms_inner_padding);
		final int groupsOfElementsPadding = (int) res.getDimension(R.dimen.groups_of_elements_padding);
		statusView = new StatusMessageView(getContext());
		titles = new TableTitles(getContext(), null);

		final LinearLayout.LayoutParams buttonParams = 
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 
						LinearLayout.LayoutParams.WRAP_CONTENT);

		final LinearLayout.LayoutParams statusParams = 
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 
						LinearLayout.LayoutParams.WRAP_CONTENT);

		final LinearLayout.LayoutParams titlesParams = 
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 
						LinearLayout.LayoutParams.WRAP_CONTENT);

		buttonParams.setMargins(0, formsInnerPadding, 0, 0);
		statusParams.setMargins(formsInnerPadding, 0, formsInnerPadding, 0);
		titlesParams.setMargins(0, groupsOfElementsPadding, 0, 0);


		addView(buttons, buttonParams);
		addView(statusView, statusParams);
		addView(titles, titlesParams);
	}

	/**
	 * Get the enum of the selected button.
	 * @return the enum of the selected button
	 */
	public final Enum<?> getSelectedButtonIndex() {
		return buttons.getSelectedEnum();
	}

	/**
	 * Set the custom message
	 * @param message - message to set
	 */
	public void setCustomMessage(final String message) {
		if(statusView != null) {
			statusView.setText(message);
			statusView.setTextBold(false);
		}
	}
	
	/**
	 * Hides the text label that is beneath the row of column titles and above the ListView.
	 */
	public void hideSecondaryMessage() {
		if(titles != null) {
			titles.hideMessage();
		}
	}
	
	/**
	 * Shows the StatusMessageView label with no error icon.
	 */
	public void showMessage() {
		if(statusView != null) {
			statusView.setVisibility(View.VISIBLE);
			hideErrorIcon();
		}
	}
	
	/**
	 * Hides the StatusMessageView error icon.
	 */
	private void hideErrorIcon() {
		if(statusView != null) {
			statusView.hideErrorIcon();
		}
	}
	
	/**
	 * Hides the StatusMessageView label.
	 */
	public void hideMessage() {
		if(statusView != null) {
			statusView.setVisibility(View.GONE);
		}
	}

	/**
	 * Set the selected button
	 * @param selectedEnum - enum of the button to select
	 */
	public final void setSelectedButton(final Enum<?> selectedEnum) {
		buttons.setButtonSelected(selectedEnum);
	}

	/**
	 * Set the selected button
	 * @param selectedEnum - index of the button to select
	 */
	public final void setSelectedButton(final int index){
		buttons.setButtonSelected(index);
	}

	/**
	 * Associate the buttons with enums.  This will associate the buttons
	 * from left to right with the enums.
	 * @param enums - enums to associate with buttons
	 */
	public void associateButtonsWithEnum(final Enum<?>...enums){
		buttons.associateButtonsWithEnum(enums);
	}

	/**
	 * Check to see if the button is selected
	 * @param clickedEnum - enum of the button selected
	 * @return
	 */
	public boolean isButtonSelected(final Enum<?> clickedEnum){
		return buttons.isButtonSelected(clickedEnum);
	}

	/**
	 * Set the observer that will receive the click of the buttons
	 * @param listener - listener to receive the click
	 */
	public void setObserver(final OnClickListener listener){
		buttons.addObserver(listener);
	}

	/**
	 * Clear the observer from the buttons
	 */
	public void clearObserver(){
		buttons.removeObserver();
	}

	public TableTitles getTableTitles(){
		return titles;
	}
}
