package com.discover.mobile.card.common.ui.modals;

import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.discover.mobile.common.ui.help.NeedHelpFooter;

import com.discover.mobile.card.R;

/**
 * Default top view to be displayed in the alert modal. This contains a title as
 * well as some content text to be displayed under the text view.
 * 
 * @author jthornton
 * 
 */
public class ModalDefaultTopView extends ScrollView implements ModalTopView {

    /** Resources for showing strings */
    private Resources res;

    /** Optional error image to be placed to the left of the title */
    private ImageView errorImage;

    /** View that holds the title */
    private TextView title;

    /** View that holds the content text */
    private TextView text;

    private TextView feedback;

    /** View that holds the footer text */
    private NeedHelpFooter helpFooter;

    /**
     * Constructor for the view
     * 
     * @param context
     *            - activity context
     * @param attrs
     *            - attributes to apply to the layout
     */
    public ModalDefaultTopView(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        final View mainView = (View) LayoutInflater.from(context).inflate(
                R.layout.modal_default_top_view, null);

        res = context.getResources();

        errorImage = (ImageView) mainView.findViewById(R.id.error_icon);
        title = (TextView) mainView.findViewById(R.id.modal_alert_title);
        text = (TextView) mainView.findViewById(R.id.modal_alert_text);
        helpFooter = new NeedHelpFooter((ViewGroup) mainView);
        feedback = (TextView) mainView.findViewById(R.id.modal_feedback_text);
        addView(mainView);
    }

    /**
     * Set the title of the view
     * 
     * @param resource
     *            - int representing the resource to be displayed
     */
    @Override
    public void setTitle(final int resource) {
        title.setText(res.getString(resource));
    }

    /**
     * Set the text in the title view
     * 
     * @param text
     *            - String with text to be displayed as the title
     */
    @Override
    public void setTitle(final String text) {
        title.setText(text);
    }

    /**
     * Set the content of the view
     * 
     * @param resource
     *            - int representing the resource to be displayed
     */
    @Override
    public void setContent(final int resource) {
        text.setText(res.getString(resource));
    }

    /**
     * Set the text in the content view
     * 
     * @param content
     *            - String with text to be displayed as the message
     */
    public void setContent(final String content) {
        text.setText(Html.fromHtml(content));
        Linkify.addLinks(text, Linkify.PHONE_NUMBERS);
        text.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * Show an error icon to the left of the modal dialog title.
     * 
     * @param isError
     *            - tells the dialog to show an error icon or not.
     */
    public void showErrorIcon(final boolean isError) {
        if (isError) {
            errorImage.setVisibility(View.VISIBLE);
        } else {
            errorImage.setVisibility(View.GONE);
        }

    }

    /**
     * Set the content of the view with dynamic text
     * 
     * DO NOT USE WITH STATIC TEXT, PLEASE USE INT METHOD and pull from resource
     * file
     * 
     * @param resource
     *            - int representing the resource to be displayed
     */
    public void setDynamicContent(final String content) {
        text.setText(content);
    }

    /**
     * @return Returns the NeedHelpFooter wrapper instance which allows to set
     *         the footer help number
     */
    public NeedHelpFooter getHelpFooter() {
        return helpFooter;
    }

    /**
     * Hide the help footer
     */
    public void hideNeedHelpFooter() {
        helpFooter.show(false);
    }

    /**
     * 
     * @return returns the feed back textview instance
     */
    public TextView getFeedbackTextView() {
        return feedback;
    }

    /**
     * Hide the feed back text view
     */
    public void hideFeedbackView() {

        feedback.setVisibility(View.GONE);
    }

}
