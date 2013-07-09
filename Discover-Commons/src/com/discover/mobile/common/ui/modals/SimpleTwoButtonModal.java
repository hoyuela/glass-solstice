package com.discover.mobile.common.ui.modals;

import android.content.Context;
import android.os.Bundle;
import android.text.Spanned;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.discover.mobile.common.R;
import com.discover.mobile.common.ui.help.NeedHelpFooter;

/**
 * Simple modal alert with two buttons.  This suppressed warngings to depricate classes
 * so that it can wrap them.
 * 
 * @author jthornton
 *
 */
@SuppressWarnings("deprecation")
public class SimpleTwoButtonModal extends ModalAlertWithTwoButtons implements ModalTopView, ModalBottomTwoButtonView{

	/**View of the modal*/
	private final View view;

	/** View that holds the footer text */
	private final NeedHelpFooter helpFooter;

	/**
	 * Constuctor for the modal
	 * @param context - activity context
	 */
	public SimpleTwoButtonModal(final Context context){
		super(context);
		view = getLayoutInflater().inflate(R.layout.simple_two_button_modal, null);
		helpFooter = new NeedHelpFooter((ViewGroup) view);
	}
	
	public SimpleTwoButtonModal(final Context context, int title, int message, int okButtonText, int cancelButtonText){
		super(context);
		view = getLayoutInflater().inflate(R.layout.simple_two_button_modal, null);
		helpFooter = new NeedHelpFooter((ViewGroup) view);
		setTitle(title);
		setContent(message);
		setOkButtonText(okButtonText);
		setCancelButtonText(cancelButtonText);
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

	@Override
	public ModalTopView getTop(){
		return this;
	}

	@Override
	public ModalBottomTwoButtonView getBottom(){
		return this;
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
	 * Set the text in the content view
	 * 
	 * @param content
	 *            - int with text to be displayed as the message
	 */
	public void setContent(final Spanned content){
		((TextView) view.findViewById(R.id.modal_alert_text)).setText(content);
	}

	/**
	 * Set the text in the content view
	 * 
	 * @param content
	 *            - int with text to be displayed as the message
	 */
	public TextView getContentView(){
		return (TextView) view.findViewById(R.id.modal_alert_text);
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
	 * Show an error icon to the left of the modal dialog title.
	 * @param isError - tells the dialog to show an error icon or not.
	 */
	public void showErrorIcon(final boolean isError) {
		if(isError) {
			showErrorIcon();
		}
	}

	/**
	 * 
	 * @return the button on the modal
	 */
	@Override
	public Button getOkButton(){
		return (Button) view.findViewById(R.id.modal_ok_button);
	}

	/**
	 * Set the text in the button
	 * @param text - text to display
	 */
	@Override
	public void setOkButtonText(final int text){
		((Button) view.findViewById(R.id.modal_ok_button)).setText(text);
	}

	/**
	 * 
	 * @return the button on the modal
	 */
	@Override
	public Button getCancelButton(){
		return (Button) view.findViewById(R.id.modal_alert_cancel);
	}

	/**
	 * Set the text in the button
	 * @param text - text to display
	 */
	@Override
	public void setCancelButtonText(final int text){
		((Button) view.findViewById(R.id.modal_alert_cancel)).setText(text);
	}

	/**
	 * Init the UI
	 */
	@Override
	protected void initUI() { 
		//Intentionally left empty to allow wrapping of the super class
	}

	/**
	 * Create the orientation changed listener
	 * @return the orientation changed listener
	 */
	@Override
	public OrientationEventListener createOrientationListener() { 
		//Intentionally left empty to allow wrapping of the super class
		return null; 
	}

	/**
	 * Display the layout with the correct layout.
	 */
	@Override
	public void display(){ 
		//Intentionally left empty to allow wrapping of the super class
	}
}
