package com.discover.mobile.common.ui.modals;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.discover.mobile.common.R;
import com.discover.mobile.common.ui.help.NeedHelpFooter;

/**
 * Simple class for modals.  This will rotate without weights and
 * will still look like the other modals.
 * 
 * @author jthornton
 *
 */
public class SimpleContentModal extends AlertDialog{

	/**View of the modal*/
	private final View view;

	/** View that holds the footer text */
	private final NeedHelpFooter helpFooter;

	/**
	 * Constructor for the alert
	 * @param context - activity context
	 */
	public SimpleContentModal(final Context context) {
		super(context);	
		view = getLayoutInflater().inflate(R.layout.simple_content_modal, null);
		helpFooter = new NeedHelpFooter((ViewGroup) view);
	}

	/**
	 * Create the modal alert and add the views to be displayed.
	 * @param savedInstanceState - saved state of the modal
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		this.setContentView(view);
	}

	/**
	 * Set the text in the title view
	 * 
	 * @param text
	 *            - String with text to be displayed as the title
	 */
	public void setTitle(final String title){
		((TextView) view.findViewById(R.id.modal_alert_title)).setText(title);
	}

	/**
	 * Set the text in the title view
	 * 
	 * @param text
	 *            - int with text to be displayed as the title
	 */
	@Override
	public void setTitle(final int title){
		((TextView) view.findViewById(R.id.modal_alert_title)).setText(title);
	}

	/**
	 * Set the text in the content view
	 * 
	 * @param content
	 *            - String with text to be displayed as the message
	 */
	public void setContent(final String content){
		((TextView) view.findViewById(R.id.modal_alert_text)).setText(content);
	}

	/**
	 * Set the text in the content view
	 * 
	 * @param content
	 *            - int with text to be displayed as the message
	 */
	public void setContent(final int content){
		((TextView) view.findViewById(R.id.modal_alert_text)).setText(content);
	}

	/**
	 * Show an error icon to the left of the modal dialog title.
	 * 
	 * @param isError
	 *            - tells the dialog to show an error icon or not.
	 */
	public void showErrorIcon(){
		view.findViewById(R.id.error_icon).setVisibility(View.VISIBLE);
	}

	/**
	 * Hide the help footer
	 */
	public void hideNeedHelpFooter(){
		helpFooter.show(false);
	}

	/**
	 * @return Returns the NeedHelpFooter wrapper instance which allows to set
	 *         the footer help number
	 */
	public NeedHelpFooter getHelpFooter() {
		return helpFooter;
	}

	/**
	 * 
	 * @return the button on the modal
	 */
	public Button getButton(){
		return (Button) view.findViewById(R.id.button);
	}

	/**
	 * Set the text in the button
	 * @param text - text to display
	 */
	public void setButtonText(final int text){
		((Button) view.findViewById(R.id.button)).setText(text);
	}
}
