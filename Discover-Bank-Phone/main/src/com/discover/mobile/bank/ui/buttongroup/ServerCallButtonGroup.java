package com.discover.mobile.bank.ui.buttongroup;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.discover.mobile.bank.R;

public class ServerCallButtonGroup extends LinearLayout{

	private final LinearLayout view;

	public ServerCallButtonGroup(final Context context, final AttributeSet attrs) {
		super(context, attrs);

		this.view = (LinearLayout)LayoutInflater.from(context).inflate(R.layout.server_call_button_group, null);
		addView(this.view);
	}

	public void addServerCallButton(final ServerCallButton button){
		//this.view.addView(button);
	}


}
