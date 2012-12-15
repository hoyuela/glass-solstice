package com.discover.mobile.alert;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.discover.mobile.R;

/**
 * This class is a modal alert with both a top view and a bottom view.  This view will only show a title and
 * some content text and a bottom with only one button.
 * 
 * @author jthornton
 *
 */
public class ModalAlertWithOneButton extends AlertDialog{
	
	/**Top view too be displayed*/
	private ModalTopView top;
	
	/**Bottom view to be displayed*/
	private ModalBottomOneButtonView bottom;
	
	/**
	 * Constructor for the alert
	 * @param context - activity context
	 * @param top - top piece to be displayed
	 * @param bottom - bottom piece to be displayed
	 */
	public ModalAlertWithOneButton(final Context context, 
			final ModalTopView top, 
			final ModalBottomOneButtonView bottom) {
		
		super(context);
		this.top = top;
		this.bottom = bottom;
	}
	
	/**
	 * Create the modal alert and add the views to be displayed.
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		final View mainView = this.getLayoutInflater().inflate(R.layout.modal_alert_layout, null);
		this.setContentView(mainView);
		
		final RelativeLayout linearLayout = (RelativeLayout) mainView.findViewById(R.id.modal_linear_layout);
		linearLayout.addView((View)top);
		linearLayout.addView((View)bottom);
	}

	/**
	 * Get the top piece so that it can be manipulated
	 * @return the top piece so that it can be manipulated
	 */
	public ModalTopView getTop(){
		return top;
	}
	
	/**
	 * Get the bottom piece so that it can be manipulated
	 * @return the bottom piece so that it can be manipulated
	 */
	public ModalBottomOneButtonView getBottom(){
		return bottom;
	}
}
