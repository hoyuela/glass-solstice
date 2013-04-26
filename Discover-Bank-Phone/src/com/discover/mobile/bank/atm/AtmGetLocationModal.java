/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.atm;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.R;
import com.discover.mobile.common.ui.modals.ModalTopView;

/**
 * The get location modal that is displayed when the application would like to get the
 * users current location in the ATM locator activity.
 * @author jthornton
 *
 */
public class AtmGetLocationModal extends RelativeLayout implements ModalTopView{

	/**
	 * Constructor for the modal
	 * @param context - activity context
	 * @param attrs - attributes
	 */
	public AtmGetLocationModal(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		final View v = inflate(context, R.layout.atm_get_location_modal, null);
		addView(v);
	}

	/**
	 * Get the allow button
	 * @return the allow button
	 */
	public Button getAllow(){
		return (Button)this.findViewById(R.id.modal_ok_button);
	}

	/**
	 * Get the dont allow button
	 * @return the dont allow button
	 */
	public Button getDontAllow(){
		return (Button)this.findViewById(R.id.modal_alert_cancel);
	}

	/**
	 * Get the text view holding the text
	 * @return the text view holding the text
	 */
	public TextView getContentView(){
		return (TextView) this.findViewById(R.id.modal_alert_text);
	}

	@Override
	public void setTitle(final int resource) {}

	@Override
	public void setTitle(final String text) {}

	@Override
	public void setContent(final int resouce) {}

	@Override
	public void setContent(final String content) {}

}
