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

public class PrivacyTermsLanding extends CardNotLoggedInCommonActivity implements OnClickListener{

    private final String referer =  "privacyPolicy-pg" ;
    private Bundle extras;
    private boolean is_enhance =false ;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_terms_landing);
        
        final List<String> values = new ArrayList<String>();
        values.add(getResources().getString(R.string.card_terms_privacy_statement));
        values.add(getResources().getString(R.string.card_terms_of_use_heading));
        
        final LinearLayout list = (LinearLayout)findViewById(R.id.terms_list);
        final TextView footer = (TextView)findViewById(R.id.provide_feedback_button);

        // Build the linear layout table.
        for (int i = 0; i < values.size(); ++i) {

           
                insertDividerLine(list);
            
                LayoutInflater inflater =
                        (LayoutInflater)this.getSystemService( this.LAYOUT_INFLATER_SERVICE );

            // Set the text of the section
            final RelativeLayout item = (RelativeLayout) inflater.inflate(R.layout.single_item_table_cell, null);
            final TextView label = (TextView) item.findViewById(android.R.id.text1);
            label.setText(values.get(i));
            item.setOnClickListener(getCardListClickListener(item));
            ((TextView)item.findViewById(android.R.id.text1)).setTextColor(getResources().getColor(R.color.blue_link));

            // Add the constructed list item to the table.
            list.addView(item);
        }
        setActionBarTitle(R.string.privacyTerms);
        footer.setOnClickListener(this);
        
        extras = getIntent().getExtras();
        if(extras!=null)
        {
           is_enhance= extras.getBoolean("is_enhance")  ;
            
        }
        
    }

   
    private void insertDividerLine(final LinearLayout view) {
        final View divider = new View(this, null);
        divider.setBackgroundResource(R.drawable.table_dotted_line);
        final LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 1);

        view.addView(divider, params);
    }
    
    private OnClickListener getCardListClickListener(final RelativeLayout listItem) {
        return new OnClickListener() {
            @Override
            public void onClick(final View v) {
                final TextView itemTitle = (TextView)listItem.findViewById(android.R.id.text1);
                navigateToCard(itemTitle.getText().toString());
            }
        };
    }
    
    private void navigateToCard(final String title) {
        final Resources res = getResources();

        if(title.equals(res.getString(R.string.card_terms_privacy_statement))){
            //BankConductor.navigateToCardMobilePrivacy();
            Intent privacyStatement  = new Intent(PrivacyTermsLanding.this , PrivacyTermsStatement.class);
            startActivity(privacyStatement);
            
        }else if(title.equals(res.getString(R.string.card_terms_of_use_heading))){
            //BankConductor.navigateToCardMobileTermsOfUse();
            Intent privacyStatement  = new Intent(PrivacyTermsLanding.this , PrivacyTermsofUse.class);
            startActivity(privacyStatement);
        }
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
        //Hide the title image in the action bar.
        ((ImageView)findViewById(R.id.action_bar_discover_logo)).setVisibility(View.GONE);

        //Show title text with string resource.
        final TextView titleText = (TextView)findViewById(R.id.logged_out_title_view);
        titleText.setText(this.getString(stringResource));
        titleText.setVisibility(View.VISIBLE);

    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if(v.getId() ==  R.id.provide_feedback_button)
        {
            Utils.createProvideFeedbackDialog(this, referer);
        }
    }


   
    
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        if(is_enhance && extras !=null)
        {
            Intent enhanceActivity = new Intent(
                    PrivacyTermsLanding.this,
                    EnhancedAccountSecurityActivity.class);
           
            enhanceActivity.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY) ;
            enhanceActivity.putExtra(IntentExtraKey.STRONG_AUTH_QUESTION, extras.getString(IntentExtraKey.STRONG_AUTH_QUESTION));
            enhanceActivity.putExtra(IntentExtraKey.STRONG_AUTH_QUESTION_ID, extras.getString(IntentExtraKey.STRONG_AUTH_QUESTION_ID));
            enhanceActivity.putExtra(EnhancedAccountSecurityActivity.FORGOT_BOTH_FLOW, extras.getBoolean(EnhancedAccountSecurityActivity.FORGOT_BOTH_FLOW));
            enhanceActivity.putExtra(EnhancedAccountSecurityActivity.FORGOT_PASSWORD_FLOW, extras.getBoolean(EnhancedAccountSecurityActivity.FORGOT_PASSWORD_FLOW));
            enhanceActivity.putExtra(EnhancedAccountSecurityActivity.ANSWER_ERROR_VISIBILITY, extras.getInt(EnhancedAccountSecurityActivity.ANSWER_ERROR_VISIBILITY));
            enhanceActivity.putExtra(EnhancedAccountSecurityActivity.ANSWER_ERROR_TEXT, extras.getString(EnhancedAccountSecurityActivity.ANSWER_ERROR_TEXT));
            enhanceActivity.putExtra("is_enhance",true);
            enhanceActivity.putExtra(EnhancedAccountSecurityActivity.YES_RADIOBUTTON_SEL, extras.getBoolean(EnhancedAccountSecurityActivity.YES_RADIOBUTTON_SEL));
            enhanceActivity.putExtra(EnhancedAccountSecurityActivity.ANSWER_TEXT, extras.getString(EnhancedAccountSecurityActivity.ANSWER_TEXT));
            
            startActivity(enhanceActivity);
        }
    }
   
}
