package com.discover.mobile.alert;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.discover.mobile.R;

public class SingleOrangeButtonBottom extends RelativeLayout implements ModalBottomOneButtonView{
	
	final Context context;
	
	final Button button;

	public SingleOrangeButtonBottom(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		
		final RelativeLayout mainView = 
				(RelativeLayout) LayoutInflater.from(context).inflate(R.layout.single_orange_button_bottom, null);
		this.context = context;
		button = (Button) mainView.findViewById(R.id.orange_button);
		addView(mainView);
	}

	/**
	 * Get the button
	 */
	@Override
	public Button getButton() {
		return button;
	}

	/**
	 * Set the button text
	 * @param resource - resource int value to place in the button
	 */
	@Override
	public void setButtonText(final int resource) {
		button.setText(context.getResources().getString(resource));		
	}

}
