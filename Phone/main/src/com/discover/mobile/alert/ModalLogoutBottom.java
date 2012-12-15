package com.discover.mobile.alert;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.discover.mobile.R;

/**
 * Custom logout bottom modal.  
 * @author jthornton
 *
 */
public class ModalLogoutBottom extends RelativeLayout implements ModalBottomTwoButtonView{
	
	/**Confirm logout button in the layout*/
	private Button ok;
	
	/**Stay logged in button in the layout*/
	private Button cancel;
	
	/**Checkbox for the show the logout alert again toggle*/
	private ImageView checkbox;
	
	/**Boolean holding the sate of the checkbox*/
	private boolean isTextChecked = false;
	
	/**Resources for the view*/
	private Resources res;

	/**
	 * Constructor for the view
	 * @param context - activity context
	 * @param attrs - attributes to set in the layout
	 */
	public ModalLogoutBottom(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		
		final RelativeLayout mainView = (RelativeLayout) LayoutInflater.from(context)
                .inflate(R.layout.modal_logout_bottom, null);
		
		res = context.getResources();
		
		final ImageView divider = (ImageView) mainView.findViewById(R.id.modal_alert_divider);
		final LinearLayout rememberLayout = (LinearLayout) mainView.findViewById(R.id.show_view);
		ok = (Button) mainView.findViewById(R.id.modal_alert_ok);
		cancel = (Button) mainView.findViewById(R.id.modal_alert_cancel);
		
		mainView.removeAllViews();
		addView(divider);
		addView(rememberLayout);
		addView(ok);
		addView(cancel);
	}
	
	/**
	 * Toggle the checkbox
	 */
	public void toggleCheckBox(){
		toggleBox(isTextChecked);
		isTextChecked = (isTextChecked) ? false : true;
	}
	
	/**
	 * Toggle the checkbox drawables
	 * @param isChecked - boolean if the box is already checked
	 */
	public void toggleBox(final boolean isChecked){
		if(isChecked){
			checkbox.setBackgroundDrawable(res.getDrawable(R.drawable.gray_gradient_square));
			checkbox.setImageDrawable(res.getDrawable(R.drawable.transparent_square));
		} else{
			checkbox.setBackgroundDrawable(res.getDrawable(R.drawable.black_gradient_square));
			checkbox.setImageDrawable(res.getDrawable(R.drawable.white_check_mark));
		}
	}
	
	/**
	 * Return if the checkbox is checked
	 * @return if the checkbox is checked
	 */
	public boolean isShowAgainSelected(){
		return isTextChecked;
	}
	
	/**
	 * Get the ok button
	 * @return the ok button
	 */
	@Override
	public Button getOkButton(){
		return ok;
	}
	
	/**
	 * Get the cancel button
	 * @return the cancel button
	 */
	@Override
	public Button getCancelButton(){
		return cancel;
	}

	/**
	 * Set the ok button text
	 * @param resource - the resource id of the string to be displayed
	 */
	@Override
	public void setOkButtonText(final int resource) {
		ok.setText(res.getString(resource));
	}

	/**
	 * Set the cancel button text
	 * @param resource - the resource id of the string to be displayed
	 */
	@Override
	public void setCancelButtonText(final int resource) {
		cancel.setText(res.getString(resource));
	}
}
