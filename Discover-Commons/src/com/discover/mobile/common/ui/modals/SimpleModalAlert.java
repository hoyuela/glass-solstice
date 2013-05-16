package com.discover.mobile.common.ui.modals;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.discover.mobile.common.R;

/**
 * This class is a modal alert with only a top view (no bottom buttons).  This view will only show a title and
 * some content text (or some other variation).
 * 
 * @author jthornton
 *
 */
public class SimpleModalAlert extends AlertDialog{
	
	/**Top view too be displayed*/
	private final ModalTopView top;
	
	/**
	 * Constructor for the alert
	 * @param context - activity context
	 * @param top - top piece to be displayed
	 */
	public SimpleModalAlert(final Context context, final ModalTopView top) {
		super(context);
		this.top = top;
	}
	
	/**
	 * Create the modal alert and add the views to be displayed.
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		final View mainView = this.getLayoutInflater().inflate(R.layout.modal_alert_layout, null);
		this.setContentView(mainView);
		
		final LinearLayout linearLayout = (LinearLayout) mainView.findViewById(R.id.modal_linear_layout);
		linearLayout.addView((View)top);
	}

	/**
	 * Get the top piece so that it can be manipulated
	 * @return the top piece so that it can be manipulated
	 */
	public ModalTopView getTop(){
		return top;
	}
}	
