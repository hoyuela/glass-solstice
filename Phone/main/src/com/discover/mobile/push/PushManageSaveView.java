package com.discover.mobile.push;

import android.content.Context;
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
		final TextView textView1 = (TextView) mainView.findViewById(R.id.terms_of_use_view_1);
		final TextView textView2 = (TextView) mainView.findViewById(R.id.terms_of_use_view_2);
		//FIXME: fix the ugly layout
		final TextView textView3 = (TextView) mainView.findViewById(R.id.terms_of_use_view_3);

		mainView.removeAllViews();
		addView(save);
		addView(textView1);
		addView(textView2);
		addView(textView3);
	}

}
