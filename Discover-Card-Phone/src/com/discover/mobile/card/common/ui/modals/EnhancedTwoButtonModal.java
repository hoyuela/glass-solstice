package com.discover.mobile.card.common.ui.modals;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.discover.mobile.common.ui.modals.SimpleTwoButtonModal;

public class EnhancedTwoButtonModal extends SimpleTwoButtonModal{

	private Runnable backAction = null;

	public EnhancedTwoButtonModal(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public EnhancedTwoButtonModal(final Context context, int title, int content, int okButtonText, int cancelButtonText){
		this(context, title, content, okButtonText, cancelButtonText, null, null, null);
	}

	public EnhancedTwoButtonModal(final Context context, int title, int content, int okButtonText, int cancelButtonText,
			Runnable okButtonAction, Runnable cancelButtonAction){
		this(context, title, content, okButtonText, cancelButtonText, okButtonAction, cancelButtonAction, null);
	}

	public EnhancedTwoButtonModal(final Context context, int title, int content, int okButtonText, int cancelButtonText,
			Runnable okButtonAction, Runnable cancelButtonAction, Runnable backButtonAction){
		super(context, title, content, okButtonText, cancelButtonText);
		setOkButton(okButtonAction);
		setCancelButton(cancelButtonAction);
		this.backAction = backButtonAction;
	}

	public void setOkButton(Runnable okAction) {
		getOkButton().setOnClickListener(new MyClickListener(okAction));
	}

	public void setCancelButton(Runnable okAction) {
		getCancelButton().setOnClickListener(new MyClickListener(okAction));
	}

	//regular button
	private class MyClickListener implements View.OnClickListener {
		private String TAG = "SimpleContentButton";

		private Runnable action;

		public MyClickListener(Runnable action) {
			this.action = action;
		}

		public Runnable getAction() {
			return action;
		}

		@Override
		public void onClick(View v) {
			Log.v(TAG, "About to execute action");
			if (getAction() != null) {
				Log.v(TAG, "Action executed!");
				getAction().run();
			}
			dismiss();
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (this.backAction != null) {
			this.backAction.run();
		}
		Log.v("EnhancedContent onBackPressed", "Back was pressed");
	}

}
