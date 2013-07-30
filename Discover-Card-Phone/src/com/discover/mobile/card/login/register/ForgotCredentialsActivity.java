package com.discover.mobile.card.login.register;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.discover.mobile.card.CardSessionContext;
import com.discover.mobile.card.R;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.card.privacyterms.PrivacyTermsLanding;
import com.discover.mobile.common.NotLoggedInRoboActivity;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.error.ErrorHandler;
import com.discover.mobile.common.facade.FacadeFactory;
import com.discover.mobile.common.utils.CommonUtils;

/**
 * This class handles a user's choice and navigation to the first step of forgot
 * password/user id/ or both. It contains a list of choices, upon tapping a
 * choice, this activity is finished and the selected activity is started.
 * 
 * @author scottseward
 * 
 */
public class ForgotCredentialsActivity extends NotLoggedInRoboActivity
        implements OnClickListener {

    private ArrayAdapter<Option> optionAdapter;
    private static final String REFERER = "forgot-uid-or-password-menu-pg";
    final Activity currentContext = this;
    protected TextView helpNumber;
    protected TextView provideFeedback;
    // Defect id 95853
    protected TextView privacy_terms;

    // Defect id 95853

    /**
     * Load list options into the list and setup an OnClickListener to wait for
     * list selections.
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_forgot_type_select);
        final ListView choicesList = (ListView) findViewById(android.R.id.list);
        optionAdapter = new ArrayAdapter<Option>(this,
                R.layout.register_forgot_type_select_option_item,
                android.R.id.text1, Option.values());
        helpNumber = (TextView) findViewById(R.id.help_number_label);
        provideFeedback = (TextView) findViewById(R.id.provide_feedback_button);
        // Defect id 95853
        privacy_terms = (TextView) findViewById(R.id.privacy_terms);
        provideFeedback.setOnClickListener(this);
        privacy_terms.setOnClickListener(this);
        // Defect id 95853

        choicesList.setAdapter(optionAdapter);
        setListViewHeightBasedOnChildren(choicesList);

        choicesList.setOnItemClickListener(new OnItemClickListener() {

            /**
             * When a item is selected in the list, get the position that was
             * pressed, select the option object at that position, start the
             * activity associated with that object, then finish this activity.
             */
            @Override
            public void onItemClick(final AdapterView<?> arg0, final View arg1,
                    final int arg2, final long arg3) {
                final Option selection = optionAdapter.getItem(arg2);
                final Class<?> intentClass = selection.getIntentClass();
                if (selection == Option.BOTH) {
                    CardSessionContext.getCurrentSessionDetails()
                            .setForgotCreds(true);
                }
                startActivity(new Intent(currentContext, intentClass));
                // endActivity();
            }
        });
        setupClickablePhoneNumbers();

        TrackingHelper.trackPageView(AnalyticsPage.FORGOT_PASSWORD_MENU);

    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * Make the help number clickable and dialable.
     */
    protected void setupClickablePhoneNumbers() {
        final Context currentContext = this;
        helpNumber.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                CommonUtils.dialNumber(helpNumber.getText().toString(),
                        currentContext);
            }
        });
    }

    /**
     * An enumerated type that contains list items and the class/activity that
     * they start upon selection.
     * 
     * @author scottseward
     * 
     */
    private static enum Option {
        USER_ID("Forgot User ID", ForgotUserIdActivity.class), PASSWORD(
                "Forgot Password",
                ForgotPasswordAccountInformationActivity.class), BOTH(
                "Forgot Both", ForgotBothAccountInformationActivity.class);

        private final String ITEM_TEXT;
        private final Class<?> INTENT_CLASS;

        Option(final String itemText,
                final Class<? extends Activity> intentClass) {
            ITEM_TEXT = itemText;
            INTENT_CLASS = intentClass;
        }

        /**
         * Returns a String object containing the text of the list item.
         */
        @Override
        public String toString() {
            return ITEM_TEXT;
        }

        /**
         * Returns the class/activity associated with this list option.
         * 
         * @return - the class/activity associated with this list item
         */
        private Class<?> getIntentClass() {
            return INTENT_CLASS;
        }
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    @Override
    public void goBack() {
        FacadeFactory.getLoginFacade().navToLogin(this);
        finish();
    }

    @Override
    public TextView getErrorLabel() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<EditText> getInputFields() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void showCustomAlert(final AlertDialog alert) {
        // TODO Auto-generated method stub

    }

    @Override
    public void showOneButtonAlert(final int title, final int content,
            final int buttonText) {
        // TODO Auto-generated method stub

    }

    @Override
    public void showDynamicOneButtonAlert(final int title,
            final String content, final int buttonText) {
        // TODO Auto-generated method stub

    }

    @Override
    public Context getContext() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setLastError(final int errorCode) {
        // TODO Auto-generated method stub

    }

    @Override
    public int getLastError() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return null;
    }

    @Override
    public void onClick(final View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.provide_feedback_button) {
        	
            Utils.createProvideFeedbackDialog(ForgotCredentialsActivity.this,
                    REFERER);
            
           /*  Intent newsampel = new Intent(ForgotCredentialsActivity.this ,
             CreateLoginActivity.class ); startActivity(newsampel);*/

            // Defect id 95853
        } else if (v.getId() == R.id.privacy_terms) {

            // Changes for 13.4 start
            // FacadeFactory.getBankFacade().navToCardPrivacyTerms();
            Intent privacyTerms = new Intent(ForgotCredentialsActivity.this,
                    PrivacyTermsLanding.class);
            startActivity(privacyTerms);
            // Changes for 13.4 end
        }
        // Defect id 95853
    }

}
