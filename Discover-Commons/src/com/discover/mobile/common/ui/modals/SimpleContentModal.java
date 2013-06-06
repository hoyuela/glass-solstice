package com.discover.mobile.common.ui.modals;

import android.content.Context;
import android.os.Bundle;
import android.view.OrientationEventListener;
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
 * For now this modal will suppress the warnings and can be used as a wrapper for the old
 * modal system
 *
 * @author jthornton
 */
@SuppressWarnings("deprecation")
public class SimpleContentModal extends ModalAlertWithOneButton implements ModalBottomOneButtonView, ModalTopView{

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
	 * An alternate way to create a modal alert with one button 
	 * by supplying content only
	 * 
	 * @param context  - application context
	 * @param title - the title for the alert
	 * @param content - the body text for the alert
	 * @param buttonText - the button text for the alert
	 */
	public SimpleContentModal(final Context context, 
			final int title, final int content, 
			final int buttonText) {
		this(context);
		setTitle(title);
		setContent(content);
		setButtonText(buttonText);
	}

	/**
	 * Constructor for the alert
	 * @param context - activity context
	 * @param top - top piece to be displayed
	 * @param bottom - bottom piece to be displayed
	 */
	public SimpleContentModal(final Context context, 
			final int title, final String content, 
			final int buttonText) {

		this(context);
		setTitle(title);
		setContent(content);
		setButtonText(buttonText);
	}

	/**
	 * Constructor for the alert
	 * @param context - activity context
	 * @param top - top piece to be displayed
	 * @param bottom - bottom piece to be displayed
	 */
	public SimpleContentModal(final Context context, 
			final String title, final String content, 
			final int buttonText) {

		this(context);
		setTitle(title);
		setContent(content);
		setButtonText(buttonText);
	}

	/**
	 * An alternate way to create a modal alert with one button 
	 * by supplying content only
	 * 
	 * @param context  - application context
	 * @param title - the title for the alert
	 * @param content - the body text for the alert
	 * @param showErrorIcon - used to show the error icon on the top view
	 * @param helpNumber - help number to be displayed in the modal
	 * @param buttonText - the button text for the alert
	 */
	public SimpleContentModal(final Context context, final int title, final int content, 
			final boolean showErrorIcon, final int helpNumber, final int buttonText) {
		this(context,title,content,buttonText);
		showErrorIcon(showErrorIcon);
		helpFooter.setToDialNumberOnClick(helpNumber);
	}

	/**
	 * An alternate way to create a modal alert with one button 
	 * by supplying content only
	 * 
	 * @param context  - application context
	 * @param title - the title for the alert
	 * @param content - the body text for the alert
	 * @param showErrorIcon - used to show the error icon on the top view
	 * @param helpNumber - help number to be displayed in the modal
	 * @param buttonText - the button text for the alert
	 */
	public SimpleContentModal(final Context context, final String title, final String content, 
			final boolean showErrorIcon, final int helpNumber, final int buttonText) {
		this(context,title,content,buttonText);
		showErrorIcon(showErrorIcon);
		helpFooter.setToDialNumberOnClick(helpNumber);
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
	@Override
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
	@Override
	public void setContent(final String content){
		((TextView) view.findViewById(R.id.modal_alert_text)).setText(content);
	}

	/**
	 * Set the text in the content view
	 * 
	 * @param content
	 *            - int with text to be displayed as the message
	 */
	@Override
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
	@Override
	public Button getButton(){
		return (Button) view.findViewById(R.id.button);
	}

	/**
	 * Set the text in the button
	 * @param text - text to display
	 */
	@Override
	public void setButtonText(final int text){
		((Button) view.findViewById(R.id.button)).setText(text);
	}

	/**
	 * Get the top piece so that it can be manipulated
	 * @return the top piece so that it can be manipulated
	 */
	@Override
	public ModalTopView getTop(){
		return this;
	}

	/**
	 * Get the bottom piece so that it can be manipulated
	 * @return the bottom piece so that it can be manipulated
	 */
	@Override
	public ModalBottomOneButtonView getBottom(){
		return this;
	}

	/**
	 * Show an error icon to the left of the modal dialog title.
	 * @param isError - tells the dialog to show an error icon or not.
	 */
	public void showErrorIcon(final boolean isError) {
		if(isError) {
			showErrorIcon();
		}
	}

	@Override
	protected void initUI() { }

	/**
	 * Create the orientation changed listener
	 * @return the orientation changed listener
	 */
	@Override
	public OrientationEventListener createOrientationListener() { return null; }

	/**
	 * Display the layout with the correct layout.
	 */
	@Override
	public void display(){ }
}
