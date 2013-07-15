/* 13.3  Changes */

package com.discover.mobile.card.whatsnew;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.discover.mobile.card.R;

public class WhatsNewAvailable extends LinearLayout implements
        WhatsNewConstants {

    Context context;
    int pageID;
    String cardType;

    public WhatsNewAvailable(final Context context, final int pageID,
            final String cardType) {
        super(context);
        this.context = context;
        this.pageID = pageID;
        this.cardType = cardType;
        init();
    }

    public WhatsNewAvailable(final Context context) {
        super(context);
        this.context = context;
        init();
    }

    private void init() {
        /*
         * setBackgroundColor(Color.WHITE); Button button = new
         * Button(getContext()); button.setText("Click Me..");
         */
        final LayoutInflater vi = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (pageID) {
        case QUICK_VIEW_INFO:
            final View quickview = vi.inflate(R.layout.whats_new_layout1, null);
            setGravity(Gravity.CENTER);
            addView(quickview);
            setPageOneContent();
            break;
        case MANAGE_BANK_INFO:
            final View bankaccountview = vi.inflate(R.layout.whats_new_layout1,
                    null);
            setGravity(Gravity.CENTER);
            addView(bankaccountview);
            setPageOneContent();
            break;
        case MANAGE_PAYMENT_INFO:
            final View paymentview = vi.inflate(R.layout.whats_new_layout1,
                    null);
            setGravity(Gravity.CENTER);
            addView(paymentview);
            setPageOneContent();
            break;
        case PASSCODE_INFO:
            final View passcodeView = vi.inflate(R.layout.whats_new_layout1,
                    null);
            setGravity(Gravity.CENTER);
            addView(passcodeView);
            setPageOneContent();
            break;
        default:
            break;
        }

    }

    private void setPageOneContent() {
        TextView title;
        ImageView imageView;
        TextView contentDescription;
        TextView linkLabel;
        title = (TextView) findViewById(R.id.whats_new_content_heading);
        imageView = (ImageView) findViewById(R.id.whats_new_image);
        contentDescription = (TextView) findViewById(R.id.whats_new_content_description_label);
        linkLabel = (TextView) findViewById(R.id.whats_new_label);

        switch (pageID) {
        case QUICK_VIEW_INFO:
            title.setText(R.string.quickview_content_heading);
            linkLabel.setText(R.string.quickview_gotolink);
            if (cardType.equalsIgnoreCase("CBB")
                    || cardType.equalsIgnoreCase("SBC")) {
                contentDescription
                        .setText(R.string.quickview_content_description_cbb);
                imageView.setImageDrawable(getResources().getDrawable(
                        R.drawable.quick_view));
            } else if (cardType.equalsIgnoreCase("NOR")) {
                contentDescription
                        .setText(R.string.quickview_content_description_dbc_corp);
                imageView.setImageDrawable(getResources().getDrawable(
                        R.drawable.quick_view));

            } else {
                contentDescription
                        .setText(R.string.quickview_content_description_mi);
                imageView.setImageDrawable(getResources().getDrawable(
                        R.drawable.quick_view));
            }
            break;
        case MANAGE_BANK_INFO:
            title.setText(R.string.manage_bankacc_content_heading);
            linkLabel.setText(R.string.manage_bankacc_gotolink);
            contentDescription
                    .setText(R.string.manage_bankacc_content_description);
            imageView.setImageDrawable(getResources().getDrawable(
                    R.drawable.manage_bank_account));
            break;
        case MANAGE_PAYMENT_INFO:
            title.setText(R.string.manage_payment_content_heading);
            linkLabel.setText(R.string.manage_payment_gotolink);
            contentDescription
                    .setText(R.string.manage_payment_content_description);
            imageView.setImageDrawable(getResources().getDrawable(
                    R.drawable.manage_payment));
            break;
        case PASSCODE_INFO:
            title.setText(R.string.passcode_content_heading);
            linkLabel.setText(R.string.passcode_gotolink);
            contentDescription.setText(R.string.passcode_content_description);
            imageView.setImageDrawable(getResources().getDrawable(
                    R.drawable.passcode));
            break;
        }

    }

}