package com.discover.mobile.card.common.ui.modals;

import android.content.Context;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.discover.mobile.card.R;
import com.discover.mobile.common.ui.modals.SimpleContentModal;

public class EnhancedContentModal extends SimpleContentModal{

	private Runnable backAction = null;
	
//	public EnhancedContentModal(Context context) {
//		super(context);
//		// TODO Auto-generated constructor stub
//	}

	public EnhancedContentModal(final Context context, 
			final int title, final int content,
			final int buttonText) {
		this(context, title, content, buttonText, null, null);
	}
	
	public EnhancedContentModal(final Context context, 
			final int title, final int content,
			final int buttonText, Runnable buttonAction) {
		this(context, title, content, buttonText, buttonAction, null);
	}

	public EnhancedContentModal(final Context context, 
			final int title, final int content,
			final int buttonText, Runnable buttonAction, Runnable backButtonAction) {
		super(context);
		setTitle(title);
		setContentHtml(content);
		setButtonText(buttonText);
		setOkButton(buttonAction);
		this.backAction = backButtonAction;
	}

	public void setOkButton(Runnable okAction) {
		getButton().setOnClickListener(new MyClickListener(okAction));
	}
	
//	public void setBackButton(Runnable backAction){
//		super.setOnKeyListener(new MyKeyListener(backAction));
//	}
	
	public void setOrangeTitle() {
		((TextView) view.findViewById(R.id.modal_alert_title)).setTextColor(getContext().getResources().getColor(R.color.orange));
	}
	
	public void setGrayButton() {
		getButton().setBackgroundColor(getContext().getResources().getColor(R.color.action_button_gray));
		getButton().setTextColor(getContext().getResources().getColor(R.color.black));
	}
	
	public void setContentHtml(final int content){
		setContentHtml(getContext().getResources().getString(content));
	}

	public void setContentHtml(String content){
		TextView tv = ((TextView) view.findViewById(R.id.modal_alert_text));
		tv.setText(Html.fromHtml(content));
        Linkify.addLinks(tv, Linkify.PHONE_NUMBERS);
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
