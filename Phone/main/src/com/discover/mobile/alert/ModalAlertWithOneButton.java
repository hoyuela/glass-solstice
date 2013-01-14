package com.discover.mobile.alert;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

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
	 * An alternate way to create a modal alert with one button 
	 * by supplying content only
	 * 
	 * @param context 
	 * @param title - the title for the alert
	 * @param content - the body text for the alert
	 * @param buttonText - the button text for the alert
	 */
	public ModalAlertWithOneButton(final Context context, 
			final int title, final int content, 
			final int buttonText) {
		
		super(context);
		
		final ModalDefaultTopView topView = new ModalDefaultTopView(context, null);
		final ModalDefaultOneButtonBottomView bottomView = new ModalDefaultOneButtonBottomView(context, null);
		
		topView.setTitle(title);
		topView.setContent(content);
		bottomView.setButtonText(buttonText);
		bottomView.getButton().setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(final View v) {
				dismiss();
			}
			
		});
		
		this.top = topView;
		this.bottom = bottomView;
	}
	
	/**
	 * Constructor for the alert
	 * @param context - activity context
	 * @param top - top piece to be displayed
	 * @param bottom - bottom piece to be displayed
	 */
	public ModalAlertWithOneButton(final Context context, 
			final int title, final String content, 
			final int buttonText) {
		
		super(context);
		
		final ModalDefaultTopView topView = new ModalDefaultTopView(context, null);
		final ModalDefaultOneButtonBottomView bottomView = new ModalDefaultOneButtonBottomView(context, null);
		
		topView.setTitle(title);
		topView.setDynamicContent(content);
		bottomView.setButtonText(buttonText);
		bottomView.getButton().setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(final View v) {
				dismiss();
			}
			
		});
		
		this.top = topView;
		this.bottom = bottomView;
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
		if(null != top){
			linearLayout.addView((View)top);
		}
		if(null != bottom){
			linearLayout.addView((View)bottom);
		}
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
