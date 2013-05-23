package com.discover.mobile.common.ui.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

public class CustomOptionsMenu extends AlertDialog {
	private final View view;

	public CustomOptionsMenu(final Context context, final int resourceId) {
		super(context);
		view = getLayoutInflater().inflate(resourceId, null);
	}

	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(view);
	}

	public void addAnimation(final int resourceId) {
		getWindow().setWindowAnimations(resourceId);
	}

	public void addOnClickListener(final int resourceId, final View.OnClickListener actionListener) {
		final View viewToAddActionListener = view.findViewById(resourceId);
		viewToAddActionListener.setOnClickListener(actionListener);
	}
}
