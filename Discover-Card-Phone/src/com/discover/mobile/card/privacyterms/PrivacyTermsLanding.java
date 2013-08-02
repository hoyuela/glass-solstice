package com.discover.mobile.card.privacyterms;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

import com.discover.mobile.common.IntentExtraKey;

import com.discover.mobile.card.common.ui.CardNotLoggedInCommonActivity;
import com.discover.mobile.card.common.utils.Utils;

import com.discover.mobile.card.R;
import com.discover.mobile.card.auth.strong.EnhancedAccountSecurityActivity;
import com.discover.mobile.card.error.CardErrHandler;

public class PrivacyTermsLanding extends CardNotLoggedInCommonActivity
        implements OnClickListener {

    private final String referer = "privacyPolicy-pg";
    private Bundle extras;
    private boolean is_enhance = false;
    private TextView privacyStatement, termsOfUse;
    private RelativeLayout privacyStatementRow, termsOfUseRow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_terms_landing);
        intialize();
        privacyStatement.setText(R.string.card_terms_privacy_statement);
        termsOfUse.setText(R.string.card_terms_of_use_heading);
        final TextView footer = (TextView) findViewById(R.id.provide_feedback_button);
        footer.setOnClickListener(this);
        privacyStatementRow.setOnClickListener(this);
        termsOfUseRow.setOnClickListener(this);
        setActionBarTitle(R.string.privacyTerms);
        extras = getIntent().getExtras();
        if (extras != null) {
            is_enhance = extras.getBoolean("is_enhance");
        }
    }

    private void intialize() {
        privacyStatementRow = (RelativeLayout) findViewById(R.id.privacystatementrow);
        termsOfUseRow = (RelativeLayout) findViewById(R.id.termsofuserow);
        privacyStatement = (TextView) privacyStatementRow
                .findViewById(R.id.leftText);
        termsOfUse = (TextView) termsOfUseRow.findViewById(R.id.leftText);

    }

    private void navigateToCard(final String title) {
        final Resources res = getResources();

        if (title.equals(res.getString(R.string.card_terms_privacy_statement))) {
            Intent privacyStatement = new Intent(PrivacyTermsLanding.this,
                    PrivacyTermsStatement.class);
            startActivity(privacyStatement);

        } else if (title.equals(res
                .getString(R.string.card_terms_of_use_heading))) {
            Intent privacyStatement = new Intent(PrivacyTermsLanding.this,
                    PrivacyTermsofUse.class);
            startActivity(privacyStatement);
        }
    }

    @Override
    public void onSuccess(Object data) {

    }

    @Override
    public void OnError(Object data) {

    }

    @Override
    public CardErrHandler getCardErrorHandler() {
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
        if (v.getId() == R.id.provide_feedback_button) {
            Utils.createProvideFeedbackDialog(this, referer);
        } else if (v.getId() == R.id.privacystatementrow) {
            navigateToCard(privacyStatement.getText().toString());
        } else if (v.getId() == R.id.termsofuserow) {
            navigateToCard(termsOfUse.getText().toString());
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (is_enhance && extras != null) {
            Intent enhanceActivity = new Intent(PrivacyTermsLanding.this,
                    EnhancedAccountSecurityActivity.class);

            enhanceActivity.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            enhanceActivity.putExtra(IntentExtraKey.STRONG_AUTH_QUESTION,
                    extras.getString(IntentExtraKey.STRONG_AUTH_QUESTION));
            enhanceActivity.putExtra(IntentExtraKey.STRONG_AUTH_QUESTION_ID,
                    extras.getString(IntentExtraKey.STRONG_AUTH_QUESTION_ID));
            enhanceActivity
                    .putExtra(
                            EnhancedAccountSecurityActivity.FORGOT_BOTH_FLOW,
                            extras.getBoolean(EnhancedAccountSecurityActivity.FORGOT_BOTH_FLOW));
            enhanceActivity
                    .putExtra(
                            EnhancedAccountSecurityActivity.FORGOT_PASSWORD_FLOW,
                            extras.getBoolean(EnhancedAccountSecurityActivity.FORGOT_PASSWORD_FLOW));
            enhanceActivity
                    .putExtra(
                            EnhancedAccountSecurityActivity.ANSWER_ERROR_VISIBILITY,
                            extras.getInt(EnhancedAccountSecurityActivity.ANSWER_ERROR_VISIBILITY));
            enhanceActivity
                    .putExtra(
                            EnhancedAccountSecurityActivity.ANSWER_ERROR_TEXT,
                            extras.getString(EnhancedAccountSecurityActivity.ANSWER_ERROR_TEXT));
            enhanceActivity.putExtra("is_enhance", true);
            enhanceActivity
                    .putExtra(
                            EnhancedAccountSecurityActivity.YES_RADIOBUTTON_SEL,
                            extras.getBoolean(EnhancedAccountSecurityActivity.YES_RADIOBUTTON_SEL));
            enhanceActivity
                    .putExtra(
                            EnhancedAccountSecurityActivity.ANSWER_TEXT,
                            extras.getString(EnhancedAccountSecurityActivity.ANSWER_TEXT));

            startActivity(enhanceActivity);
        }
    }

}
