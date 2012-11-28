package com.discover.mobile.push;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class ManagePushNotificationAdapter extends ArrayAdapter{

	public ManagePushNotificationAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		return convertView;
		
	}
}
