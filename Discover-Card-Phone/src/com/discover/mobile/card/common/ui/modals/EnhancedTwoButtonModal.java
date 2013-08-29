package com.discover.mobile.card.common.ui.modals;

import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.discover.mobile.card.R;
import com.discover.mobile.common.ui.modals.SimpleTwoButtonModal;

/**
 * 
 * �2013 Discover Bank
 * 
 * Modal dialog for showing error message
 * 
 * @author CTS
 * 
 * @version 1.0
 */
public class EnhancedTwoButtonModal extends SimpleTwoButtonModal {

    private Runnable backAction = null;

    public EnhancedTwoButtonModal(final Context context) {
        super(context);
    }

    public EnhancedTwoButtonModal(final Context context, final int title,
            final int content, final int okButtonText,
            final int cancelButtonText) {
        this(context, title, content, okButtonText, cancelButtonText, null,
                null, null);
    }

    public EnhancedTwoButtonModal(final Context context, final int title,
            final int content, final int okButtonText,
            final int cancelButtonText, final Runnable okButtonAction,
            final Runnable cancelButtonAction) {
        this(context, title, content, okButtonText, cancelButtonText,
                okButtonAction, cancelButtonAction, null);
    }

    public EnhancedTwoButtonModal(final Context context, final int title,
            final int content, final int okButtonText,
            final int cancelButtonText, final Runnable okButtonAction,
            final Runnable cancelButtonAction, final Runnable backButtonAction) {
        super(context, title, content, okButtonText, cancelButtonText);
        setOkButton(okButtonAction);
        setCancelButton(cancelButtonAction);
        backAction = backButtonAction;
        setTitle(title);
		setContentHtml(content);
		this.backAction = backButtonAction;
    }

    public void setOkButton(final Runnable okAction) {
        getOkButton().setOnClickListener(new MyClickListener(okAction));
    }

    public void setCancelButton(final Runnable okAction) {
        getCancelButton().setOnClickListener(new MyClickListener(okAction));
    }

    // regular button
    private class MyClickListener implements View.OnClickListener {
        private String TAG = "SimpleContentButton";

        private Runnable action;

        public MyClickListener(final Runnable action) {
            this.action = action;
        }

        public Runnable getAction() {
            return action;
        }

        @Override
        public void onClick(final View v) {
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
        if (backAction != null) {
            backAction.run();
        }
        Log.v("EnhancedContent onBackPressed", "Back was pressed");
    }

	public void setContentHtml(final int content){
		setContentHtml(getContext().getResources().getString(content));
	}

	public void setContentHtml(String content){
		TextView tv = ((TextView) view.findViewById(R.id.modal_alert_text));
		tv.setText(Html.fromHtml(content));
        Linkify.addLinks(tv, Linkify.PHONE_NUMBERS);
        stripUnderlines(tv);
	}
	
	private class URLSpanNoUnderline extends URLSpan {
        public URLSpanNoUnderline(String url) {
            super(url);
        }
        @Override public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }
	
	private void stripUnderlines(TextView textView) {
		if (!(textView.getText() instanceof Spannable)) {
			return;
		}
        Spannable s = (Spannable)textView.getText();
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span: spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        textView.setText(s);
    }
}
