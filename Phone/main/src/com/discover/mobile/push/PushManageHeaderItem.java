package com.discover.mobile.push;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.R;


public class PushManageHeaderItem extends RelativeLayout{
	
	private LinearLayout list;
	
	private TextView hide;

	private final String hideString; 

	private final String showString;
	
	public PushManageHeaderItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		final RelativeLayout mainView = (RelativeLayout) LayoutInflater.from(context)
                .inflate(R.layout.push_manage_header_item, null);
		final Resources res = context.getResources();
		hideString = res.getString(R.string.hide_notification_list);
		showString = res.getString(R.string.show_notification_list);
		
		hide = (TextView) mainView.findViewById(R.id.hide_view);
		final TextView title = (TextView) mainView.findViewById(R.id.title);
		
		mainView.removeAllViews();
		addView(hide);
		addView(title);
		setClickListener();
	}
	
	public void setHeader(String header){
		final TextView title = (TextView) findViewById(R.id.title);
		title.setText(header);
	}
	
	private void setClickListener() {
		this.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				final String text = hide.getText().toString();
				if(hideString.equals(text)){
					hide.setText(showString);
					list.setVisibility(View.GONE);
				}else{
					hide.setText(hideString);
					list.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	public void setList(LinearLayout list){
		this.list = list;
	}
}
