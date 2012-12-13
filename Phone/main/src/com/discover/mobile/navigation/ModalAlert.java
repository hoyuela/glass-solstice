package com.discover.mobile.navigation;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import com.discover.mobile.R;

public class ModalAlert extends AlertDialog{

	protected ModalAlert(final Context context) {
		super(context);
		
		
	}
	
	public void onCreate(final Bundle savedInstanceState){
		this.setContentView(R.layout.modal_alert_layout);
	}
}
