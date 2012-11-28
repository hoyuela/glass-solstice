package com.discover.mobile.push;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.discover.mobile.R;

public class SaveChangesView extends RelativeLayout{
	
	private Button saveButton;

	public SaveChangesView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		final RelativeLayout mainView = (RelativeLayout) LayoutInflater.from(context)
											.inflate(R.layout.notification_save_list_item, null);
											
		saveButton = (Button)mainView.findViewById(R.id.notificaiton_save_button);
		
		//TODO: Find how to use the save button
	}

}
