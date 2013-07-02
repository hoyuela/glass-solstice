package com.discover.mobile.bank.ui.table;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.ui.widgets.StatusMessageView;
import com.discover.mobile.common.ui.table.TableButtonGroup;

/**
 * Header for the load more table.  This contains the button group,
 * the table titles and the status message.
 * 
 * @author sseward / jthornton
 *
 */
public class LoadMoreTableHeader extends RelativeLayout {

	/**Group of buttons displayed*/
	private TableButtonGroup buttons;

	/**
	 * Constructor for the class
	 * @param context - activity context
	 */
	public LoadMoreTableHeader(final Context context) {
		super(context);
		inflateLayout();
	}

	/**
	 * Constructor for the class
	 * @param context - activity context
	 * @param attrs - attributes to apply to the header
	 */
	public LoadMoreTableHeader(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		inflateLayout();
	}

	/**
	 * Constructor for the class
	 * @param context - activity context
	 * @param attrs - attributes to apply to the header
	 * @param defStyle - style to apply
	 */
	public LoadMoreTableHeader(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		inflateLayout();
	}

	/**
	 * Inflate the layout
	 */
	private void inflateLayout() {
		final View inflatedLayout = LayoutInflater.from(getContext()).inflate(R.layout.load_more_header, null);
		buttons = (TableButtonGroup) inflatedLayout.findViewById(R.id.button_row);
		this.addView(inflatedLayout);
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
		final StatusMessageView statusView = (StatusMessageView)findViewById(R.id.status);
		if(statusView != null) {
			statusView.setText(message);
			statusView.setTextBold(false);
		}
	}

	/**
	 * Hide the secondary title message
	 */
	public void hideSecondaryMessage() {
		final TableTitles titles = (TableTitles)findViewById(R.id.table_titles);
		if(titles != null) {
			titles.hideMessage();
		}
	}

	/**
	 * Show the message
	 */
	public void showMessage() {
		final StatusMessageView statusView = (StatusMessageView)findViewById(R.id.status);
		if(statusView != null) {
			statusView.setVisibility(View.VISIBLE);
			hideErrorIcon();
		}
	}

	/**
	 * Hide the status bar error icon
	 */
	private void hideErrorIcon() {
		final StatusMessageView statusView = (StatusMessageView)findViewById(R.id.status);
		if(statusView != null) {
			statusView.hideErrorIcon();
		}
	}

	/**
	 * Hide the status bar message
	 */
	public void hideMessage() {
		final StatusMessageView statusView = (StatusMessageView)findViewById(R.id.status);
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
}
