package com.discover.mobile.card.privacyterms;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.discover.mobile.card.common.ui.CardNotLoggedInCommonActivity;
import com.discover.mobile.card.common.utils.Utils;

import com.discover.mobile.card.R;
import com.discover.mobile.card.error.CardErrHandler;

public class PrivacyTermsofUse extends CardNotLoggedInCommonActivity implements
        OnClickListener {

    private final String referer = "privacyPolicy-pg";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_terms_statement);

        final TextView pageTitle = (TextView) findViewById(R.id.page_title);
        final TextView content = (TextView) findViewById(R.id.content_text_view);
        final TextView footer = (TextView) findViewById(R.id.provide_feedback_button);

        content.setText(Html.fromHtml(this.getResources().getString(
                R.string.card_terms_of_use)));
        pageTitle.setText(Html.fromHtml(this.getResources().getString(
                R.string.card_terms_of_use_title)));
        content.setMovementMethod(LinkMovementMethod.getInstance());
        footer.setOnClickListener(this);

        setActionBarTitle(R.string.privacyTerms);

    }

    @Override
    public void onSuccess(Object data) {
        // TODO Auto-generated method stub

    }

    @Override
    public void OnError(Object data) {
        // TODO Auto-generated method stub

    }

    @Override
    public CardErrHandler getCardErrorHandler() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setActionBarTitle(final int stringResource) {
        // Hide the title image in the action bar.
        ((ImageView) findViewById(R.id.action_bar_discover_logo))
                .setVisibility(View.GONE);

        // Show title text with string resource.
        final TextView titleText = (TextView) findViewById(R.id.logged_out_title_view);
        titleText.setText(this.getString(stringResource));
        titleText.setVisibility(View.VISIBLE);

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.provide_feedback_button) {
            Utils.createProvideFeedbackDialog(this, referer);
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent privacyHome  =  new Intent(PrivacyTermsofUse.this, PrivacyTermsLanding.class);
        startActivity(privacyHome);
        finish();
    }
}
