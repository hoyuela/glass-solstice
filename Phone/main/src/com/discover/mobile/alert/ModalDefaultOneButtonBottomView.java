package com.discover.mobile.alert;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.discover.mobile.R;

/**
 * Custom single bottom button for a modal view.
 * @author scottseward
 *
 */
public class ModalDefaultOneButtonBottomView extends RelativeLayout implements ModalBottomOneButtonView{

	/**The main call to action button in the bottom center of the dialog*/
	private Button mainCallToActionButton;

	/**Return the button so that a click listener can be added to it*/
	@Override
	public Button getButton() {
		return mainCallToActionButton;
	}

	/**
	 * Set the text of the button.
	 * @param resource - the string resource of the text to set the button to.
	 */
	@Override
	public void setButtonText(final int resource) {
		mainCallToActionButton.setText(getResources().getString(resource));
	}

	public ModalDefaultOneButtonBottomView(final Context context, final AttributeSet attrs) {
		super(context, attrs);

		final RelativeLayout buttonView = (RelativeLayout) LayoutInflater.from(context)
				.inflate(R.layout.modal_default_one_button_bottom, null);
		
		mainCallToActionButton = (Button)buttonView.findViewById(R.id.button);
		ImageView testImageView = (ImageView)buttonView.findViewById(R.id.modal_alert_divider);
		
		buttonView.removeAllViews();
		addView(buttonView);
		addView(testImageView);
		addView(mainCallToActionButton);
	}

}
