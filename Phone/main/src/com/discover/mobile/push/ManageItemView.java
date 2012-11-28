package com.discover.mobile.push;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.R;

public class ManageItemView extends RelativeLayout{
	
	//TODO:  These will probably change
	private CheckBox textAlert;
	
	private CheckBox pushAlert;

	public ManageItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		final RelativeLayout mainView = (RelativeLayout) (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.notifcation_alert_toggle_item, null);
		final TextView header = (TextView) mainView.findViewById(R.id.header);
		mainView.removeAllViews();
        addView(header);
		//textAlert = (CheckBox) mainView.findViewById(R.id.checkBox1);
		//pushAlert = (CheckBox) mainView.findViewById(R.id.checkBox2);
	}

	
	public void setHeader(String header){
		//final TextView headerView = (TextView) findViewById(R.id.header);
		//headerView.setText(header);
	}
	
	public void setText(String text){
		//final TextView textView = (TextView) findViewById(R.id.text_secondary);
		//textView.setText(text);
	}
	
	public boolean isTextAlertEnabled(){
		return textAlert.isChecked();
	}
	
	public boolean isPushAlertEnabled(){
		return pushAlert.isChecked();
	}

}
