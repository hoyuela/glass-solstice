package com.discover.mobile.push.manage;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.R;

/**
 * View representing the save item in the push notification manage screen.
 *  
 * @author jthornton
 *
 */
public class PushManageSaveView extends RelativeLayout{

	/**
	 * Creates the view, including removing the child elements from their parent view and adding them to the
	 * current view being displayed
	 * @param context - activity context
	 * @param attrs - attributes of the current layout
	 */
	public PushManageSaveView(final Context context, final AttributeSet attrs) {
		super(context, attrs);


		final RelativeLayout mainView = (RelativeLayout) LayoutInflater.from(context)
                .inflate(R.layout.push_manage_save_item, null);

		final Button save = (Button) mainView.findViewById(R.id.notification_save_button);
		final TextView termsView = (TextView) mainView.findViewById(R.id.terms_of_use_view_1);
		final TextView clickableView = (TextView) mainView.findViewById(R.id.clickable_view);
		
		termsView.setText(Html.fromHtml(context.getResources().getString(R.string.terms_text_manage)));
		
		
		mainView.removeAllViews();
		addView(save);
		addView(termsView);
		addView(clickableView);
	}

}
