package com.discover.mobile.common.ui.modals;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.discover.mobile.common.R;

/**
 * Modal Alert used to show a modal with just a simple amount of content.  For
 * example a modal that only has content but no buttons.
 * 
 * @author jthornton
 *
 */
public class SimpleNoButtonModal extends AlertDialog{

	/**Main view of the modal*/
	private final View view;

	/**Content view of the modal*/
	private final View content;

	/**
	 * Constructor for the modal
	 * @param context - activity context
	 * @param layout - view to be place in the modal
	 */
	public SimpleNoButtonModal(final Context context, final View layout) {
		super(context);
		view = getLayoutInflater().inflate(R.layout.simple_no_button_modal, null);
		content = layout;
		final LinearLayout container = (LinearLayout) view.findViewById(R.id.container);
		container.addView(content);
	}

	/**
	 * Create the modal
	 * @param savedInstanceState - saved state of the modal
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(view);
	}

	/**
	 * Get the content view of the modal
	 * @return - the content view of the modal
	 */
	public View getContent(){
		return content;
	}
}
