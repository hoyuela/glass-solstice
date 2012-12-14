package com.discover.mobile.alert;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.discover.mobile.R;

/**
 * Modal alert used for the application.
 * 
 * @author jthornton
 *
 */
public class LogoutModalAlert extends BaseModalAlert{
	
	public LogoutModalAlert(final Context context) {
		super(context);
	}

	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		final View mainView = this.getLayoutInflater().inflate(R.layout.modal_alert_layout, null);
		this.setContentView(mainView);
		
		title = (TextView) mainView.findViewById(R.id.modal_alert_title);
		content = (TextView) mainView.findViewById(R.id.modal_alert_text);
		divider = (ImageView) mainView.findViewById(R.id.modal_alert_divider);
		ok = (Button) mainView.findViewById(R.id.modal_alert_ok);
		cancel = (TextView) mainView.findViewById(R.id.modal_alert_cancel);
		checkbox = (ImageView) mainView.findViewById(R.id.show_again);
		
		hideAll();
		showDefaultViews();
		setTitle(res.getString(R.string.logout_confirm_title));
		setContent(res.getString(R.string.logout_confirm_text));
		setOkButtonText(res.getString(R.string.logout_ok_button_text));
		setCancelButtonText(res.getString(R.string.logout_cancel_button_text));
		
		cancel.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(final View view) {
				dismiss();
			}		
		});
		
		checkbox.setOnClickListener(getToggleListener());
	}	
		
	private View.OnClickListener getToggleListener(){
		return new View.OnClickListener(){

			@Override
			public void onClick(final View v) {
				final ImageView toggleImage = (ImageView) v;
				if(toggleImage.getId() == checkbox.getId()){
					toggleCheckBox();
				} else{
					toggleCheckBox();
				}
			}
			
		};
	}
}
