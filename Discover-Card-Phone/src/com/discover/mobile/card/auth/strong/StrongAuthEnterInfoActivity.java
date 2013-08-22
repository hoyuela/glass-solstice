package com.discover.mobile.card.auth.strong;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.common.BaseFragment;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;

import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.SessionCookieManager;
import com.discover.mobile.card.common.net.error.CardErrorBean;
import com.discover.mobile.card.common.net.error.CardErrorResponseHandler;
import com.discover.mobile.card.common.net.error.CardErrorUIWrapper;
import com.discover.mobile.card.common.sessiontimer.PageTimeOutUtil;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;
import com.discover.mobile.card.common.ui.modals.ModalAlertWithOneButton;
import com.discover.mobile.card.common.ui.modals.ModalTopView;
import com.discover.mobile.card.common.uiwidget.AnswerEditText;
import com.discover.mobile.card.common.uiwidget.ConfirmationAnswerEditText;
import com.discover.mobile.card.common.utils.Utils;

import com.discover.mobile.card.R;
import com.discover.mobile.card.error.CardErrHandler;
import com.discover.mobile.card.error.CardErrorHandlerUi;
import com.discover.mobile.card.navigation.CardNavigationRootActivity;
import com.discover.mobile.card.privacyterms.PrivacyTermsLanding;
import com.discover.mobile.card.services.auth.strong.StrongAuthCreateUserDetails;
import com.discover.mobile.card.services.auth.strong.StrongAuthReviewQueAnsDetails;
import com.discover.mobile.card.services.auth.strong.StrongAuthUpdateUser;

import com.fasterxml.jackson.databind.JsonNode;


/***
 * Strong Auth Enrollment. On Login if user has not done strong auth enrollment 
 * .User will be navigated to this activity for enrollment
 * 
 * 
 * @author 228218
 * 
 */
