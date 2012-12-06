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

	public PushManageSaveView(final Context context, final AttributeSet attrs) {
		super(context, attrs);


		final RelativeLayout mainView = (RelativeLayout) LayoutInflater.from(context)
                .inflate(R.layout.push_manage_save_item, null);

		final Button save = (Button) mainView.findViewById(R.id.notificaiton_save_button);
		final TextView text = (TextView) mainView.findViewById(R.id.terms_of_use_view);

		mainView.removeAllViews();
		addView(save);
		addView(text);
	}

}
