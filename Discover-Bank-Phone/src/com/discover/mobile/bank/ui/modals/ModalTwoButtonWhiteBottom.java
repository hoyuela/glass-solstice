/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.ui.modals;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.discover.mobile.bank.R;
import com.discover.mobile.common.ui.modals.ModalBottomTwoButtonView;

/**
 * Default two button bottom view for the modal
 * @author jthornton
 *
 */
public class ModalTwoButtonWhiteBottom extends RelativeLayout implements ModalBottomTwoButtonView{

	/**The main call to action button in the bottoer of the dialog*/
	private final Button mainCallToActionButton;

	/**The main cancel button in the modal*/
	private final Button cancelButton;

	/*Application Context context*/
	private final Context context;

	public ModalTwoButtonWhiteBottom(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		final RelativeLayout buttonView = (RelativeLayout) LayoutInflater.from(context)
				.inflate(R.layout.modal_two_button_white_bottom, null);

		mainCallToActionButton = (Button)buttonView.findViewById(R.id.modal_ok_button);
		cancelButton  = (Button)buttonView.findViewById(R.id.modal_alert_cancel);
		addView(buttonView);
	}

	/**Return the button so that a click listener can be added to it*/
	@Override
	public Button getOkButton() {
		return mainCallToActionButton;
	}

	/**Return the button so that a click listener can be added to it*/
	@Override
	public Button getCancelButton() {
		return cancelButton;
	}

	/**
	 * Set the text of the button.
	 * @param resource - the string resource of the text to set the button to.
	 */
	@Override
	public void setOkButtonText(final int resource) {
		mainCallToActionButton.setText(context.getResources().getString(resource));
	}

	/**
	 * Set the text of the button.
	 * @param resource - the string resource of the text to set the button to.
	 */
	@Override
	public void setCancelButtonText(final int resource) {
		cancelButton.setText(context.getResources().getString(resource));
	}
}