public class StrongAuthEnterInfoActivity extends Activity implements
        OnClickListener, CardErrorHandlerUi {

    // for saving the State on Orientation change
    private static final String UPDATE_FIRST_ANSWER_STATE = "a";
    private static final String UPDATE_SECOND_ANSWER_STATE = "b";
    private static final String UPDATE_THIRD_ANSWER_STATE = "c";
    private static final String UPDATE_FIRST_CONFIRM_ANSWER_STATE = "d";
    private static final String UPDATE_SECOND_CONFIRM_ANSWER_STATE = "e";
    private static final String UPDATE_THIRD_CONFIRM_ANSWER_STATE = "f";

    private static final String UPDATE_FIRST_ANSWER_ERROR_TEXT = "g";
    private static final String UPDATE_SECOND_ANSWER_ERROR_TEXT = "h";
    private static final String UPDATE_THIRD_ANSWER_ERROR_TEXT = "i";
    private static final String UPDATE_FIRST_CONFIRM_ANSWER_ERROR_TEXT = "j";
    private static final String UPDATE_SECOND_CONFIRM_ANSWER_ERROR_TEXT = "k";
    private static final String UPDATE_THIRD_CONFIRM_ANSWER_ERROR_TEXT = "l";

    private static final String UPDATE_FIRST_ANSWER_ERROR_STATE = "m";
    private static final String UPDATE_SECOND_ANSWER_ERROR_STATE = "n";
    private static final String UPDATE_THIRD_ANSWER_ERROR_STATE = "o";
    private static final String UPDATE_FIRST_CONFIRM_ANSWER_ERROR_STATE = "p";
    private static final String UPDATE_SECOND_CONFIRM_ANSWER_ERROR_STATE = "q";
    private static final String UPDATE_THIRD_CONFIRM_ANSWER_ERROR_STATE = "r";

    protected final int SPINNER_ERROR_APPEARANCE = R.drawable.card_spinner_invalid_holo_light;
    protected final int SPINNER_FOCUSSED_APPEARANCE = R.drawable.card_spinner_filled_holo_light;
    protected final int SPINNER_DEFAULT_APPEARANCE = R.drawable.card_spinner_normal_holo_light;

    // Error State
    private boolean answerFirstIsInError = false;
    private boolean answerSecondIsInError = false;
    private boolean answerThirdIsInError = false;
    private boolean confirmAnswerFirstIsInError = false;
    private boolean confirmAnswerSecondIsInError = false;
    private boolean confirmAnswerThirdIsInError = false;

    // Error Label Value
    private String answerFirstErrorText = "";
    private String answerSecondErrorText = "";
    private String answerThirdErrorText = "";
    private String confirmAnswerFirstErrorText = "";
    private String confirmAnswerSecondErrorText = "";
    private String confirmAnswerThirdErrorText = "";

    private EditText securityQuestion1Spinner, securityQuestion2Spinner,
            securityQuestion3Spinner;
    private LinearLayout securityBlock1, securityBlock2, securityBlock3;
    private AnswerEditText firstAnswer, secondAnswer, thirdAnswer;
    private ConfirmationAnswerEditText confirmFirstAnswer, confirmSecondAnswer,
            confirmThirdAnswer;
    private TextView firstQuestionLabel, secondQuestionLabel,
            thirdQuestionLabel;
    private Button submit;
    private RadioGroup securityRadioGroup;

    // Footer links
    private TextView privacyTerms, provideFeedback;
    // RADIO BUTTONS
    private RadioButton radioButtonOne;
    private RadioButton radioButtonTwo;
    private List<String> sQListfirst, sQListSecond, sQListThird;
    private List<String> sQListfirstId, sQListSecondId, sQListThirdID;
    private StrongAuthReviewQueAnsDetails strongAuthReviewQueAnsDetails = new StrongAuthReviewQueAnsDetails();
    // Error label
    private TextView securityQuestionOneError;
    private TextView answerOneError;
    private TextView confirmAnswerOneError;

    private TextView securityQuestionSecondError;
    private TextView answerSecondError;
    private TextView confirmAnswerSecondError;

    private TextView securityQuestionThirdError;
    private TextView answerThirdError;
    private TextView confirmAnswerThirdError;

    private TextView errorMessage;

    private Button logout;

    private String question1, question2, question3;

    /** Added for spinner question */
    private int lastselectedQuestion1, lastselectedQuestion2,
            lastselectedQuestion3;

    private Boolean isTimeout;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.strong_auth_enter_info);
        /*
         * if (android.os.Build.VERSION.SDK_INT >= 11) { ActionBar actionBar =
         * getActionBar(); actionBar.hide(); }
         */
        populateQuestions();
        intializeViews();
        setlabelValues();
        populateSpinners();
        handlingClickEvents();
        attachErrorLabelsToFields();
        setupConfirmationFields();
        setupRadioGroupListener();
        restoreState(savedInstanceState);
        PageTimeOutUtil.getInstance(StrongAuthEnterInfoActivity.this)
                .startPageTimer();
        /* 13.4 site cat tagging */
        TrackingHelper
                .trackPageView(AnalyticsPage.SETUP_ENHANCED_AUTH_NOT_POPULATED);
        /* 13.4 site cat tagging */
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        showFieldsAsPerLastState();
    }

    private void showFieldsAsPerLastState() {
        // TODO Auto-generated method stub
        if (answerFirstIsInError) {
            firstAnswer.setErrorLabelText(answerFirstErrorText);
            firstAnswer.setErrors();
        } else {
            firstAnswer.setupDefaultAppearance();
        }

        if (answerSecondIsInError) {
            secondAnswer.setErrorLabelText(answerSecondErrorText);
            secondAnswer.setErrors();
        } else {
            secondAnswer.setupDefaultAppearance();
        }

        if (answerThirdIsInError) {
            thirdAnswer.setErrorLabelText(answerThirdErrorText);
            thirdAnswer.setErrors();
        } else {
            thirdAnswer.setupDefaultAppearance();
        }

        if (confirmAnswerFirstIsInError) {
            confirmFirstAnswer.setErrorLabelText(confirmAnswerFirstErrorText);
            confirmFirstAnswer.setErrors();
        } else {
            confirmFirstAnswer.setupDefaultAppearance();
        }

        if (confirmAnswerSecondIsInError) {
            confirmFirstAnswer.setErrorLabelText(confirmAnswerSecondErrorText);
            confirmSecondAnswer.setErrors();
        } else {
            confirmSecondAnswer.setupDefaultAppearance();
        }

        if (confirmAnswerThirdIsInError) {
            confirmFirstAnswer.setErrorLabelText(confirmAnswerThirdErrorText);
            confirmThirdAnswer.setErrors();
        } else {
            confirmThirdAnswer.setupDefaultAppearance();
        }
    }

    private void restoreState(final Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (savedInstanceState != null) {

            firstAnswer.setText(savedInstanceState
                    .getString(UPDATE_FIRST_ANSWER_STATE));
            secondAnswer.setText(savedInstanceState
                    .getString(UPDATE_SECOND_ANSWER_STATE));
            thirdAnswer.setText(savedInstanceState
                    .getString(UPDATE_THIRD_ANSWER_STATE));
            confirmFirstAnswer.setText(savedInstanceState
                    .getString(UPDATE_FIRST_CONFIRM_ANSWER_STATE));
            confirmSecondAnswer.setText(savedInstanceState
                    .getString(UPDATE_SECOND_CONFIRM_ANSWER_STATE));
            confirmThirdAnswer.setText(savedInstanceState
                    .getString(UPDATE_THIRD_CONFIRM_ANSWER_STATE));

            answerFirstIsInError = savedInstanceState.getBoolean(
                    UPDATE_FIRST_ANSWER_ERROR_STATE, false);
            answerSecondIsInError = savedInstanceState.getBoolean(
                    UPDATE_SECOND_ANSWER_ERROR_STATE, false);
            answerThirdIsInError = savedInstanceState.getBoolean(
                    UPDATE_THIRD_ANSWER_ERROR_STATE, false);
            confirmAnswerFirstIsInError = savedInstanceState.getBoolean(
                    UPDATE_FIRST_CONFIRM_ANSWER_ERROR_STATE, false);
            confirmAnswerSecondIsInError = savedInstanceState.getBoolean(
                    UPDATE_SECOND_CONFIRM_ANSWER_ERROR_STATE, false);
            confirmAnswerThirdIsInError = savedInstanceState.getBoolean(
                    UPDATE_THIRD_CONFIRM_ANSWER_ERROR_STATE, false);

            answerFirstErrorText = savedInstanceState
                    .getString(UPDATE_FIRST_ANSWER_ERROR_TEXT);
            answerSecondErrorText = savedInstanceState
                    .getString(UPDATE_SECOND_ANSWER_ERROR_TEXT);
            answerThirdErrorText = savedInstanceState
                    .getString(UPDATE_THIRD_ANSWER_ERROR_TEXT);
            confirmAnswerFirstErrorText = savedInstanceState
                    .getString(UPDATE_FIRST_CONFIRM_ANSWER_ERROR_TEXT);
            confirmAnswerSecondErrorText = savedInstanceState
                    .getString(UPDATE_SECOND_CONFIRM_ANSWER_ERROR_TEXT);
            confirmAnswerThirdErrorText = savedInstanceState
                    .getString(UPDATE_THIRD_CONFIRM_ANSWER_ERROR_TEXT);

        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        outState.putBoolean(UPDATE_FIRST_ANSWER_ERROR_STATE,
                firstAnswer.isInErrorState);
        outState.putBoolean(UPDATE_SECOND_ANSWER_ERROR_STATE,
                secondAnswer.isInErrorState);
        outState.putBoolean(UPDATE_THIRD_ANSWER_ERROR_STATE,
                thirdAnswer.isInErrorState);
        outState.putBoolean(UPDATE_FIRST_CONFIRM_ANSWER_ERROR_STATE,
                confirmFirstAnswer.isInErrorState);
        outState.putBoolean(UPDATE_SECOND_CONFIRM_ANSWER_ERROR_STATE,
                confirmSecondAnswer.isInErrorState);
        outState.putBoolean(UPDATE_THIRD_CONFIRM_ANSWER_ERROR_STATE,
                confirmThirdAnswer.isInErrorState);

        outState.putString(UPDATE_FIRST_ANSWER_ERROR_TEXT,
                firstAnswer.getErrorLabelText());
        outState.putString(UPDATE_SECOND_ANSWER_ERROR_TEXT,
                secondAnswer.getErrorLabelText());
        outState.putString(UPDATE_THIRD_ANSWER_ERROR_TEXT,
                thirdAnswer.getErrorLabelText());
        outState.putString(UPDATE_FIRST_CONFIRM_ANSWER_ERROR_TEXT,
                confirmFirstAnswer.getErrorLabelText());
        outState.putString(UPDATE_SECOND_CONFIRM_ANSWER_ERROR_TEXT,
                confirmSecondAnswer.getErrorLabelText());
        outState.putString(UPDATE_THIRD_CONFIRM_ANSWER_ERROR_TEXT,
                confirmThirdAnswer.getErrorLabelText());

        outState.putString(UPDATE_FIRST_ANSWER_STATE, firstAnswer.getText()
                .toString());
        outState.putString(UPDATE_SECOND_ANSWER_STATE, secondAnswer.getText()
                .toString());
        outState.putString(UPDATE_THIRD_ANSWER_STATE, thirdAnswer.getText()
                .toString());
        outState.putString(UPDATE_FIRST_CONFIRM_ANSWER_STATE,
                confirmFirstAnswer.getText().toString());
        outState.putString(UPDATE_SECOND_CONFIRM_ANSWER_STATE,
                confirmSecondAnswer.getText().toString());
        outState.putString(UPDATE_THIRD_CONFIRM_ANSWER_STATE,
                confirmThirdAnswer.getText().toString());

    }

    private void attachErrorLabelsToFields() {
        // TODO Auto-generated method stub
        firstAnswer.attachErrorLabel(answerOneError);
        secondAnswer.attachErrorLabel(answerSecondError);
        thirdAnswer.attachErrorLabel(answerThirdError);

        confirmFirstAnswer.attachErrorLabel(confirmAnswerOneError);
        confirmSecondAnswer.attachErrorLabel(confirmAnswerSecondError);
        confirmThirdAnswer.attachErrorLabel(confirmAnswerThirdError);
    }

    private void setupRadioGroupListener() {
        securityRadioGroup
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    final int subCopyColor = getResources().getColor(
                            R.color.blue_grey);
                    final int fieldCopyColor = getResources().getColor(
                            R.color.field_copy);

                    @Override
                    public void onCheckedChanged(final RadioGroup group,
                            final int checkedId) {
                        // TODO Auto-generated method stub
                        final RadioButton checkedRadioButton = (RadioButton) group
                                .findViewById(checkedId);
                        if (checkedRadioButton.equals(radioButtonOne)) {
                            radioButtonOne.setTextColor(subCopyColor);
                            radioButtonTwo.setTextColor(fieldCopyColor);
                        } else {
                            radioButtonOne.setTextColor(fieldCopyColor);
                            radioButtonTwo.setTextColor(subCopyColor);
                        }
                    }

                });
    }

    private void setlabelValues() {
        // TODO Auto-generated method stub
        firstQuestionLabel.setText(R.string.sa_security_question);
        secondQuestionLabel.setText(R.string.sa_security_question_second);
        thirdQuestionLabel.setText(R.string.sa_security_question_third);

    }

    private void handlingClickEvents() {
        // TODO Auto-generated method stub
        submit.setOnClickListener(this);
        privacyTerms.setOnClickListener(this);
        provideFeedback.setOnClickListener(this);
        logout.setOnClickListener(this);
    }

    private void populateQuestions() {
        // TODO Auto-generated method stub

        final CardShareDataStore cardShareDataStoreObj = CardShareDataStore
                .getInstance(StrongAuthEnterInfoActivity.this);
        StrongAuthCreateUserDetails strongAuthCreateUserDetails = (StrongAuthCreateUserDetails) cardShareDataStoreObj
                .getValueOfAppCache(StrongAuthEnterInfoActivity.this
                        .getString(R.string.sa_question_answer_list));
        sQListfirst = new ArrayList<String>();
        sQListSecond = new ArrayList<String>();
        sQListThird = new ArrayList<String>();
        sQListfirstId = new ArrayList<String>();
        sQListSecondId = new ArrayList<String>();
        sQListThirdID = new ArrayList<String>();
        
        //13.4 Code CleanUp Changes start
        Iterator<String> listFirstFieldNames = strongAuthCreateUserDetails.saQuestion1
                .fieldNames();
        while (listFirstFieldNames.hasNext()) {
            String fieldName = listFirstFieldNames.next();
            JsonNode listFirstFieldValue = strongAuthCreateUserDetails.saQuestion1
                    .get(fieldName);
            String value = listFirstFieldValue.asText();
            sQListfirst.add(value);
            sQListfirstId.add(fieldName);
        }
        
        
        Iterator<String> listSecondFieldNames = strongAuthCreateUserDetails.saQuestion2
                .fieldNames();
        while (listSecondFieldNames.hasNext()) {
            String fieldName = listSecondFieldNames.next();
            JsonNode listSecondFieldValue = strongAuthCreateUserDetails.saQuestion2
                    .get(fieldName);
            String value = listSecondFieldValue.asText();
            sQListSecond.add(value);
            sQListSecondId.add(fieldName);
        }
        
        Iterator<String> listThirdFieldNames = strongAuthCreateUserDetails.saQuestion3
                .fieldNames();
        while (listThirdFieldNames.hasNext()) {
            String fieldName = listThirdFieldNames.next();
            JsonNode listThirdFieldValue = strongAuthCreateUserDetails.saQuestion3
                    .get(fieldName);
            String value = listThirdFieldValue.asText();
            sQListThird.add(value);
            sQListThirdID.add(fieldName);
        }
        
        //13.4 Code CleanUp Changes end
    }

    /** Added for spinner */
    AlertDialog questionsDialog;

    /**
     * To populate the list of questions for spinner number 1.
     */
    protected void OpenSecurityQuestions1() {

        Intent questionScreen = new Intent(StrongAuthEnterInfoActivity.this,
                StrongAuthQuestionList.class);
        questionScreen.putStringArrayListExtra("Questions",
                (ArrayList<String>) sQListfirst);
        questionScreen.putStringArrayListExtra("QuestionsId",
                (ArrayList<String>) sQListfirstId);
        questionScreen.putExtra("lastSelectedQuestion", lastselectedQuestion1);
        questionScreen.putExtra("Questiongroup", 1);
        startActivityForResult(questionScreen, 1);

    }

    /**
     * To populate the list of questions for spinner number 2.
     */
    protected void OpenSecurityQuestions2() {

        Intent questionScreen = new Intent(StrongAuthEnterInfoActivity.this,
                StrongAuthQuestionList.class);
        questionScreen.putStringArrayListExtra("Questions",
                (ArrayList<String>) sQListSecond);
        questionScreen.putStringArrayListExtra("QuestionsId",
                (ArrayList<String>) sQListSecondId);
        questionScreen.putExtra("lastSelectedQuestion", lastselectedQuestion2);
        questionScreen.putExtra("Questiongroup", 2);
        startActivityForResult(questionScreen, 2);

    }

    /**
     * To populate the list of questions for spinner number 3.
     */
    protected void OpenSecurityQuestions3() {

        Intent questionScreen = new Intent(StrongAuthEnterInfoActivity.this,
                StrongAuthQuestionList.class);
        questionScreen.putStringArrayListExtra("Questions",
                (ArrayList<String>) sQListThird);
        questionScreen.putStringArrayListExtra("QuestionsId",
                (ArrayList<String>) sQListThirdID);
        questionScreen.putExtra("lastSelectedQuestion", lastselectedQuestion3);
        questionScreen.putExtra("Questiongroup", 3);
        startActivityForResult(questionScreen, 3);

    }

    /** Spinner finishes */

    private void populateSpinners() {

        securityQuestion1Spinner.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                OpenSecurityQuestions1();
                securityQuestion1Spinner
                        .setBackgroundResource(SPINNER_FOCUSSED_APPEARANCE);
            }
        });

        securityQuestion2Spinner.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                OpenSecurityQuestions2();
                securityQuestion2Spinner
                        .setBackgroundResource(SPINNER_FOCUSSED_APPEARANCE);
            }
        });

        securityQuestion3Spinner.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                OpenSecurityQuestions3();
                securityQuestion3Spinner
                        .setBackgroundResource(SPINNER_FOCUSSED_APPEARANCE);
            }
        });

        securityQuestion1Spinner
                .setOnFocusChangeListener(new OnFocusChangeListener() {

                    @Override
                    public void onFocusChange(final View v,
                            final boolean hasFocus) {
                        // TODO Auto-generated method stub
                        if (hasFocus) {
                            OpenSecurityQuestions1();
                            securityQuestion1Spinner
                                    .setBackgroundResource(SPINNER_FOCUSSED_APPEARANCE);
                        } else {
                            securityQuestion1Spinner
                                    .setBackgroundResource(SPINNER_DEFAULT_APPEARANCE);
                        }
                    }
                });

        securityQuestion2Spinner
                .setOnFocusChangeListener(new OnFocusChangeListener() {

                    @Override
                    public void onFocusChange(final View v,
                            final boolean hasFocus) {
                        // TODO Auto-generated method stub
                        if (hasFocus) {
                            OpenSecurityQuestions2();
                            securityQuestion2Spinner
                                    .setBackgroundResource(SPINNER_FOCUSSED_APPEARANCE);
                        } else {
                            securityQuestion2Spinner
                                    .setBackgroundResource(SPINNER_DEFAULT_APPEARANCE);
                        }
                    }
                });

        securityQuestion3Spinner
                .setOnFocusChangeListener(new OnFocusChangeListener() {

                    @Override
                    public void onFocusChange(final View v,
                            final boolean hasFocus) {
                        // TODO Auto-generated method stub
                        if (hasFocus) {
                            OpenSecurityQuestions3();
                            securityQuestion3Spinner
                                    .setBackgroundResource(SPINNER_FOCUSSED_APPEARANCE);
                        } else {
                            securityQuestion3Spinner
                                    .setBackgroundResource(SPINNER_DEFAULT_APPEARANCE);
                        }

                    }
                });
    }

    private void intializeViews() {
        // TODO Auto-generated method stub
        errorMessage = (TextView) findViewById(R.id.strongauth_security_server_error);
        securityBlock1 = (LinearLayout) findViewById(R.id.security_block1);
        securityBlock2 = (LinearLayout) findViewById(R.id.security_block2);
        securityBlock3 = (LinearLayout) findViewById(R.id.security_block3);

        securityQuestion1Spinner = (EditText) securityBlock1
                .findViewById(R.id.account_security_question_field);
        securityQuestion2Spinner = (EditText) securityBlock2
                .findViewById(R.id.account_security_question_field);
        securityQuestion3Spinner = (EditText) securityBlock3
                .findViewById(R.id.account_security_question_field);

        firstAnswer = (AnswerEditText) securityBlock1
                .findViewById(R.id.account_security_answer_field);
        secondAnswer = (AnswerEditText) securityBlock2
                .findViewById(R.id.account_security_answer_field);
        thirdAnswer = (AnswerEditText) securityBlock3
                .findViewById(R.id.account_security_answer_field);

        confirmFirstAnswer = (ConfirmationAnswerEditText) securityBlock1
                .findViewById(R.id.account_security_confirm_answer_field);
        confirmSecondAnswer = (ConfirmationAnswerEditText) securityBlock2
                .findViewById(R.id.account_security_confirm_answer_field);
        confirmThirdAnswer = (ConfirmationAnswerEditText) securityBlock3
                .findViewById(R.id.account_security_confirm_answer_field);

        firstQuestionLabel = (TextView) securityBlock1
                .findViewById(R.id.security_question);
        secondQuestionLabel = (TextView) securityBlock2
                .findViewById(R.id.security_question);
        thirdQuestionLabel = (TextView) securityBlock3
                .findViewById(R.id.security_question);

        securityRadioGroup = (RadioGroup) findViewById(R.id.account_security_choice_radio_group);
        radioButtonOne = (RadioButton) securityRadioGroup
                .findViewById(R.id.account_security_choice_one_radio);
        radioButtonTwo = (RadioButton) securityRadioGroup
                .findViewById(R.id.account_security_choice_two_radio);

        securityQuestionOneError = (TextView) securityBlock1
                .findViewById(R.id.security_question_error_label);
        answerOneError = (TextView) securityBlock1
                .findViewById(R.id.answer_error_label);
        confirmAnswerOneError = (TextView) securityBlock1
                .findViewById(R.id.confirm_answer_error_label);

        securityQuestionSecondError = (TextView) securityBlock2
                .findViewById(R.id.security_question_error_label);
        answerSecondError = (TextView) securityBlock2
                .findViewById(R.id.answer_error_label);
        confirmAnswerSecondError = (TextView) securityBlock2
                .findViewById(R.id.confirm_answer_error_label);

        securityQuestionThirdError = (TextView) securityBlock3
                .findViewById(R.id.security_question_error_label);
        answerThirdError = (TextView) securityBlock3
                .findViewById(R.id.answer_error_label);
        confirmAnswerThirdError = (TextView) securityBlock3
                .findViewById(R.id.confirm_answer_error_label);

        submit = (Button) findViewById(R.id.account_info_submit_button);
        privacyTerms = (TextView) findViewById(R.id.privacy_terms);
        provideFeedback = (TextView) findViewById(R.id.provide_feedback_button);
        logout = (Button) findViewById(R.id.logout_button);
        
        logout.setVisibility(View.INVISIBLE);

    }

    /**
     * Attach confirm answer fields to their respective primary fields.
     */
    private void setupConfirmationFields() {
        confirmFirstAnswer.attachEditTextToMatch(firstAnswer);
        confirmSecondAnswer.attachEditTextToMatch(secondAnswer);
        confirmThirdAnswer.attachEditTextToMatch(thirdAnswer);
    }

    @Override
    public TextView getErrorLabel() {
        // TODO Auto-generated method stub
        return errorMessage;
    }

    /*
     * @Override public List<EditText> getInputFields() { // TODO Auto-generated
     * method stub return super.getInputFields(); }
     * 
     * @Override public void goBack() { // TODO Auto-generated method stub
     * super.goBack(); }
     */

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        // super.onBackPressed();
    }

    @Override
    public CardErrHandler getCardErrorHandler() {
        // TODO Auto-generated method stub
        return CardErrorUIWrapper.getInstance();
    }

    @Override
    public void onClick(final View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.account_info_submit_button) {

            validateAndUpdate();

        } else if (v.getId() == R.id.privacy_terms) {
            // FacadeFactory.getBankFacade().navToCardPrivacyTerms();
            Intent privacyTerms = new Intent(StrongAuthEnterInfoActivity.this,
                    PrivacyTermsLanding.class);
            startActivity(privacyTerms);
        } else if (v.getId() == R.id.provide_feedback_button) {
            Utils.createProvideFeedbackDialog(StrongAuthEnterInfoActivity.this,
                    "strongAuthEnroll-pg");
        } else if (v.getId() == R.id.logout_button) {
            // Changes for 13.4 start
            Utils.logoutUser(this, false);
            // Changes for 13.4 end
        }
    }

    private void validateAndUpdate() {
        // TODO Auto-generated method stub

        question1 = securityQuestion1Spinner.getText().toString();
        question2 = securityQuestion2Spinner.getText().toString();
        question3 = securityQuestion3Spinner.getText().toString();

        Boolean validForm = isEnterInfoValid();

        firstAnswer.updateAppearanceForInput();
        secondAnswer.updateAppearanceForInput();
        thirdAnswer.updateAppearanceForInput();
        confirmFirstAnswer.updateAppearanceForInput();
        confirmSecondAnswer.updateAppearanceForInput();
        confirmThirdAnswer.updateAppearanceForInput();

        if (question1.equalsIgnoreCase("")) {
            securityQuestion1Spinner
                    .setBackgroundResource(SPINNER_ERROR_APPEARANCE);
        }
        if (question2.equalsIgnoreCase("")) {
            securityQuestion2Spinner
                    .setBackgroundResource(SPINNER_ERROR_APPEARANCE);
        }
        if (question3.equalsIgnoreCase("")) {
            securityQuestion3Spinner
                    .setBackgroundResource(SPINNER_ERROR_APPEARANCE);
        }

        if (validForm) {
            /* 13.4 site cat tagging */
            TrackingHelper.trackPageView(AnalyticsPage.SETUP_ENHANCED_AUTH);
            /* 13.4 site cat tagging */

            strongAuthReviewQueAnsDetails.saAnswer1 = firstAnswer.getText()
                    .toString();
            strongAuthReviewQueAnsDetails.saAnswer2 = secondAnswer.getText()
                    .toString();
            strongAuthReviewQueAnsDetails.saAnswer3 = thirdAnswer.getText()
                    .toString();

            final int radioButtonId = securityRadioGroup
                    .getCheckedRadioButtonId();
            final View selectedButton = securityRadioGroup
                    .findViewById(radioButtonId);
            final int selectedIndex = securityRadioGroup
                    .indexOfChild(selectedButton);

            if (selectedIndex == 0) {
                strongAuthReviewQueAnsDetails.bindDevice = "true";
            } else {
                strongAuthReviewQueAnsDetails.bindDevice = "false";
            }

            StrongAuthUpdateUser authUserHandler = new StrongAuthUpdateUser(
                    StrongAuthEnterInfoActivity.this, new CardEventListener() {

                        @Override
                        public void onSuccess(final Object data) {
                            // TODO Auto-generated method stub
                            final SuccessModalConfirmationTop top = new SuccessModalConfirmationTop(
                                    StrongAuthEnterInfoActivity.this, null);
                            final ModalAlertWithOneButton modal = new ModalAlertWithOneButton(
                                    StrongAuthEnterInfoActivity.this, top, null);
                            top.getButton().setOnClickListener(
                                    new OnClickListener() {
                                        @Override
                                        public void onClick(final View v) {
                                            final CardEventListener cardEventListener = new CardEventListener() {

                                                @Override
                                                public void onSuccess(
                                                        final Object data) {
                                                    // TODO Auto-generated
                                                    // method stub
                                                    final CardShareDataStore cardShareDataStoreObj = CardShareDataStore
                                                            .getInstance(StrongAuthEnterInfoActivity.this);
                                                    final SessionCookieManager sessionCookieManagerObj = cardShareDataStoreObj
                                                            .getCookieManagerInstance();
                                                    sessionCookieManagerObj
                                                            .setCookieValues();
                                                    cardShareDataStoreObj
                                                            .addToAppCache(
                                                                    StrongAuthEnterInfoActivity.this
                                                                            .getString(R.string.account_details),
                                                                    data);
                                                    final Intent accountHomeScreen = new Intent(
                                                            StrongAuthEnterInfoActivity.this,
                                                            CardNavigationRootActivity.class);

                                                    startActivity(accountHomeScreen);
                                                    finish();
                                                }

                                                @Override
                                                public void OnError(
                                                        final Object data) {
                                                    // TODO Auto-generated
                                                    // method stub
                                                    final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                                                            StrongAuthEnterInfoActivity.this);
                                                    cardErrorResHandler
                                                            .handleCardError((CardErrorBean) data);
                                                }
                                            };
                                            Utils.updateAccountDetails(
                                                    StrongAuthEnterInfoActivity.this,
                                                    cardEventListener,
                                                    "Discover", "Loading...");
                                            modal.dismiss();
                                        }
                                    });
                            modal.show();
                            LinearLayout dialogLayout = modal.getLinearLayout();
                            dialogLayout.setPadding(0, 0, 0, 0);

                        }

                        @Override
                        public void OnError(final Object data) {
                            // TODO Auto-generated method stub
                            CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(
                                    StrongAuthEnterInfoActivity.this);
                            cardErrorResHandler
                                    .handleCardError((CardErrorBean) data);
                        }
                    });
            authUserHandler.sendRequest(strongAuthReviewQueAnsDetails);
        }
    }

    private Boolean isEnterInfoValid() {
        // TODO Auto-generated method stub

        Boolean formValid = false;

        if (firstAnswer.isValid() && secondAnswer.isValid()
                && thirdAnswer.isValid() && confirmFirstAnswer.isValid()
                && confirmSecondAnswer.isValid()
                && confirmThirdAnswer.isValid()
                && !(question1.equalsIgnoreCase(""))
                && !(question2.equalsIgnoreCase(""))
                && !(question3.equalsIgnoreCase(""))) {
            formValid = true;
        }

        return formValid;
    }

    public class SuccessModalConfirmationTop extends RelativeLayout implements
            ModalTopView {

        final RelativeLayout mainView;
        final Button done;

        public SuccessModalConfirmationTop(final Context context,
                final AttributeSet attrs) {
            super(context, attrs);
            mainView = (RelativeLayout) LayoutInflater.from(context).inflate(
                    R.layout.strongauthconfirm, null);
            /* 13.4 site cat tagging */
            TrackingHelper
                    .trackPageView(AnalyticsPage.SETUP_ENHANCED_AUTH_CONFIRMATION);
            /* 13.4 site cat tagging */
            done = (Button) mainView.findViewById(R.id.sa_done);
            addView(mainView);

        }

        public Button getButton() {
            return done;
        }

        @Override
        public void setTitle(final int resource) {
            // TODO Auto-generated method stub

        }

        @Override
        public void setTitle(final String text) {
            // TODO Auto-generated method stub

        }

        @Override
        public void setContent(final int resouce) {
            // TODO Auto-generated method stub

        }

        @Override
        public void setContent(final String content) {
            // TODO Auto-generated method stub

        }

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
    public List<EditText> getInputFields() {
        // TODO Auto-generated method stub
        final List<EditText> inputFields = new ArrayList<EditText>();
        inputFields.add(firstAnswer);
        inputFields.add(secondAnswer);
        inputFields.add(thirdAnswer);
        return inputFields;
    }

    @Override
    protected void onActivityResult(final int requestCode,
            final int resultCode, final Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (requestCode == 1) { // REQUEST_CODE
            // //STRONG_AUTH_ACTIVITY
            if (resultCode == RESULT_OK) {

                if (data != null) {
                    lastselectedQuestion1 = data.getIntExtra(
                            "lastSelectedPosition", 0);
                    securityQuestion1Spinner.setText(data
                            .getStringExtra("selectedQuestion"));
                    securityQuestion1Spinner.setTextColor(Color
                            .parseColor("#293033"));
                    strongAuthReviewQueAnsDetails.saQuestionId1 = data
                            .getStringExtra("selectedQuestionId");
                    firstAnswer.requestFocus();
                    /*
                     * StrongAuthEnterInfoActivity.this .getWindow()
                     * .setSoftInputMode(
                     * LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                     */

                    if (imm != null) {
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                                InputMethodManager.HIDE_IMPLICIT_ONLY);
                        imm.showSoftInput(firstAnswer,
                                InputMethodManager.SHOW_FORCED);
                    }
                }
            } else {
                /*
                 * StrongAuthEnterInfoActivity.this.getWindow().setSoftInputMode(
                 * LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                 */
            }

        } else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {

                if (data != null) {
                    lastselectedQuestion2 = data.getIntExtra(
                            "lastSelectedPosition", 0);
                    securityQuestion2Spinner.setText(data
                            .getStringExtra("selectedQuestion"));
                    securityQuestion2Spinner.setTextColor(Color
                            .parseColor("#293033"));
                    strongAuthReviewQueAnsDetails.saQuestionId2 = data
                            .getStringExtra("selectedQuestionId");
                    secondAnswer.requestFocus();
                    /*
                     * StrongAuthEnterInfoActivity.this .getWindow()
                     * .setSoftInputMode(
                     * LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                     */
                    if (imm != null) {
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                                InputMethodManager.HIDE_IMPLICIT_ONLY);
                        imm.showSoftInput(secondAnswer,
                                InputMethodManager.SHOW_FORCED);
                    }
                }
            } else {
                /*
                 * StrongAuthEnterInfoActivity.this.getWindow().setSoftInputMode(
                 * LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                 */
            }
        } else if (requestCode == 3) {
            if (resultCode == RESULT_OK) {

                if (data != null) {
                    lastselectedQuestion3 = data.getIntExtra(
                            "lastSelectedPosition", 0);
                    securityQuestion3Spinner.setText(data
                            .getStringExtra("selectedQuestion"));
                    securityQuestion3Spinner.setTextColor(Color
                            .parseColor("#293033"));
                    strongAuthReviewQueAnsDetails.saQuestionId3 = data
                            .getStringExtra("selectedQuestionId");
                    thirdAnswer.requestFocus();
                    /*
                     * StrongAuthEnterInfoActivity.this .getWindow()
                     * .setSoftInputMode(
                     * LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                     */
                    if (imm != null) {
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                                InputMethodManager.HIDE_IMPLICIT_ONLY);
                        imm.showSoftInput(thirdAnswer,
                                InputMethodManager.SHOW_FORCED);
                    }

                }
            } else {
                /*
                 * StrongAuthEnterInfoActivity.this.getWindow().setSoftInputMode(
                 * LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                 */
            }
        }

    }

    public void idealTimeoutLogout() {
        Utils.log("CardNavigationRootActivity", "inside logout...");
        // super.logout();
        isTimeout = true;
        Utils.logoutUser(StrongAuthEnterInfoActivity.this, isTimeout);
    }

}
