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
import com.discover.mobile.common.ui.modals.SimpleContentModal;

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
public class EnhancedContentModal extends SimpleContentModal {

    private Runnable backAction = null;

    public EnhancedContentModal(final Context context, final int title,
            final int content, final int buttonText) {
        this(context, title, content, buttonText, null, null);
    }

    public EnhancedContentModal(final Context context, final int title,
            final int content, final int buttonText, final Runnable buttonAction) {
        this(context, title, content, buttonText, buttonAction, null);
    }

    public EnhancedContentModal(final Context context, final int title,
            final int content, final int buttonText,
            final Runnable buttonAction, final Runnable backButtonAction) {
        super(context);
        setTitle(title);
        setContentHtml(content);
        setButtonText(buttonText);
        setOkButton(buttonAction);
        backAction = backButtonAction;
    }

    public void setOkButton(final Runnable okAction) {
        getButton().setOnClickListener(new MyClickListener(okAction));
    }

    public void setOrangeTitle() {
        ((TextView) view.findViewById(R.id.modal_alert_title))
                .setTextColor(getContext().getResources().getColor(
                        R.color.orange));
    }

    public void setGrayButton() {
        getButton().setBackgroundColor(
                getContext().getResources()
                        .getColor(R.color.action_button_gray));
        getButton().setTextColor(
                getContext().getResources().getColor(R.color.black));
    }

    public void setContentHtml(final int content) {
        setContentHtml(getContext().getResources().getString(content));
    }

    public void setContentHtml(final String content) {
        TextView tv = (TextView) view.findViewById(R.id.modal_alert_text);
        tv.setText(Html.fromHtml(content));
        Linkify.addLinks(tv, Linkify.PHONE_NUMBERS);
        stripUnderlines(tv);
    }

    private class URLSpanNoUnderline extends URLSpan {
        public URLSpanNoUnderline(final String url) {
            super(url);
        }

        @Override
        public void updateDrawState(final TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }

    private void stripUnderlines(final TextView textView) {
        if (!(textView.getText() instanceof Spannable)) {
            return;
        }
        Spannable s = (Spannable) textView.getText();
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span : spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        textView.setText(s);
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

    @SuppressWarnings("deprecation")
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (backAction != null) {
            backAction.run();
        }
        Log.v("EnhancedContent onBackPressed", "Back was pressed");
    }

}
