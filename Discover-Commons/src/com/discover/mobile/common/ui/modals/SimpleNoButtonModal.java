package com.discover.mobile.common.ui.modals;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.discover.mobile.common.R;

public class SimpleNoButtonModal extends AlertDialog{

	private final View view;

	private final View content;

	public SimpleNoButtonModal(final Context context, final View layout) {
		super(context);
		view = getLayoutInflater().inflate(R.layout.simple_no_button_modal, null);
		content = layout;
		final LinearLayout container = (LinearLayout) view.findViewById(R.id.container);
		container.addView(content);
	}

	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(view);
	}

	public View getContent(){
		return content;
	}
}
